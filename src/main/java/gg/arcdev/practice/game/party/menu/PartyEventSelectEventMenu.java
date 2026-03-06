package gg.arcdev.practice.game.party.menu;

import gg.arcdev.practice.game.party.PartyEvent;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.Menu;
import gg.arcdev.practice.util.menu.button.DisplayButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PartyEventSelectEventMenu extends Menu {

	{
		setPlaceholder(true);

		ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE)
				.durability(7)
				.name(" ")
				.build();

		setPlaceholderButton(new DisplayButton(placeholder, true));
	}

	@Override
	public String getTitle(Player player) {
		return CC.translate("&8» &b&lParty Events");
	}

	@Override
	public int getSize() {
		return 45;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(20, new SelectEventButton(PartyEvent.FFA));
		buttons.put(24, new SelectEventButton(PartyEvent.SPLIT));

		return buttons;
	}

	@AllArgsConstructor
	private class SelectEventButton extends Button {

		private PartyEvent partyEvent;

		@Override
		public ItemStack getButtonItem(Player player) {

			Material material = partyEvent == PartyEvent.FFA
					? Material.DIAMOND_SWORD
					: Material.GOLD_SWORD;

			return new ItemBuilder(material)
					.name("&b&l" + partyEvent.getName())
					.lore(
							"&7Start a party event using",
							"&7the &f" + partyEvent.getName() + " &7mode.",
							"",
							"&bClick to select"
					)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getParty() == null) {
				player.sendMessage(CC.RED + "You are not in a party.");
				return;
			}

			new PartyEventSelectKitMenu(partyEvent).openMenu(player);
		}
	}
}