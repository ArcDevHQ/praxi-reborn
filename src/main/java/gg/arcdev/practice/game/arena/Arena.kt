package gg.arcdev.practice.game.arena

import gg.arcdev.practice.Main
import gg.arcdev.practice.game.arena.cuboid.Cuboid
import gg.arcdev.practice.game.arena.impl.SharedArena
import gg.arcdev.practice.game.arena.impl.StandaloneArena
import gg.arcdev.practice.game.kit.Kit
import gg.arcdev.practice.util.LocationUtil
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import java.util.concurrent.ThreadLocalRandom

open class Arena(
    val name: String,
    location1: Location,
    location2: Location
) : Cuboid(location1, location2) {

    var spawnA: Location? = null
        get() = field?.clone()

    var spawnB: Location? = null
        get() = field?.clone()

    var active: Boolean = false
        set(value) {
            if (getType() != ArenaType.SHARED) {
                field = value
            }
        }

    var kits: MutableList<String> = mutableListOf()

    open fun getType(): ArenaType = ArenaType.DUPLICATE

    fun isSetup(): Boolean = lowerCorner != null && upperCorner != null && spawnA != null && spawnB != null

    fun isActive(): Boolean = active

    fun getMaxBuildHeight(): Int {
        val first = spawnA ?: return 0
        val second = spawnB ?: return 0
        return maxOf(first.y, second.y).toInt() + 5
    }

    open fun save() {}

    open fun delete() {
        arenas.remove(this)
    }

    companion object {
        private val arenas: MutableList<Arena> = mutableListOf()

        @JvmStatic
        fun getArenas(): MutableList<Arena> = arenas

        @JvmStatic
        fun init() {
            val configuration: FileConfiguration = Main.getInstance().arenasConfig.configuration
            val section = configuration.getConfigurationSection("arenas") ?: run {
                Main.getInstance().logger.info("Loaded 0 arenas")
                return
            }

            for (arenaName in section.getKeys(false)) {
                val path = "arenas.$arenaName"
                val arenaType = ArenaType.valueOf(configuration.getString("$path.type") ?: continue)
                val serializedLocation1 = configuration.getString("$path.cuboid.location1")
                val serializedLocation2 = configuration.getString("$path.cuboid.location2")
                val location1 = LocationUtil.deserialize(serializedLocation1)
                val location2 = LocationUtil.deserialize(serializedLocation2)

                if (location1 == null || location2 == null) {
                    Main.getInstance().logger.warning(
                        "Skipping arena \"$arenaName\" because one or more cuboid worlds could not be resolved. " +
                            "location1=$serializedLocation1, location2=$serializedLocation2"
                    )
                    continue
                }

                val arena = when (arenaType) {
                    ArenaType.STANDALONE -> StandaloneArena(arenaName, location1, location2)
                    ArenaType.SHARED -> SharedArena(arenaName, location1, location2)
                    ArenaType.DUPLICATE -> continue
                }

                configuration.getString("$path.spawnA")?.let { arena.spawnA = LocationUtil.deserialize(it) }
                configuration.getString("$path.spawnB")?.let { arena.spawnB = LocationUtil.deserialize(it) }
                arena.kits.addAll(configuration.getStringList("$path.kits"))

                if (arena is StandaloneArena) {
                    val duplicatesSection = configuration.getConfigurationSection("$path.duplicates")
                    if (duplicatesSection != null) {
                        for (duplicateId in duplicatesSection.getKeys(false)) {
                            val duplicatePath = "$path.duplicates.$duplicateId"
                            val duplicateLocation1 = LocationUtil.deserialize(configuration.getString("$duplicatePath.cuboid.location1")) ?: continue
                            val duplicateLocation2 = LocationUtil.deserialize(configuration.getString("$duplicatePath.cuboid.location2")) ?: continue

                            val duplicate = Arena(arenaName, duplicateLocation1, duplicateLocation2)
                            duplicate.spawnA = LocationUtil.deserialize(configuration.getString("$duplicatePath.spawnA"))
                            duplicate.spawnB = LocationUtil.deserialize(configuration.getString("$duplicatePath.spawnB"))
                            duplicate.kits = arena.kits.toMutableList()

                            arena.duplicates.add(duplicate)
                            arenas.add(duplicate)
                        }
                    }
                }

                arenas.add(arena)
            }

            Main.getInstance().logger.info("Loaded ${arenas.size} arenas")
        }

        @JvmStatic
        fun getByName(name: String): Arena? {
            for (arena in arenas) {
                if (arena.getType() != ArenaType.DUPLICATE && arena.name.equals(name, ignoreCase = true)) {
                    return arena
                }
            }

            return null
        }

        @JvmStatic
        fun getRandomArena(kit: Kit): Arena? {
            val available = mutableListOf<Arena>()

            for (arena in arenas) {
                if (!arena.isSetup()) {
                    continue
                }

                if (!arena.kits.contains(kit.getName())) {
                    continue
                }

                if (kit.getGameRules().isBuild() && !arena.active &&
                    (arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE)
                ) {
                    available.add(arena)
                } else if (!kit.getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                    available.add(arena)
                }
            }

            if (available.isEmpty()) {
                return null
            }

            return available[ThreadLocalRandom.current().nextInt(available.size)]
        }
    }
}
