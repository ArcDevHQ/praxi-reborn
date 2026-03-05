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

@CommandAlias("event map set spawn")
@CommandPermission("praxi.admin.event")
public class EventMapSetSpawnCommand extends BaseCommand {

	@Default
	public void onSetSpawn(Player player, EventGameMap map, String field) {
		if (map == null) {
			player.sendMessage(CC.RED + "An event map with that name does not exist.");
			return;
		}

		switch (field.toLowerCase()) {
			case "spectator":
				map.setSpectatorPoint(player.getLocation());
				player.sendMessage(CC.GREEN + "You successfully updated " +
						map.getMapName() + "'s " + field + " location.");
				break;

			case "a":
				if (!(map instanceof TeamEventGameMap)) {
					player.sendMessage(CC.RED + "That type of map only has spread locations!");
					player.sendMessage(CC.RED + "To add a location to the spread list, use /event map set <map> spread.");
					return;
				}
				TeamEventGameMap teamMapA = (TeamEventGameMap) map;
				teamMapA.setSpawnPointA(player.getLocation());
				player.sendMessage(CC.GREEN + "You successfully updated " +
						map.getMapName() + "'s " + field + " location.");
				break;

			case "b":
				if (!(map instanceof TeamEventGameMap)) {
					player.sendMessage(CC.RED + "That type of map only has spread locations!");
					player.sendMessage(CC.RED + "To add a location to the spread list, use /event map set <map> spread.");
					return;
				}
				TeamEventGameMap teamMapB = (TeamEventGameMap) map;
				teamMapB.setSpawnPointB(player.getLocation());
				player.sendMessage(CC.GREEN + "You successfully updated " +
						map.getMapName() + "'s " + field + " location.");
				break;

			case "spread":
				if (!(map instanceof SpreadEventGameMap)) {
					player.sendMessage(CC.RED + "That type of map does not have spread locations!");
					player.sendMessage(CC.RED + "To set one of the locations, use /event map set <map> <a/b>.");
					return;
				}
				SpreadEventGameMap spreadMap = (SpreadEventGameMap) map;
				spreadMap.getSpawnLocations().add(player.getLocation());
				player.sendMessage(CC.GREEN + "You successfully added a location to " +
						map.getMapName() + "'s " + field + " list.");
				break;

			default:
				player.sendMessage(CC.RED + "A field by that name does not exist.");
				player.sendMessage(CC.RED + "Fields: spectator, a, b, spread");
				return;
		}

		map.save();
	}
}