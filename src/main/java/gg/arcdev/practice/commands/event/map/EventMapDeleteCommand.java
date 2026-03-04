package gg.arcdev.practice.commands.event.map;

import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.util.command.command.CPL;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "event map delete", permission = "praxi.admin.event")
public class EventMapDeleteCommand {

	public void execute(Player player, @CPL("map") EventGameMap gameMap) {
		if (gameMap == null) {
			player.sendMessage(CC.RED + "An event map with that name already exists.");
			return;
		}

		gameMap.delete();

		EventGameMap.getMaps().remove(gameMap);

		player.sendMessage(CC.GREEN + "You successfully deleted the event map \"" + gameMap.getMapName() + "\".");
	}

}
