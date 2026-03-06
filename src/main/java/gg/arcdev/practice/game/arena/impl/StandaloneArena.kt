package gg.arcdev.practice.game.arena.impl

import gg.arcdev.practice.Main
import gg.arcdev.practice.game.arena.Arena
import gg.arcdev.practice.game.arena.ArenaType
import gg.arcdev.practice.util.LocationUtil
import org.bukkit.Location
import java.io.IOException

class StandaloneArena(name: String, location1: Location, location2: Location) : Arena(name, location1, location2) {

    val duplicates: MutableList<Arena> = mutableListOf()

    override fun getType(): ArenaType = ArenaType.STANDALONE

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

        for ((index, duplicate) in duplicates.withIndex()) {
            val duplicatePath = "$path.duplicates.${index + 1}"
            configuration.set("$duplicatePath.cuboid.location1", LocationUtil.serialize(duplicate.lowerCorner))
            configuration.set("$duplicatePath.cuboid.location2", LocationUtil.serialize(duplicate.upperCorner))
            configuration.set("$duplicatePath.spawnA", LocationUtil.serialize(duplicate.spawnA))
            configuration.set("$duplicatePath.spawnB", LocationUtil.serialize(duplicate.spawnB))
        }

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
