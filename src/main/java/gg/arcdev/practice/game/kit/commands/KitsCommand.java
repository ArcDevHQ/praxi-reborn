package gg.arcdev.practice.game.kit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.kit.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("kits")
@CommandPermission("praxi.admin.kit")
public class KitsCommand extends BaseCommand {

	@Default
	public void onKits(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "Kits");

		for (Kit kit : Kit.getKits()) {
			sender.sendMessage(kit.getName());
		}
	}
}