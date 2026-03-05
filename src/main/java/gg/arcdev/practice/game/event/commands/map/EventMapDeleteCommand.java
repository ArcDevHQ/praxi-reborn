package gg.arcdev.practice.game.event.commands.map;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("event map delete")
@CommandPermission("praxi.admin.event")
public class EventMapDeleteCommand extends BaseCommand {

	@Default
	public void onDelete(Player player, EventGameMap gameMap) {
		if (gameMap == null) {
			player.sendMessage(CC.RED + "An event map with that name does not exist.");
			return;
		}

		gameMap.delete();
		EventGameMap.getMaps().remove(gameMap);

		player.sendMessage(CC.GREEN + "You successfully deleted the event map \"" + gameMap.getMapName() + "\".");
	}
}