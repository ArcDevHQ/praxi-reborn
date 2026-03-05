package gg.arcdev.practice.game.event.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("event|event help")
public class EventHelpCommand extends BaseCommand {

	@Default
	public void onHelp(Player player) {
		for (String line : Main.get().getMainConfig().getStringList("EVENT.HELP")) {
			player.sendMessage(CC.translate(line));
		}
	}
}