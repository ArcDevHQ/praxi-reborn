package gg.arcdev.practice.core.settings.buttons;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToggleScoreboardButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean enabled = profile.getOptions().showScoreboard();

        List<String> lore = new ArrayList<>();
        lore.add("&fClick to toggle &bScoreboard");
        lore.add("&7Status: " + (enabled ? "&aEnabled" : "&cDisabled"));
        lore.add("");
        lore.addAll(enabled
                ? Locale.OPTIONS_SCOREBOARD_ENABLED.formatLines()
                : Locale.OPTIONS_SCOREBOARD_DISABLED.formatLines()
        );

        return new ItemBuilder(Material.ITEM_FRAME)
                .name("&bScoreboard")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean newState = !profile.getOptions().showScoreboard();
        profile.getOptions().showScoreboard(newState);

        player.sendMessage(newState
                ? Locale.OPTIONS_SCOREBOARD_ENABLED.format()
                : Locale.OPTIONS_SCOREBOARD_DISABLED.format()
        );

        player.updateInventory();
    }
}