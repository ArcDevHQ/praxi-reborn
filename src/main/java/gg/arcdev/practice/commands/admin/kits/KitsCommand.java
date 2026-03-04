package gg.arcdev.practice.commands.admin.kits;

import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = "kits", permission = "praxi.admin.kit")
public class KitsCommand {

	public void execute(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "Kits");

		for (Kit kit : Kit.getKits()) {
			sender.sendMessage(kit.getName());
		}
	}

}
