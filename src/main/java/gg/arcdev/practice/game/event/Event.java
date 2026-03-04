package gg.arcdev.practice.game.event;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.event.game.EventGameLogic;
import gg.arcdev.practice.game.event.impl.sumo.SumoEvent;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface Event {

	List<Event> events = new ArrayList<>();

	static void init() {
		events.add(new SumoEvent());

		for (Event event : events) {
			for (Listener listener : event.getListeners()) {
				Main.get().getServer().getPluginManager().registerEvents(listener, Main.get());
			}

			for (Object command : event.getCommands()) {
				Main.get().getHoncho().registerCommand(command);
			}
		}
	}

	static <T extends Event> T getEvent(Class<? extends Event> clazz) {
		for (Event event : events) {
			if (event.getClass() == clazz) {
				return (T) clazz.cast(event);
			}
		}

		return null;
	}

	String getDisplayName();

	String getDisplayName(EventGame game);

	List<String> getDescription();

	Location getLobbyLocation();

	void setLobbyLocation(Location location);

	ItemStack getIcon();

	boolean canHost(Player player);

	List<String> getAllowedMaps();

	List<Listener> getListeners();

	default List<Object> getCommands() {
		return new ArrayList<>();
	}

	EventGameLogic start(EventGame game);

	void save();

}
