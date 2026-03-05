package gg.arcdev.practice.commands.user.settings;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import org.bukkit.entity.Player;

@CommandAlias("togglescoreboard|tsb")
public class ToggleScoreboardCommand extends BaseCommand {

    @Default
    public void onToggle(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        boolean newState = !profile.getOptions().showScoreboard();
        profile.getOptions().showScoreboard(newState);

        if (newState) {
            player.sendMessage(Locale.OPTIONS_SCOREBOARD_ENABLED.format());
        } else {
            player.sendMessage(Locale.OPTIONS_SCOREBOARD_DISABLED.format());
        }
    }
}