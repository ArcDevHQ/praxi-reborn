package gg.arcdev.practice.game.event.commands.map;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.game.event.game.map.impl.SpreadEventGameMap;
import gg.arcdev.practice.game.event.game.map.impl.TeamEventGameMap;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("event map create")
@CommandPermission("praxi.admin.event")
public class EventMapCreateCommand extends BaseCommand {

	@Default
	public void onCreate(Player player, String mapName, String mapType) {
		if (EventGameMap.getByName(mapName) != null) {
			player.sendMessage(CC.RED + "An event map with that name already exists.");
			return;
		}

		EventGameMap gameMap;

		if (mapType.equalsIgnoreCase("TEAM")) {
			gameMap = new TeamEventGameMap(mapName);
		} else if (mapType.equalsIgnoreCase("SPREAD")) {
			gameMap = new SpreadEventGameMap(mapName);
		} else {
			player.sendMessage(CC.RED + "That event map type is not valid. Pick either \"TEAM\" or \"SPREAD\"!");
			return;
		}

		gameMap.save();
		EventGameMap.getMaps().add(gameMap);

		player.sendMessage(CC.GREEN + "You successfully created the event map \"" + mapName + "\".");
	}
}