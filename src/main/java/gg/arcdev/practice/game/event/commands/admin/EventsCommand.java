package gg.arcdev.practice.game.event.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("events")
@CommandPermission("praxi.event.host")
public class EventsCommand extends BaseCommand {

	@Default
	public void onEvents(Player player) {
		player.sendMessage("WIP");
	}
}