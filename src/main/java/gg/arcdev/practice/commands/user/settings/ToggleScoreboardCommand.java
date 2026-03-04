package gg.arcdev.practice.commands.user.settings;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "togglescoreboard", "tsb" })
public class ToggleScoreboardCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getOptions().showScoreboard(!profile.getOptions().showScoreboard());

        if (profile.getOptions().showScoreboard()) {
            player.sendMessage(Locale.OPTIONS_SCOREBOARD_ENABLED.format());
        } else {
            player.sendMessage(Locale.OPTIONS_SCOREBOARD_DISABLED.format());
        }
    }

}
