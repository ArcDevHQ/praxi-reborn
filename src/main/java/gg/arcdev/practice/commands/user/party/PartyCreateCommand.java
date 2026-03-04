package gg.arcdev.practice.commands.user.party;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.core.hotbar.Hotbar;
import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "p create", "party create" })
public class PartyCreateCommand {

	public void execute(Player player) {
		if (player.hasMetadata("frozen")) {
			player.sendMessage(CC.RED + "You cannot create a party while frozen.");
			return;
		}

		Profile profile = Profile.getByUuid(player.getUniqueId());

		if (profile.getParty() != null) {
			player.sendMessage(CC.RED + "You already have a party.");
			return;
		}

		if (profile.getState() != ProfileState.LOBBY) {
			player.sendMessage(CC.RED + "You must be in the lobby to create a party.");
			return;
		}

		profile.setParty(new Party(player));

		Hotbar.giveHotbarItems(player);

		player.sendMessage(Locale.PARTY_CREATE.format());
	}

}
