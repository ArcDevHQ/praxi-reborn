package gg.arcdev.practice.commands.user.match;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("spectate|spec")
public class SpectateCommand extends BaseCommand {

	@Default
	public void onSpectate(Player player, Profile target) {
		if (player.hasMetadata("frozen")) {
			player.sendMessage(CC.RED + "You cannot spectate while frozen.");
			return;
		}

		if (target == null) {
			player.sendMessage(CC.RED + "A player with that name could not be found.");
			return;
		}

		Profile playerProfile = Profile.getByUuid(player.getUniqueId());

		if (playerProfile.isBusy()) {
			player.sendMessage(CC.RED + "You must be in the lobby and not queueing to spectate.");
			return;
		}

		if (playerProfile.getParty() != null) {
			player.sendMessage(CC.RED + "You must leave your party to spectate a match.");
			return;
		}


		if (target.getState() != ProfileState.FIGHTING) {
			player.sendMessage(CC.RED + "That player is not in a match.");
			return;
		}

		if (!target.getOptions().allowSpectators()) {
			player.sendMessage(CC.RED + "That player is not allowing spectators.");
			return;
		}

		target.getMatch().addSpectator(player, target.getPlayer());
	}
}