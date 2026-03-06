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

public class ToggleSpectatorsButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean enabled = profile.getOptions().allowSpectators();

        List<String> lore = new ArrayList<>();
        lore.add("&fClick to toggle &bSpectators");
        lore.add("&7Status: " + (enabled ? "&aEnabled" : "&cDisabled"));
        lore.add("");
        lore.addAll(enabled
                ? Locale.OPTIONS_SPECTATORS_ENABLED.formatLines()
                : Locale.OPTIONS_SPECTATORS_DISABLED.formatLines()
        );

        return new ItemBuilder(Material.FEATHER)
                .name("&bSpectators")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean newState = !profile.getOptions().allowSpectators();
        profile.getOptions().allowSpectators(newState);

        player.sendMessage(newState
                ? Locale.OPTIONS_SPECTATORS_ENABLED.format()
                : Locale.OPTIONS_SPECTATORS_DISABLED.format()
        );

        player.updateInventory();
    }
}