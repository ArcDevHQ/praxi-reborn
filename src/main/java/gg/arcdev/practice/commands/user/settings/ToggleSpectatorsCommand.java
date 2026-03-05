package gg.arcdev.practice.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import org.bukkit.entity.Player;

@CommandAlias("togglespectators|togglespecs|tgs")
public class ToggleSpectatorsCommand extends BaseCommand {

    @Default
    public void onToggle(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean newState = !profile.getOptions().allowSpectators();
        profile.getOptions().allowSpectators(newState);

        if (newState) {
            player.sendMessage(Locale.OPTIONS_SPECTATORS_ENABLED.format());
        } else {
            player.sendMessage(Locale.OPTIONS_SPECTATORS_DISABLED.format());
        }
    }
}