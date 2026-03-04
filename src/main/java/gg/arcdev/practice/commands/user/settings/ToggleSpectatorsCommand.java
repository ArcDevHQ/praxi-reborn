package gg.arcdev.practice.commands.user.settings;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "togglespectators", "togglespecs", "tgs" })
public class ToggleSpectatorsCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getOptions().allowSpectators(!profile.getOptions().allowSpectators());

        if (profile.getOptions().allowSpectators()) {
            player.sendMessage(Locale.OPTIONS_SPECTATORS_ENABLED.format());
        } else {
            player.sendMessage(Locale.OPTIONS_SPECTATORS_DISABLED.format());
        }
    }

}
