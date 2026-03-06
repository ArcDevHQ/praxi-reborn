package gg.arcdev.practice.core.settings.buttons;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.settings.SettingsMenu;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ToggleDuelRequestsButton extends Button {

    private final Profile profile;

    public ToggleDuelRequestsButton(Profile profile) {
        this.profile = profile;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        boolean enabled = profile.getOptions().receiveDuelRequests();

        List<String> lore = new ArrayList<>();
        lore.add("&fClick to receive &bDuel Requests");
        lore.add("&7Status: " + (enabled ? "&aEnabled" : "&cDisabled"));
        lore.add("");
        lore.addAll(enabled
                ? Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_ENABLED.formatLines()
                : Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_DISABLED.formatLines()
        );

        return new ItemBuilder(Material.DIAMOND_SWORD)
                .name("&bDuel Requests")
                .lore(lore)
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        boolean newState = !profile.getOptions().receiveDuelRequests();
        profile.getOptions().receiveDuelRequests(newState);

        player.sendMessage(newState
                ? Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_ENABLED.format()
                : Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_DISABLED.format()
        );

        new SettingsMenu().openMenu(player);
    }
}