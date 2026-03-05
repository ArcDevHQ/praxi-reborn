package gg.arcdev.practice.game.event.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.event.game.EventGameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("event forcestart")
@CommandPermission("praxi.admin.event")
public class EventForceStartCommand extends BaseCommand {

	@Default
	public void onForceStart(Player player) {
		if (EventGame.getActiveGame() != null) {
			EventGame game = EventGame.getActiveGame();

			if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
					game.getGameState() == EventGameState.STARTING_EVENT) {
				game.getGameLogic().startEvent();
				game.getGameLogic().preStartRound();
				game.setGameState(EventGameState.STARTING_ROUND);
				game.getGameLogic().getGameLogicTask().setNextAction(4);
			} else {
				player.sendMessage(ChatColor.RED + "The event has already started.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "There is no active event.");
		}
	}
}