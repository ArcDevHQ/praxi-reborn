package gg.arcdev.practice.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.match.MatchSnapshot;
import gg.arcdev.practice.game.match.menu.MatchDetailsMenu;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("viewinv")
public class ViewInventoryCommand extends BaseCommand {

	@Default
	public void onViewInventory(Player player, String id) {
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