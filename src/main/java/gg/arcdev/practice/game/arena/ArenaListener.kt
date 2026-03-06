package gg.arcdev.practice.game.arena

import gg.arcdev.practice.game.arena.selection.Selection
import gg.arcdev.practice.game.match.Match
import gg.arcdev.practice.game.match.MatchState
import gg.arcdev.practice.util.CC
import org.bukkit.Difficulty
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.WorldLoadEvent

class ArenaListener : Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (!(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return
        }

        val item = event.getItem()

        if (item != null && item == Selection.SELECTION_WAND) {
            val player = event.getPlayer()
            val clicked = event.clickedBlock
            var location = 0

            val selection = Selection.createOrGetSelection(player)

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                selection.point2 = clicked.location
                location = 2
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                selection.point1 = clicked.location
                location = 1
            }

            event.isCancelled = true
            event.setUseItemInHand(Event.Result.DENY)
            event.setUseInteractedBlock(Event.Result.DENY)

            var message = CC.AQUA + (if (location == 1) "First" else "Second") +
                    " location " + CC.YELLOW + "(" + CC.GREEN +
                    clicked.x + CC.YELLOW + ", " + CC.GREEN +
                    clicked.y + CC.YELLOW + ", " + CC.GREEN +
                    clicked.z + CC.YELLOW + ")" + CC.AQUA + " has been set!"

            if (selection.isFullObject) {
                message += CC.RED + " (" + CC.YELLOW + selection.cuboid.volume() + CC.AQUA + " blocks" +
                        CC.RED + ")"
            }

            player.sendMessage(message)
        }
    }

    @EventHandler
    fun onBlockFromTo(event: BlockFromToEvent) {
        val x = event.getBlock().x
        val y = event.getBlock().y
        val z = event.getBlock().z

        var foundArena: Arena? = null

        for (arena in Arena.getArenas()) {
            if (!(arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE)) {
                continue
            }

            if (!arena.active) {
                continue
            }

            if (x >= arena.lowerX && x <= arena.upperX &&
                y >= arena.lowerY && y <= arena.upperY &&
                z >= arena.lowerZ && z <= arena.upperZ
            ) {
                foundArena = arena
                break
            }
        }

        if (foundArena == null) {
            return
        }

        for (match in Match.getMatches()) {
            if (match.getArena() == foundArena) {
                if (match.getState() == MatchState.PLAYING_ROUND) {
                    match.placedBlocks.add(event.toBlock.location)
                }

                break
            }
        }
    }

    @EventHandler
    fun onCreatureSpawnEvent(event: CreatureSpawnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onWorldLoad(event: WorldLoadEvent) {
        event.world.entities.clear()
        event.world.difficulty = Difficulty.HARD
    }

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockIgnite(event: BlockIgniteEvent) {
        if (event.cause == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onHangingBreak(event: HangingBreakEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockBurn(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockSpread(event: BlockSpreadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPrime(event: ExplosionPrimeEvent) {
        event.isCancelled = true
    }
}
