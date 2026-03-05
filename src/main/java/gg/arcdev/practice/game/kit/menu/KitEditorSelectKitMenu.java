package gg.arcdev.practice.game.kit.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.Menu;
import gg.arcdev.practice.util.menu.button.DisplayButton;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitEditorSelectKitMenu extends Menu {

	{
		setAutoUpdate(true);
		setPlaceholder(true);

		ItemStack placeholderItem = new ItemBuilder(Material.STAINED_GLASS_PANE)
				.durability(7)
				.name(" ")
				.build();

		setPlaceholderButton(new DisplayButton(placeholderItem, true));
	}

	@Override
	public String getTitle(Player player) {
		return CC.translate("&8» &6&lSelect a Kit");
	}

	@Override
	public int getSize() {
		return 45;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		List<Kit> enabledKits = new ArrayList<>();
		for (Kit kit : Kit.getKits()) {
			if (kit.isEnabled()) enabledKits.add(kit);
		}

		int startSlot = 10;
		for (int i = 0; i < enabledKits.size(); i++) {
			int slot = startSlot + i;
			if (slot >= 44) break;

			Kit kit = enabledKits.get(i);
			buttons.put(slot, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					return new ItemBuilder(kit.getDisplayIcon())
							.name("&b&l" + kit.getName())
							.lore(
									"&7Click to select this kit",
									" "
							)
							.build();
				}

				@Override
				public void clicked(Player player, ClickType clickType) {
					player.closeInventory();

					Profile profile = Profile.getByUuid(player.getUniqueId());
					if (profile == null) return;

					profile.getKitEditorData().setSelectedKit(kit);
					new KitManagementMenu(kit).openMenu(player);
				}
			});
		}

		return buttons;
	}
}