package gg.arcdev.practice.commands.admin.kits;

import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit getloadout", permission = "praxi.kit.getloadout")
public class KitGetLoadoutCommand {

	public void execute(Player player, Kit kit) {
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name does not exist.");
			return;
		}

		player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
		player.getInventory().setContents(kit.getKitLoadout().getContents());
		player.updateInventory();

		player.sendMessage(CC.GREEN + "You received the kit's loadout.");
	}

}
