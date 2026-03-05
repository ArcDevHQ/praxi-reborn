package gg.arcdev.practice.game.event.impl.sumo;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.event.game.EventGameLogic;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.LocationUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import gg.arcdev.practice.util.config.BasicConfigurationFile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SumoEvent implements Event {

	@Setter private Location lobbyLocation;
	@Getter private final List<String> allowedMaps;

	public SumoEvent() {
		BasicConfigurationFile config = Main.getInstance().getEventsConfig();

		lobbyLocation = LocationUtil.deserialize(config.getString("EVENTS.SUMO.LOBBY_LOCATION"));

		allowedMaps = new ArrayList<>();
		allowedMaps.addAll(config.getStringList("EVENTS.SUMO.ALLOWED_MAPS"));
	}

	@Override
	public String getDisplayName() {
		return "Sumo";
	}

	@Override
	public String getDisplayName(EventGame game) {
		return "Sumo &7(Solos)";
	}

	@Override
	public List<String> getDescription() {
		return Arrays.asList(
				"Compete by knocking your",
				"opponent off of the platform."
		);
	}

	@Override
	public Location getLobbyLocation() {
		return lobbyLocation;
	}

	@Override
	public ItemStack getIcon() {
		return new ItemBuilder(Material.FEATHER).build();
	}

	@Override
	public boolean canHost(Player player) {
		return player.hasPermission("praxi.event.host.sumo");
	}

	@Override
	public List<Listener> getListeners() {
		return Collections.emptyList();
	}

	@Override
	public List<Object> getCommands() {
		return Collections.emptyList();
	}

	@Override
	public EventGameLogic start(EventGame game) {
		return new SumoGameLogic(game);
	}

	@Override
	public void save() {
		FileConfiguration config = Main.getInstance().getEventsConfig().getConfiguration();
		config.set("EVENTS.SUMO.LOBBY_LOCATION", LocationUtil.serialize(lobbyLocation));
		config.set("EVENTS.SUMO.ALLOWED_MAPS", allowedMaps);

		try {
			config.save(Main.getInstance().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
