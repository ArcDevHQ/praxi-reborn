package gg.arcdev.practice.game.event.commands.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("event add map")
@CommandPermission("praxi.admin.event")
public class EventAddMapCommand extends BaseCommand {

	@Default
	public void onAddMap(Player player, Event event, EventGameMap gameMap) {
		if (event == null) {
			player.sendMessage(CC.RED + "An event type by that name does not exist.");
			player.sendMessage(CC.RED + "Types: sumo, corners");
			return;
		}

		if (gameMap == null) {
			player.sendMessage(CC.RED + "A map with that name does not exist.");
			return;
		}

		if (!event.getAllowedMaps().contains(gameMap.getMapName())) {
			event.getAllowedMaps().add(gameMap.getMapName());
			event.save();

			player.sendMessage(CC.GOLD + "You successfully added the \"" + CC.GREEN + gameMap.getMapName() +
					CC.GOLD + "\" map from the \"" + CC.GREEN + gameMap.getMapName() + CC.GOLD +
					"\" event.");
		}
	}
}