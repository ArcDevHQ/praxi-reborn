package gg.arcdev.practice.commands.admin.arena;

import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena delete", permission = "praxi.admin.arena")
public class ArenaDeleteCommand {

	public void execute(Player player, Arena arena) {
		if (arena != null) {
			arena.delete();

			player.sendMessage(CC.GOLD + "Deleted arena \"" + arena.getName() + "\"");
		} else {
			player.sendMessage(CC.RED + "An arena with that name does not exist.");
		}
	}

}
