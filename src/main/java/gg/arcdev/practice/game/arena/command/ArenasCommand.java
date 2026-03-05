package gg.arcdev.practice.game.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.arena.ArenaType;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ChatComponentBuilder;
import gg.arcdev.practice.util.ChatHelper;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

@CommandAlias("arenas")
@CommandPermission("praxi.admin.arena")
public class ArenasCommand extends BaseCommand {

	@Default
	public void onArenas(Player player) {
		player.sendMessage(CC.GOLD + "Arenas:");

		if (Arena.getArenas().isEmpty()) {
			player.sendMessage(CC.GRAY + "There are no arenas.");
			return;
		}

		for (Arena arena : Arena.getArenas()) {
			if (arena.getType() != ArenaType.DUPLICATE) {
				ChatComponentBuilder builder = new ChatComponentBuilder("")
						.parse("&7- " + (arena.isSetup() ? "&a" : "&c") + arena.getName() +
								"&7(" + arena.getType().name() + ") ");

				ChatComponentBuilder status = new ChatComponentBuilder("").parse("&7[&6STATUS&7]");
				status.attachToEachPart(ChatHelper.hover("&6Click to view this arena's status."));
				status.attachToEachPart(ChatHelper.click("/arena status " + arena.getName()));

				builder.append(" ");

				for (BaseComponent component : status.create()) {
					builder.append((TextComponent) component);
				}

				player.spigot().sendMessage(builder.create());
			}
		}
	}
}