package gg.arcdev.practice.game.kit;


import gg.arcdev.practice.util.command.command.adapter.CommandTypeAdapter;

public class KitTypeAdapter implements CommandTypeAdapter {

	@Override
	public <T> T convert(String string, Class<T> type) {
		return type.cast(Kit.getByName(string));
	}

}
