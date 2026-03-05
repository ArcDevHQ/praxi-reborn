package gg.arcdev.practice.game.event.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.EventGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("event cancel")
@CommandPermission("praxi.admin.event")
public class EventCancelCommand extends BaseCommand {

	@Default
	public void onCancel(CommandSender sender) {
		if (EventGame.getActiveGame() != null) {
			EventGame.getActiveGame().getGameLogic().cancelEvent();
		} else {
			sender.sendMessage(ChatColor.RED + "There is no active event.");
		}
	}
}