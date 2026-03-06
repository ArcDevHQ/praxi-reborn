package gg.arcdev.practice.game.arena.impl

import gg.arcdev.practice.Main
import gg.arcdev.practice.game.arena.Arena
import gg.arcdev.practice.game.arena.ArenaType
import gg.arcdev.practice.util.LocationUtil
import org.bukkit.Location
import java.io.IOException

class SharedArena(name: String, location1: Location, location2: Location) : Arena(name, location1, location2) {

    override fun getType(): ArenaType = ArenaType.SHARED

    override fun save() {
        val path = "arenas.$name"
        val configuration = Main.getInstance().getArenasConfig().configuration

        configuration.set(path, null)
        configuration.set("$path.type", getType().name)
        configuration.set("$path.spawnA", LocationUtil.serialize(spawnA))
        configuration.set("$path.spawnB", LocationUtil.serialize(spawnB))
        configuration.set("$path.cuboid.location1", LocationUtil.serialize(lowerCorner))
        configuration.set("$path.cuboid.location2", LocationUtil.serialize(upperCorner))
        configuration.set("$path.kits", kits)

        try {
            configuration.save(Main.getInstance().getArenasConfig().file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun delete() {
        super.delete()

        val configuration = Main.getInstance().getArenasConfig().configuration
        configuration.set("arenas.$name", null)

        try {
            configuration.save(Main.getInstance().getArenasConfig().file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
