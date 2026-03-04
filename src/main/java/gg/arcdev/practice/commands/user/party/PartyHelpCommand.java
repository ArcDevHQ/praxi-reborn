package gg.arcdev.practice.commands.user.party;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.util.command.command.CommandMeta;
import org.bukkit.entity.Player;

@CommandMeta(label = { "p", "p help", "party", "party help" })
public class PartyHelpCommand {

	public void execute(Player player) {
		Locale.PARTY_HELP.formatLines().forEach(player::sendMessage);
	}

}
