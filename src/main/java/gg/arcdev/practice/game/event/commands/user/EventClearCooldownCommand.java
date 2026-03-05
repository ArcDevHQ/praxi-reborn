package gg.arcdev.practice.game.event.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.util.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("event clearcooldown|event clearcd")
@CommandPermission("praxi.admin.event")
public class EventClearCooldownCommand extends BaseCommand {

	@Default
	public void onClearCooldown(CommandSender sender) {
		EventGame.setCooldown(new Cooldown(0));
		sender.sendMessage(ChatColor.GREEN + "You cleared the event cooldown.");
	}
}