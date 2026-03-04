package gg.arcdev.practice.commands.donater;

import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "fly", permission = "praxi.donor.fly")
public class FlyCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
			if (player.getAllowFlight()) {
				player.setAllowFlight(false);
				player.setFlying(false);
				player.updateInventory();
				player.sendMessage(CC.YELLOW + "You are no longer flying.");
			} else {
				player.setAllowFlight(true);
				player.setFlying(true);
				player.updateInventory();
				player.sendMessage(CC.YELLOW + "You are now flying.");
			}
		} else {
			player.sendMessage(CC.RED + "You cannot fly right now.");
		}
	}

}
