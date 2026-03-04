package gg.arcdev.practice.commands.admin.arena;

import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = "arena set spawn", permission = "praxi.admin.arena")
public class ArenaSetSpawnCommand {

	public void execute(Player player, Arena arena, String pos) {
		if (arena != null) {
			if (pos.equalsIgnoreCase("a")) {
				arena.setSpawnA(player.getLocation());
			} else if (pos.equalsIgnoreCase("b")) {
				arena.setSpawnB(player.getLocation());
			} else {
				player.sendMessage(CC.RED + "Invalid spawn point. Try \"a\" or \"b\".");
				return;
			}

			arena.save();

			player.sendMessage(CC.GOLD + "Updated spawn point \"" + pos + "\" for arena \"" + arena.getName() + "\"");
		} else {
			player.sendMessage(CC.RED + "An arena with that name already exists.");
		}
	}

}
