package gg.arcdev.practice.game.arena.selection

import gg.arcdev.practice.Main
import gg.arcdev.practice.game.arena.cuboid.Cuboid
import gg.arcdev.practice.util.ItemBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

class Selection private constructor() {

    var point1: Location? = null
    var point2: Location? = null

    val cuboid: Cuboid
        get() = Cuboid(point1!!, point2!!)

    val isFullObject: Boolean
        get() = point1 != null && point2 != null

    fun clear() {
        point1 = null
        point2 = null
    }

    companion object {
        @JvmField
        val SELECTION_WAND: ItemStack = ItemBuilder(Material.GOLD_AXE)
            .name("&6&lSelection Wand")
            .lore(
                listOf(
                    "&eLeft-click to set position 1.",
                    "&eRight-click to set position 2."
                )
            )
            .build()

        private const val SELECTION_METADATA_KEY = "CLAIM_SELECTION"

        @JvmStatic
        fun createOrGetSelection(player: Player): Selection {
            if (player.hasMetadata(SELECTION_METADATA_KEY)) {
                return player.getMetadata(SELECTION_METADATA_KEY)[0].value() as Selection
            }

            val selection = Selection()
            player.setMetadata(SELECTION_METADATA_KEY, FixedMetadataValue(Main.getInstance(), selection))
            return selection
        }
    }
}
