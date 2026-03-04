package gg.arcdev.practice.commands.event.vote;

import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.util.command.command.CPL;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "event map vote")
public class EventMapVoteCommand {

	public void execute(Player player, @CPL("map") EventGameMap gameMap) {
		if (gameMap == null) {
			player.sendMessage(ChatColor.RED + "You cannot vote for a map that doesn't exist!");
			return;
		}

		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT && EventGame.getActiveGame() != null) {
			if (profile.getVoteCooldown().hasExpired()) {
				profile.setVoteCooldown(new Cooldown(5000));
				EventGame.getActiveGame().getGameLogic().onVote(player, gameMap);
			} else {
				player.sendMessage(ChatColor.RED + "You can vote in another " +
						profile.getVoteCooldown().getTimeLeft() + ".");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You are not in an event.");
		}
	}

}
