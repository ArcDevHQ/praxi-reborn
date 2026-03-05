package gg.arcdev.practice.game.event.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.Event;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("event set lobby")
@CommandPermission("praxi.admin.event")
public class EventSetLobbyCommand extends BaseCommand {

	@Default
	public void onSetLobby(Player player, Event event) {
		if (event != null) {
			event.setLobbyLocation(player.getLocation());
			event.save();

			player.sendMessage(ChatColor.GOLD + "You updated the " + ChatColor.GREEN + event.getDisplayName() +
					ChatColor.GOLD + " Event's lobby location.");
		} else {
			player.sendMessage(ChatColor.RED + "An event with that name does not exist.");
		}
	}
}