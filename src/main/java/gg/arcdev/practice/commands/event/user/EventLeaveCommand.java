package gg.arcdev.practice.commands.event.user;

import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event leave")
public class EventLeaveCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT) {
			EventGame.getActiveGame().getGameLogic().onLeave(player);
		} else {
			player.sendMessage(CC.RED + "You are not in an event.");
		}
	}

}
