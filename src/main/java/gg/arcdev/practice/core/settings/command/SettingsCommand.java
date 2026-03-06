package gg.arcdev.practice.core.settings.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.core.settings.SettingsMenu;
import org.bukkit.entity.Player;

@CommandAlias("settings|options")
public class SettingsCommand extends BaseCommand {

    @Default
    public void onOpen(Player player) {
        new SettingsMenu().openMenu(player);
    }
}