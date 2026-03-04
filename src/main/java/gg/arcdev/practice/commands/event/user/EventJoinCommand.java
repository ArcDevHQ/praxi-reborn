package gg.arcdev.practice.commands.event.user;

import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.event.game.EventGameState;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event join")
public class EventJoinCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the event while in a party.");
			return;
		}

		if (profile.isBusy()) {
			player.sendMessage(CC.RED + "You must be in the lobby to join the event.");
		} else {
			EventGame game = EventGame.getActiveGame();

			if (game != null) {
				if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
				    game.getGameState() == EventGameState.STARTING_EVENT) {
					if (game.getParticipants().size() < game.getMaximumPlayers()) {
						game.getGameLogic().onJoin(player);
					} else {
						player.sendMessage(CC.RED + "The event is full.");
					}
				} else {
					player.sendMessage(CC.RED + "The event has already started.");
				}
			} else {
				player.sendMessage(CC.RED + "There is no active event.");
			}
		}
	}

}
