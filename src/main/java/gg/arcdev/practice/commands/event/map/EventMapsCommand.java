package gg.arcdev.practice.commands.event.map;

import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event maps", permission = "praxi.admin.event")
public class EventMapsCommand {

	public void execute(Player player) {
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
