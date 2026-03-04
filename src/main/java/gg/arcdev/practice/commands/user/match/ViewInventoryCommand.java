package gg.arcdev.practice.commands.user.match;

import gg.arcdev.practice.game.match.MatchSnapshot;
import gg.arcdev.practice.game.match.menu.MatchDetailsMenu;
import gg.arcdev.practice.util.command.command.CommandMeta;
import java.util.UUID;

import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "viewinv")
public class ViewInventoryCommand {

	public void execute(Player player, String id) {
		MatchSnapshot cachedInventory;

		try {
			cachedInventory = MatchSnapshot.getByUuid(UUID.fromString(id));
		} catch (Exception e) {
			cachedInventory = MatchSnapshot.getByName(id);
		}

		if (cachedInventory == null) {
			player.sendMessage(CC.RED + "Couldn't find an inventory for that ID.");
			return;
		}

		new MatchDetailsMenu(cachedInventory).openMenu(player);
	}

}
