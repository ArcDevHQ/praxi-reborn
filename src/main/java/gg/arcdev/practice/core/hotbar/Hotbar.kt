package gg.arcdev.practice.core.hotbar

import gg.arcdev.practice.Main
import gg.arcdev.practice.core.profile.Profile
import gg.arcdev.practice.core.profile.ProfileState
import gg.arcdev.practice.game.event.game.EventGame
import gg.arcdev.practice.game.event.game.EventGameState
import gg.arcdev.practice.util.ItemBuilder
import gg.arcdev.practice.util.PlayerUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.regex.Pattern

object Hotbar {

    @get:JvmStatic
    val items = mutableMapOf<HotbarItem, ItemStack>()

    @JvmStatic
    fun init() {
        val config = Main.getInstance().mainConfig.configuration

        HotbarItem.entries.forEach { item ->
            runCatching {
                val path = "HOTBAR_ITEMS.${item.name}."

                items[item] = ItemBuilder(
                    Material.valueOf(config.getString("${path}MATERIAL")!!)
                ).apply {
                    durability(config.getInt("${path}DURABILITY"))
                    name(config.getString("${path}NAME"))
                    lore(config.getStringList("${path}LORE"))
                    if(item == HotbarItem.PARTY_INFORMATION) {
                        texture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkyZDVkZjgwNWMyMzkwMjJmZTFiNDVmOTQwODgyYmY0MGI1NTk2NzE5MzdkYzcxZmJjOTZmNjMwMjUwZWJjNCJ9fX0=")
                    }
                }.build()
            }.onFailure {
                println("Failed to parse item ${item.name}")
            }
        }

        mapOf(
            HotbarItem.MAP_SELECTION to "%MAP%",
            HotbarItem.KIT_SELECTION to "%KIT%"
        ).forEach { (item, placeholder) ->

            val name = items[item]?.itemMeta?.displayName ?: return@forEach
            val split = name.split(placeholder)

            val prefix = split.getOrElse(0) { "" }
            val suffix = split.getOrElse(1) { "" }

            item.pattern = Pattern.compile("($prefix)(.*)($suffix)")
        }
    }

    @JvmStatic
    fun giveHotbarItems(player: Player) {
        val profile = Profile.getByUuid(player.uniqueId)

        val hotbar = arrayOfNulls<ItemStack>(9)

        val activeGame = EventGame.activeGame
        val activeEvent =
            activeGame?.gameState == EventGameState.WAITING_FOR_PLAYERS

        val activeRematch = profile.rematchData != null

        when (profile.state) {

            ProfileState.LOBBY -> handleLobby(profile, hotbar,
                activeRematch, activeEvent)

            ProfileState.QUEUEING ->
                hotbar[0] = items[HotbarItem.QUEUE_LEAVE]

            ProfileState.SPECTATING ->
                hotbar[0] = items[HotbarItem.SPECTATE_STOP]

            ProfileState.EVENT ->
                hotbar[8] = items[HotbarItem.EVENT_LEAVE]

            ProfileState.FIGHTING ->
                hotbar[8] = items[HotbarItem.SPECTATE_STOP]
        }

        PlayerUtil.reset(player)

        hotbar.forEachIndexed { slot, item ->
            player.inventory.setItem(slot, item)
        }

        player.updateInventory()
    }

    private fun handleLobby(
        profile: Profile,
        hotbar: Array<ItemStack?>,
        activeRematch: Boolean,
        activeEvent: Boolean
    ) {

        if (profile.party == null) {

            hotbar[0] = items[HotbarItem.QUEUE_JOIN_UNRANKED]
            hotbar[1] = items[HotbarItem.QUEUE_JOIN_RANKED]

            when {
                activeRematch && activeEvent -> {
                    hotbar[2] = rematchItem(profile)
                    hotbar[3] = items[HotbarItem.LEADERBOARD]
                    hotbar[4] = items[HotbarItem.EVENT_JOIN]
                    hotbar[5] = items[HotbarItem.PARTY_CREATE]
                }

                activeRematch -> {
                    hotbar[2] = rematchItem(profile)
                    hotbar[3] = items[HotbarItem.LEADERBOARD]
                    hotbar[4] = items[HotbarItem.PARTY_CREATE]
                }

                activeEvent -> {
                    hotbar[3] = items[HotbarItem.LEADERBOARD]
                    hotbar[4] = items[HotbarItem.EVENT_JOIN]
                    hotbar[5] = items[HotbarItem.PARTY_CREATE]
                }

                else -> {
                    hotbar[3] = items[HotbarItem.LEADERBOARD]
                    hotbar[4] = items[HotbarItem.PARTY_CREATE]
                }
            }

        } else {

            val leader = profile.party.leader.uniqueId == profile.uuid

            if (leader) {
                hotbar[2] = items[HotbarItem.PARTY_INFORMATION]
                hotbar[3] = items[HotbarItem.PARTY_EVENTS]
                hotbar[4] = items[HotbarItem.OTHER_PARTIES]
                hotbar[6] = items[HotbarItem.PARTY_DISBAND]
            } else {
                hotbar[0] = items[HotbarItem.PARTY_INFORMATION]
                hotbar[3] = items[HotbarItem.OTHER_PARTIES]
                hotbar[5] = items[HotbarItem.PARTY_LEAVE]
            }
        }

        hotbar[5] = items[HotbarItem.KIT_EDITOR]

        hotbar[8] = items[HotbarItem.SETTINGS]
    }

    private fun rematchItem(profile: Profile): ItemStack? {
        return if (profile.rematchData?.isReceive == true)
            items[HotbarItem.REMATCH_ACCEPT]
        else
            items[HotbarItem.REMATCH_REQUEST]
    }

    @JvmStatic
    fun fromItemStack(stack: ItemStack?): HotbarItem? =
        items.entries.firstOrNull { it.value == stack }?.key
}
