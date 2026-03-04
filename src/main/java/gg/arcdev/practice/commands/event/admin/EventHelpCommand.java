package gg.arcdev.practice.commands.event.admin;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "event", "event help" })
public class EventHelpCommand {

	public void execute(Player player) {
		for (String line : Main.get().getMainConfig().getStringList("EVENT.HELP")) {
			player.sendMessage(CC.translate(line));
		}
	}

}
