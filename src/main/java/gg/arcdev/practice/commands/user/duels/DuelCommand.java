package gg.arcdev.practice.commands.user.duels;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.game.duel.DuelProcedure;
import gg.arcdev.practice.game.duel.DuelRequest;
import gg.arcdev.practice.game.duel.menu.DuelSelectKitMenu;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

public class DuelCommand extends BaseCommand {

	@CommandAlias("duel")
	@Default
	public void onDuel(Player sender, @Name("player") Profile target) {
		if (target == null) {
			sender.sendMessage(CC.RED + "A player with that name could not be found.");
			return;
		}

		if (sender.hasMetadata("frozen")) {
			sender.sendMessage(CC.RED + "You cannot duel while frozen.");
			return;
		}

		if (target.getPlayer().hasMetadata("frozen")) {
			sender.sendMessage(CC.RED + "You cannot duel a frozen player.");
			return;
		}

		if (sender.getUniqueId().equals(target.getPlayer().getUniqueId())) {
			sender.sendMessage(CC.RED + "You cannot duel yourself.");
			return;
		}

		Profile senderProfile = Profile.getByUuid(sender.getUniqueId());
		Profile targetProfile = Profile.getByUuid(target.getPlayer().getUniqueId());

		if (senderProfile.isBusy()) {
			sender.sendMessage(CC.RED + "You cannot duel right now.");
			return;
		}

		if (targetProfile.isBusy()) {
			sender.sendMessage(target.getPlayer().getDisplayName() + CC.RED + " is currently busy.");
			return;
		}

		if (!targetProfile.getOptions().receiveDuelRequests()) {
			sender.sendMessage(CC.RED + "That player is not accepting duel requests at the moment.");
			return;
		}

		DuelRequest duelRequest = targetProfile.getDuelRequest(sender);

		if (duelRequest != null) {
			if (!senderProfile.isDuelRequestExpired(duelRequest)) {
				sender.sendMessage(CC.RED + "You already sent that player a duel request.");
				return;
			}
		}

		if (senderProfile.getParty() != null && targetProfile.getParty() == null) {
			sender.sendMessage(CC.RED + "You cannot send a party duel request to a player that is not in a party.");
			return;
		}

		if (senderProfile.getParty() == null && targetProfile.getParty() != null) {
			sender.sendMessage(CC.RED + "You cannot send a duel request to a player in a party.");
			return;
		}

		if (senderProfile.getParty() != null && targetProfile.getParty() != null) {
			if (senderProfile.getParty().equals(targetProfile.getParty())) {
				sender.sendMessage(CC.RED + "You cannot duel your own party.");
				return;
			}
		}

		DuelProcedure procedure = new DuelProcedure(sender, target.getPlayer(), senderProfile.getParty() != null);
		senderProfile.setDuelProcedure(procedure);

		new DuelSelectKitMenu().openMenu(sender);
	}
}