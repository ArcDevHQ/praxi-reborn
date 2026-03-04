package gg.arcdev.practice.game.arena.menu;

import java.util.Map;

import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.Menu;
import org.bukkit.entity.Player;

public class SelectArenaMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&6Select Arena";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		return super.getButtons();
	}

}
