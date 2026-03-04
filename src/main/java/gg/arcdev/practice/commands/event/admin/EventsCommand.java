package gg.arcdev.practice.commands.event.admin;

import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "events", permission = "praxi.event.host")
public class EventsCommand {

	public void execute(Player player) {
		player.sendMessage("WIP");
	}

}
