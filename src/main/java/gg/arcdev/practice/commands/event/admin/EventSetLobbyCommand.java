package gg.arcdev.practice.commands.event.admin;

import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandMeta(label = "event set lobby", permission = "praxi.admin.event")
public class EventSetLobbyCommand {

	public void execute(Player player, Event event) {
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
