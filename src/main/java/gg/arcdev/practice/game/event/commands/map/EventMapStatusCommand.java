package gg.arcdev.practice.game.event.commands.map;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.game.event.game.map.impl.SpreadEventGameMap;
import gg.arcdev.practice.game.event.game.map.impl.TeamEventGameMap;
import gg.arcdev.practice.util.CC;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.entity.Player;

@CommandAlias("event map status")
@CommandPermission("praxi.admin.event")
public class EventMapStatusCommand extends BaseCommand {

	@Default
	public void onStatus(Player player, EventGameMap gameMap) {
		if (gameMap == null) {
			player.sendMessage(CC.RED + "An event map with that name does not exist.");
			return;
		}

		player.sendMessage(CC.GOLD + CC.BOLD + "Event Map Status " + CC.GRAY + "(" +
				(gameMap.isSetup() ? CC.GREEN : CC.RED) + gameMap.getMapName() + CC.GRAY + ")");

		player.sendMessage(CC.GREEN + "Spectator Location: " + CC.YELLOW +
				(gameMap.getSpectatorPoint() == null ?
						StringEscapeUtils.unescapeJava("\u2717") :
						StringEscapeUtils.unescapeJava("\u2713")));

		if (gameMap instanceof TeamEventGameMap) {
			TeamEventGameMap teamGameMap = (TeamEventGameMap) gameMap;

			player.sendMessage(CC.GREEN + "Spawn A Location: " + CC.YELLOW +
					(teamGameMap.getSpawnPointA() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

			player.sendMessage(CC.GREEN + "Spawn B Location: " + CC.YELLOW +
					(teamGameMap.getSpawnPointB() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

		} else if (gameMap instanceof SpreadEventGameMap) {
			SpreadEventGameMap spreadGameMap = (SpreadEventGameMap) gameMap;

			player.sendMessage(CC.GREEN + "Spread Locations: " + CC.YELLOW +
					(spreadGameMap.getSpawnLocations().isEmpty() ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));
		}
	}
}