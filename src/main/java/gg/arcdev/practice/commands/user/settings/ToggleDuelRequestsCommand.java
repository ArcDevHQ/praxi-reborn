package gg.arcdev.practice.commands.user.settings;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "toggleduels", "tgr", "tgd" })
public class ToggleDuelRequestsCommand {

    public void execute(Player player) {
        Profile profile = Profile.getByUuid(player.getUniqueId());
        profile.getOptions().receiveDuelRequests(!profile.getOptions().receiveDuelRequests());

        if (profile.getOptions().receiveDuelRequests()) {
            player.sendMessage(Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_ENABLED.format());
        } else {
            player.sendMessage(Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_DISABLED.format());
        }
    }

}
