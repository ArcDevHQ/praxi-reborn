package gg.arcdev.practice.game.event.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("event leave")
public class EventLeaveCommand extends BaseCommand {

	@Default
	public void onLeave(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT) {
			EventGame.getActiveGame().getGameLogic().onLeave(player);
		} else {
			player.sendMessage(CC.RED + "You are not in an event.");
		}
	}
}