package gg.arcdev.practice.core.profile.meta;

import gg.arcdev.practice.game.kit.KitLoadout;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ProfileKitData {

	@Getter @Setter private int elo = 1000;
	@Getter @Setter private int won;
	@Getter @Setter private int lost;
	@Getter @Setter private KitLoadout[] loadouts = new KitLoadout[4];

	public void incrementWon() {
		this.won++;
	}

	public void incrementLost() {
		this.lost++;
	}

	public KitLoadout getLoadout(int index) {
		return loadouts[index];
	}

	public void replaceKit(int index, KitLoadout loadout) {
		loadouts[index] = loadout;
	}

	public void deleteKit(KitLoadout loadout) {
		for (int i = 0; i < 4; i++) {
			if (loadouts[i] != null && loadouts[i].equals(loadout)) {
				loadouts[i] = null;
				break;
			}
		}
	}

	public int getKitCount() {
		int i = 0;

		for (KitLoadout loadout : loadouts) {
			if (loadout != null) {
				i++;
			}
		}

		return i;
	}

	public void giveBooks(Player player) {
		List<KitLoadout> loadouts = new ArrayList<>();

		for (KitLoadout loadout : this.loadouts) {
			if (loadout != null) {
				loadouts.add(loadout);
			}
		}

		ItemStack defaultKitItemStack = createKitSelectionItem("Default");
		ItemMeta defaultKitItemMeta = defaultKitItemStack.getItemMeta();
		defaultKitItemMeta.setDisplayName(defaultKitItemMeta.getDisplayName()
		                                                    .replace("%KIT%", "Default"));
		defaultKitItemStack.setItemMeta(defaultKitItemMeta);

		if (loadouts.isEmpty()) {
			player.getInventory().setItem(0, defaultKitItemStack);
			} else {
				player.getInventory().setItem(8, defaultKitItemStack);

				for (KitLoadout loadout : loadouts) {
					player.getInventory().addItem(createKitSelectionItem(loadout.getCustomName()));
				}
			}

		player.updateInventory();
	}

	private ItemStack createKitSelectionItem(String kitName) {
		String path = "HOTBAR_ITEMS.KIT_SELECTION.";
		ItemStack itemStack = new ItemBuilder(Material.valueOf(
				Main.getInstance().getMainConfig().getConfiguration().getString(path + "MATERIAL")))
				.durability(Main.getInstance().getMainConfig().getConfiguration().getInt(path + "DURABILITY"))
				.name(Main.getInstance().getMainConfig().getConfiguration().getString(path + "NAME"))
				.lore(Main.getInstance().getMainConfig().getConfiguration().getStringList(path + "LORE"))
				.build();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%KIT%", kitName));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

}
