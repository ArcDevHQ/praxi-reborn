package gg.arcdev.practice.game.event.commands.user;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.game.event.game.map.vote.EventGameMapVoteData;
import gg.arcdev.practice.game.event.game.menu.EventHostMenu;
import gg.arcdev.practice.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("host|event host")
@CommandPermission("praxi.event.host")
public class EventHostCommand extends BaseCommand {

	@Default
	public void onHostMenu(Player player) {
		if (player.hasMetadata("frozen")) {
			player.sendMessage(ChatColor.RED + "You cannot host an event while frozen.");
			return;
		}

		new EventHostMenu().openMenu(player);
	}

	@Default
	public void onHost(Player player, Event event) {
		hostEvent(player, event, getHostSlots(player));
	}

	@Default
	public void onHost(Player player, Event event, @Optional Integer slots) {
		if (slots == null) slots = getHostSlots(player);
		hostEvent(player, event, slots);
	}

	private void hostEvent(Player player, Event event, int slots) {
		if (player.hasMetadata("frozen")) {
			player.sendMessage(ChatColor.RED + "You cannot host an event while frozen.");
			return;
		}

		if (!player.hasPermission("praxi.event.host.slots") && (slots < 4 || slots > 200)) {
			player.sendMessage(CC.RED + "Events can only hold 4-200 players.");
			return;
		}

		if (EventGame.getActiveGame() != null) {
			player.sendMessage(CC.RED + "There is already an active event.");
			return;
		}

		if (!EventGame.getCooldown().hasExpired()) {
			player.sendMessage(CC.RED + "The event cooldown is active.");
			return;
		}

		if (event == null) {
			player.sendMessage(CC.RED + "That type of event does not exist.");
			player.sendMessage(CC.RED + "Types: sumo, corners");
			return;
		}

		if (EventGameMap.getMaps().isEmpty()) {
			player.sendMessage(CC.RED + "There are no available event maps.");
			return;
		}

		List<EventGameMap> validMaps = new ArrayList<>();
		for (EventGameMap gameMap : EventGameMap.getMaps()) {
			if (event.getAllowedMaps().contains(gameMap.getMapName())) {
				validMaps.add(gameMap);
			}
		}

		if (validMaps.isEmpty()) {
			player.sendMessage(CC.RED + "There are no available event maps.");
			return;
		}

		try {
			EventGame game = new EventGame(event, player, slots);

			for (EventGameMap gameMap : validMaps) {
				game.getVotesData().put(gameMap, new EventGameMapVoteData());
			}

			game.broadcastJoinMessage();
			game.start();
			game.getGameLogic().onJoin(player);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getHostSlots(Player host) {
		int slots = 32;
		FileConfiguration config = Main.get().getEventsConfig().getConfiguration();

		for (String key : config.getConfigurationSection("HOST_SLOTS").getKeys(false)) {
			if (host.hasPermission(config.getString("HOST_SLOTS." + key + ".PERMISSION"))) {
				int configSlots = config.getInt("HOST_SLOTS." + key + ".SLOTS");
				if (configSlots > slots) {
					slots = configSlots;
				}
			}
		}

		return slots;
	}
}