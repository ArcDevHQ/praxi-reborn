package gg.arcdev.practice.commands.event.user;

import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandMeta(label = { "event clearcooldown", "event clearcd" }, permission = "praxi.admin.event")
public class EventClearCooldownCommand {

	public void execute(CommandSender sender) {
		EventGame.setCooldown(new Cooldown(0));
		sender.sendMessage(ChatColor.GREEN + "You cleared the event cooldown.");
	}

}
