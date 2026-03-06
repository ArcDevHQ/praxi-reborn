package gg.arcdev.practice.game.arena

import com.sk89q.worldedit.CuboidClipboard
import com.sk89q.worldedit.EditSession
import com.sk89q.worldedit.MaxChangedBlocksException
import com.sk89q.worldedit.Vector
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.schematic.SchematicFormat
import com.sk89q.worldedit.world.DataException
import org.bukkit.World
import java.io.File

class Schematic(file: File) {

    val clipBoard: CuboidClipboard?

    init {
        val format = SchematicFormat.MCEDIT
        var loadedClipboard: CuboidClipboard? = null

        try {
            loadedClipboard = format.load(file)
            loadedClipboard?.origin = Vector.ZERO
            loadedClipboard?.offset = Vector.ZERO
        } catch (e: DataException) {
            e.printStackTrace()
        }

        clipBoard = loadedClipboard
    }

    fun pasteSchematic(world: World, x: Int, y: Int, z: Int) {
        val clipboard = clipBoard ?: return
        val pastePos = Vector(x, y, z)
        val editSession = EditSession(BukkitWorld(world), 999999)

        try {
            clipboard.place(editSession, pastePos, true)
        } catch (e: MaxChangedBlocksException) {
            e.printStackTrace()
        }
    }
}