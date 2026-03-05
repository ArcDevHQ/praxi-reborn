package gg.arcdev.practice.game.kit.menu;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.kit.KitLoadout;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.core.hotbar.Hotbar;
import gg.arcdev.practice.core.profile.meta.ProfileKitData;
import gg.arcdev.practice.util.BukkitReflection;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.PlayerUtil;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.Menu;
import gg.arcdev.practice.util.menu.button.DisplayButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class KitEditorMenu extends Menu {

	private static final int[] ITEM_POSITIONS = new int[]{
			20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 47, 48, 49, 50, 51, 52, 53
	};
	private static final int[] BORDER_POSITIONS = new int[]{
			0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 17, 18, 26, 27, 35, 36, 44
	};

	private int index;

	public KitEditorMenu() {
		setAutoUpdate(true);
		setPlaceholder(true);
		setUpdateAfterClick(false);

		ItemStack placeholderItem = new ItemBuilder(Material.STAINED_GLASS_PANE)
				.durability(7)
				.name(" ")
				.build();

		setPlaceholderButton(new DisplayButton(placeholderItem, true));
	}

	@Override
	public String getTitle(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		return CC.translate("&6&lEditing: &a" + profile.getKitEditorData().getSelectedKit().getName());
	}

	@Override
	public int getSize() {
		return 54;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int border : BORDER_POSITIONS) {
			buttons.put(border, Button.placeholder(Material.COAL_BLOCK, (byte) 0, " "));
		}

		buttons.put(0, new CurrentKitButton());
		buttons.put(2, new SaveButton());
		buttons.put(6, new LoadDefaultKitButton());
		buttons.put(7, new ClearInventoryButton());
		buttons.put(8, new CancelButton(index));

		Profile profile = Profile.getByUuid(player.getUniqueId());
		Kit kit = profile.getKitEditorData().getSelectedKit();
		KitLoadout kitLoadout = profile.getKitEditorData().getSelectedKitLoadout();

		buttons.put(18, new ArmorDisplayButton(kitLoadout.getArmor()[3]));
		buttons.put(27, new ArmorDisplayButton(kitLoadout.getArmor()[2]));
		buttons.put(36, new ArmorDisplayButton(kitLoadout.getArmor()[1]));
		buttons.put(45, new ArmorDisplayButton(kitLoadout.getArmor()[0]));

		List<ItemStack> items = kit.getEditRules().getEditorItems();
		for (int i = 0; i < items.size() && i < ITEM_POSITIONS.length; i++) {
			buttons.put(ITEM_POSITIONS[i], new InfiniteItemButton(items.get(i)));
		}

		return buttons;
	}

	@Override
	public void onOpen(Player player) {
		if (!isClosedByMenu()) {
			PlayerUtil.reset(player);
			Profile profile = Profile.getByUuid(player.getUniqueId());
			profile.getKitEditorData().setActive(true);

			if (profile.getKitEditorData().getSelectedKitLoadout() != null) {
				player.getInventory().setContents(profile.getKitEditorData().getSelectedKitLoadout().getContents());
			}

			player.updateInventory();
		}
	}

	@Override
	public void onClose(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		profile.getKitEditorData().setActive(false);

		if (profile.getState() != ProfileState.FIGHTING) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Hotbar.giveHotbarItems(player);
				}
			}.runTask(Main.getInstance());
		}
	}

	private class ArmorDisplayButton extends Button {
		private ItemStack itemStack;

		ArmorDisplayButton(ItemStack itemStack) {
			this.itemStack = itemStack;
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			if (itemStack == null || itemStack.getType() == Material.AIR) return new ItemStack(Material.AIR);

			return new ItemBuilder(itemStack.clone())
					.name(CC.AQUA + BukkitReflection.getItemStackName(itemStack))
					.lore(CC.YELLOW + "Automatically equipped.")
					.build();
		}
	}

	private class CurrentKitButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			Profile profile = Profile.getByUuid(player.getUniqueId());
			return new ItemBuilder(Material.NAME_TAG)
					.name("&6&lEditing: &a" + profile.getKitEditorData().getSelectedKit().getName())
					.build();
		}
	}

	private class ClearInventoryButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.durability(7)
					.name("&e&lClear Inventory")
					.lore("&eClear your inventory to start fresh.")
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);
			player.getInventory().setContents(new ItemStack[36]);
			player.updateInventory();
		}

		@Override
		public boolean shouldUpdate(Player player, ClickType clickType) {
			return true;
		}
	}

	private class LoadDefaultKitButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.durability(7)
					.name(CC.YELLOW + CC.BOLD + "Load Default Kit")
					.lore("&eLoad the default kit items into the editor")
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);
			Profile profile = Profile.getByUuid(player.getUniqueId());
			player.getInventory()
					.setContents(profile.getKitEditorData().getSelectedKit().getKitLoadout().getContents());
			player.updateInventory();
		}

		@Override
		public boolean shouldUpdate(Player player, ClickType clickType) {
			return true;
		}
	}

	private class SaveButton extends Button {
		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.durability(5)
					.name("&a&lSave Kit")
					.lore("&eSave your current kit layout")
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);
			player.closeInventory();
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.getKitEditorData().getSelectedKitLoadout() != null) {
				profile.getKitEditorData().getSelectedKitLoadout().setContents(player.getInventory().getContents());
			}
			Hotbar.giveHotbarItems(player);
			new KitManagementMenu(profile.getKitEditorData().getSelectedKit()).openMenu(player);
		}
	}

	private class CancelButton extends Button {
		private int index;

		CancelButton(int index) {
			this.index = index;
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.durability(14)
					.name("&c&lCancel")
					.lore("&eAbort editing and return to kit menu")
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);
			Profile profile = Profile.getByUuid(player.getUniqueId());
			if (profile.getKitEditorData().getSelectedKit() != null) {
				ProfileKitData kitData = profile.getKitData().get(profile.getKitEditorData().getSelectedKit());
				kitData.replaceKit(index, null);
				new KitManagementMenu(profile.getKitEditorData().getSelectedKit()).openMenu(player);
			}
		}
	}

	private class InfiniteItemButton extends DisplayButton {
		InfiniteItemButton(ItemStack itemStack) {
			super(itemStack, false);
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbar) {
			ItemStack itemStack = player.getOpenInventory().getTopInventory().getItem(slot);
			player.setItemOnCursor(itemStack);
			player.updateInventory();
		}
	}

}