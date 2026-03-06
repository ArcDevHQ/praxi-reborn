package gg.arcdev.practice.game.party.menu;

import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.match.impl.BasicFreeForAllMatch;
import gg.arcdev.practice.game.match.impl.BasicTeamMatch;
import gg.arcdev.practice.game.match.participant.MatchGamePlayer;
import gg.arcdev.practice.game.participant.GameParticipant;
import gg.arcdev.practice.game.participant.TeamGameParticipant;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.game.party.PartyEvent;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ItemBuilder;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.Menu;
import gg.arcdev.practice.util.menu.button.BackButton;
import gg.arcdev.practice.util.menu.button.DisplayButton;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class PartyEventSelectKitMenu extends Menu {

	private PartyEvent partyEvent;

	{
		setAutoUpdate(true);
		setPlaceholder(true);

		ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE)
				.durability(7)
				.name(" ")
				.build();

		setPlaceholderButton(new DisplayButton(placeholder, true));
	}

	@Override
	public String getTitle(Player player) {
		return CC.translate("&8» &b&lSelect Kit &7(" + partyEvent.getName() + "&7)");
	}

	@Override
	public int getSize() {
		return 45;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {

		Map<Integer, Button> buttons = new HashMap<>();

		List<Kit> kits = new ArrayList<>();
		for (Kit kit : Kit.getKits()) {
			if (kit.isEnabled()) {
				kits.add(kit);
			}
		}

		int startSlot = 10;

		for (int i = 0; i < kits.size(); i++) {
			Kit kit = kits.get(i);
			int slot = startSlot + i;

			if (slot >= 44) break;

			buttons.put(slot, new SelectKitButton(partyEvent, kit));
		}

		buttons.put(40, new BackButton(new PartyEventSelectEventMenu()));

		return buttons;
	}
	@AllArgsConstructor
	private class SelectKitButton extends Button {

		private PartyEvent partyEvent;
		private Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {

			List<String> lore = new ArrayList<>();
			lore.add("&7Start a &f" + partyEvent.getName() + " &7event");
			lore.add("&7using the &f" + kit.getName() + " &7kit.");
			lore.add("");
			lore.add("&bClick to start");

			return new ItemBuilder(kit.getDisplayIcon())
					.name("&b&l" + kit.getName())
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {

			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
			player.closeInventory();

			Profile profile = Profile.getByUuid(player.getUniqueId());

			if (profile.getParty() == null) {
				player.sendMessage(CC.RED + "You are not in a party.");
				return;
			}

			if (profile.getParty().getPlayers().size() <= 1) {
				player.sendMessage(CC.RED + "You do not have enough players in your party to start an event.");
				return;
			}

			Party party = profile.getParty();
			Arena arena = Arena.getRandomArena(kit);

			if (arena == null) {
				player.sendMessage(CC.RED + "There are no available arenas.");
				return;
			}

			arena.setActive(true);

			Match match;

			if (partyEvent == PartyEvent.FFA) {

				List<GameParticipant<MatchGamePlayer>> participants = new ArrayList<>();

				for (Player partyPlayer : party.getListOfPlayers()) {
					participants.add(new GameParticipant<>(
							new MatchGamePlayer(partyPlayer.getUniqueId(), partyPlayer.getName())));
				}

				match = new BasicFreeForAllMatch(null, kit, arena, participants);

			} else {

				Player partyLeader = party.getLeader();
				Player randomLeader = Bukkit.getPlayer(party.getPlayers().get(1));

				MatchGamePlayer leaderA = new MatchGamePlayer(partyLeader.getUniqueId(), partyLeader.getName());
				MatchGamePlayer leaderB = new MatchGamePlayer(randomLeader.getUniqueId(), randomLeader.getName());

				GameParticipant<MatchGamePlayer> participantA = new TeamGameParticipant<>(leaderA);
				GameParticipant<MatchGamePlayer> participantB = new TeamGameParticipant<>(leaderB);

				List<Player> players = new ArrayList<>(party.getListOfPlayers());
				Collections.shuffle(players);

				for (Player otherPlayer : players) {

					if (participantA.containsPlayer(otherPlayer.getUniqueId()) ||
							participantB.containsPlayer(otherPlayer.getUniqueId())) {
						continue;
					}

					MatchGamePlayer gamePlayer =
							new MatchGamePlayer(otherPlayer.getUniqueId(), otherPlayer.getName());

					if (participantA.getPlayers().size() > participantB.getPlayers().size()) {
						participantB.getPlayers().add(gamePlayer);
					} else {
						participantA.getPlayers().add(gamePlayer);
					}
				}

				match = new BasicTeamMatch(null, kit, arena, false, participantA, participantB);
			}

			match.start();
		}
	}
}