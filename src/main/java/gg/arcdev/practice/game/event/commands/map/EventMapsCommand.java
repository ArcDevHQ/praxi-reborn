package gg.arcdev.practice.game.event.commands.map;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("event maps")
@CommandPermission("praxi.admin.event")
public class EventMapsCommand extends BaseCommand {

	@Default
	public void onMaps(Player player) {
		player.sendMessage(CC.GOLD + CC.BOLD + "Event Maps");

		if (EventGameMap.getMaps().isEmpty()) {
			player.sendMessage(CC.GRAY + "There are no event maps.");
		} else {
			for (EventGameMap gameMap : EventGameMap.getMaps()) {
				player.sendMessage(" - " + (gameMap.isSetup() ? CC.GREEN : CC.RED) + gameMap.getMapName());
			}
		}
	}
}