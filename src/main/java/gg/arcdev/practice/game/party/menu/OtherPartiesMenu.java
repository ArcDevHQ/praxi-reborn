package gg.arcdev.practice.game.party.menu;

import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.button.DisplayButton;
import gg.arcdev.practice.util.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class OtherPartiesMenu extends PaginatedMenu {

	{
		setPlaceholder(true);

		ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE)
				.durability(7)
				.name(" ")
				.build();

		setPlaceholderButton(new DisplayButton(placeholder, true));
	}

	@Override
	public String getPrePaginatedTitle(Player player) {
		return CC.translate("&8» &b&lOther Parties");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {

		Profile profile = Profile.getByUuid(player.getUniqueId());
		Map<Integer, Button> buttons = new HashMap<>();

		for (Party party : Party.getParties()) {
			if (!party.equals(profile.getParty())) {
				buttons.put(buttons.size(), new PartyDisplayButton(party));
			}
		}

		return buttons;
	}

	@AllArgsConstructor
	public static class PartyDisplayButton extends Button {

		private Party party;

		@Override
		public ItemStack getButtonItem(Player player) {

			List<String> lore = new ArrayList<>();
			int shown = 0;

			for (Player partyPlayer : party.getListOfPlayers()) {

				if (shown >= 10) break;

				lore.add("&b| &f" + partyPlayer.getName());
				shown++;
			}

			if (party.getPlayers().size() > shown) {
				lore.add("&7and " + (party.getPlayers().size() - shown) + " others...");
			}

			lore.add("");
			lore.add("&eClick to duel this party");

			return new ItemBuilder(Material.SKULL_ITEM)
					.durability(3)
					.name("&b&lParty of &f" + party.getLeader().getName())
					.amount(party.getPlayers().size())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {

			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getParty() == null) {
				player.sendMessage(CC.RED + "You are not in a party.");
				return;
			}

			if (profile.getParty().equals(party)) {
				return;
			}

			if (!profile.getParty().getLeader().equals(player)) {
				player.sendMessage(CC.RED + "You are not the leader of your party.");
				return;
			}

			player.chat("/duel " + party.getLeader().getName());
		}
	}
}