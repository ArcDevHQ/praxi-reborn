package gg.arcdev.practice.game.queue.menu;

import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.game.queue.Queue;
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

import java.util.*;

@AllArgsConstructor
public class QueueSelectKitMenu extends Menu {

	private boolean ranked;

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
		return CC.translate("&8» &6&lSelect Kit &7(" + (ranked ? "&cRanked" : "&aUnranked") + "&7)");
	}

	@Override
	public int getSize() {
		return 45;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		List<Queue> filteredQueues = new ArrayList<>();
		for (Queue queue : Queue.getQueues()) {
			if (queue.isRanked() == ranked) {
				filteredQueues.add(queue);
			}
		}

		int startSlot = 10;

		for (int i = 0; i < filteredQueues.size(); i++) {
			Queue queue = filteredQueues.get(i);
			int slot = startSlot + i;
			if (slot >= 44) break;

			buttons.put(slot, new Button() {
				@Override
				public ItemStack getButtonItem(Player player) {
					List<String> lore = new ArrayList<>();
					lore.add("&b| &7Fighting: &f" + Match.getInFightsCount(queue));
					lore.add("&b| &7Queueing: &f" + queue.getPlayers().size());
					lore.add(" ");
					lore.add("&eClick to queue");

					return new ItemBuilder(queue.getKit().getDisplayIcon())
							.name("&b&l" + queue.getKit().getName())
							.lore(lore)
							.build();
				}

				@Override
				public void clicked(Player player, ClickType clickType) {
					Profile profile = Profile.getByUuid(player.getUniqueId());
					if (profile == null) return;

					if (player.hasMetadata("frozen")) {
						player.sendMessage(CC.RED + "You cannot queue while frozen.");
						return;
					}

					if (profile.isBusy()) {
						player.sendMessage(CC.RED + "You cannot queue right now.");
						return;
					}

					player.closeInventory();
					queue.addPlayer(player, queue.isRanked()
							? profile.getKitData().get(queue.getKit()).getElo()
							: 0);
				}
			});
		}

		return buttons;
	}
}