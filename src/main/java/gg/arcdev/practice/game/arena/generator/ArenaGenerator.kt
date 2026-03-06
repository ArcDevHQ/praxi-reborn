package gg.arcdev.practice.game.arena.generator

import com.boydti.fawe.util.TaskManager
import gg.arcdev.practice.Main
import gg.arcdev.practice.game.arena.Arena
import gg.arcdev.practice.game.arena.ArenaType
import gg.arcdev.practice.game.arena.Schematic
import gg.arcdev.practice.game.arena.impl.SharedArena
import gg.arcdev.practice.game.arena.impl.StandaloneArena
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.util.concurrent.ThreadLocalRandom

class ArenaGenerator(
    private val name: String,
    private val world: World,
    private val schematic: Schematic,
    private val type: ArenaType
) {

    fun generate(file: File, parentArena: StandaloneArena?) {
        log("Generating ${type.name} $name arena...")

        val clipboard = schematic.clipBoard ?: return
        var range = 500
        var attempts = 0
        var preciseX = ThreadLocalRandom.current().nextInt(range)
        var preciseZ = ThreadLocalRandom.current().nextInt(range)

        if (ThreadLocalRandom.current().nextBoolean()) {
            preciseX = -preciseX
        }

        if (ThreadLocalRandom.current().nextBoolean()) {
            preciseZ = -preciseZ
        }

        top@ while (true) {
            attempts++

            if (attempts >= 5) {
                preciseX = ThreadLocalRandom.current().nextInt(range)
                preciseZ = ThreadLocalRandom.current().nextInt(range)

                if (ThreadLocalRandom.current().nextBoolean()) {
                    preciseX = -preciseX
                }

                if (ThreadLocalRandom.current().nextBoolean()) {
                    preciseZ = -preciseZ
                }

                range += 500
                attempts = 0
                log("Increased range to: $range")
            }

            val minX = preciseX - clipboard.width - ARENA_PADDING
            val maxX = preciseX + clipboard.width + ARENA_PADDING
            val minZ = preciseZ - clipboard.length - ARENA_PADDING
            val maxZ = preciseZ + clipboard.length + ARENA_PADDING
            val minY = PASTE_Y
            val maxY = PASTE_Y + clipboard.height + 10

            for (x in minX until maxX) {
                for (z in minZ until maxZ) {
                    for (y in minY until maxY) {
                        if (world.getBlockAt(x, y, z).type != Material.AIR) {
                            continue@top
                        }
                    }
                }
            }

            val minCorner = Location(world, minX.toDouble(), minY.toDouble(), minZ.toDouble())
            val maxCorner = Location(world, maxX.toDouble(), maxY.toDouble(), maxZ.toDouble())
            val finalPreciseX = preciseX
            val finalPreciseZ = preciseZ

            TaskManager.IMP.async {
                try {
                    Schematic(file).pasteSchematic(world, finalPreciseX, PASTE_Y, finalPreciseZ)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val arena = when (type) {
                    ArenaType.STANDALONE -> StandaloneArena(name, minCorner, maxCorner)
                    ArenaType.DUPLICATE -> {
                        val duplicate = Arena(name, minCorner, maxCorner)
                        parentArena?.duplicates?.add(duplicate)
                        duplicate
                    }

                    ArenaType.SHARED -> SharedArena(name, minCorner, maxCorner)
                }

                helper@ for (x in minX until maxX) {
                    for (z in minZ until maxZ) {
                        for (y in minY until maxY) {
                            if (world.getBlockAt(x, y, z).type != Material.SPONGE) {
                                continue
                            }

                            val origin = world.getBlockAt(x, y, z)
                            val up = origin.getRelative(BlockFace.UP, 1)
                            val state = up.state

                            if (state !is Sign) {
                                continue
                            }

                            if (state.getLine(0).isEmpty() || state.getLine(1).isEmpty()) {
                                continue
                            }

                            val pitch = state.getLine(0).toFloat()
                            val yaw = state.getLine(1).toFloat()
                            val loc = Location(origin.world, origin.x.toDouble(), origin.y.toDouble(), origin.z.toDouble(), yaw, pitch)

                            object : BukkitRunnable() {
                                override fun run() {
                                    up.type = Material.AIR
                                    origin.type = origin.getRelative(BlockFace.NORTH).type
                                }
                            }.runTask(Main.getInstance())

                            if (arena.spawnA == null) {
                                arena.spawnA = loc
                            } else if (arena.spawnB == null) {
                                arena.spawnB = loc
                                break@helper
                            }
                        }
                    }
                }

                Arena.getArenas().add(arena)

                if (type == ArenaType.DUPLICATE && parentArena != null) {
                    parentArena.save()
                } else {
                    arena.save()
                }

                if (type == ArenaType.STANDALONE) {
                    repeat(5) {
                        object : BukkitRunnable() {
                            override fun run() {
                                ArenaGenerator(name, world, schematic, ArenaType.DUPLICATE)
                                    .generate(file, arena as StandaloneArena)
                            }
                        }.runTask(Main.getInstance())
                    }
                }
            }

            log("Pasted schematic at $preciseX, $PASTE_Y, $preciseZ")
            break
        }
    }

    private fun log(message: String) {
        Main.getInstance().logger.info("[ArenaGen] $message")
    }

    companion object {
        private const val PASTE_Y = 120
        private const val ARENA_PADDING = 40
    }
}
