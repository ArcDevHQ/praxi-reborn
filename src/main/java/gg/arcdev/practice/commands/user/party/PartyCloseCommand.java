package gg.arcdev.practice.commands.user.party;

import gg.arcdev.practice.game.party.PartyPrivacy;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "p close", "party close", "p lock", "party lock" })
public class PartyCloseCommand {

	public void execute(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getParty() == null) {
			player.sendMessage(CC.RED + "You do not have a party.");
			return;
		}

		if (!profile.getParty().getLeader().equals(player)) {
			player.sendMessage(CC.RED + "You are not the leader of your party.");
			return;
		}

		profile.getParty().setPrivacy(PartyPrivacy.CLOSED);
	}

}
