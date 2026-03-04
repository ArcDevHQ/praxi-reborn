package gg.arcdev.practice.commands.admin.kits;

import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "kit create", permission = "praxi.kit.create")
public class KitCreateCommand {

	public void execute(Player player, String kitName) {
		if (Kit.getByName(kitName) != null) {
			player.sendMessage(CC.RED + "A kit with that name already exists.");
			return;
		}

		Kit kit = new Kit(kitName);
		kit.save();

		Kit.getKits().add(kit);

		player.sendMessage(CC.GREEN + "You created a new kit.");
	}

}
