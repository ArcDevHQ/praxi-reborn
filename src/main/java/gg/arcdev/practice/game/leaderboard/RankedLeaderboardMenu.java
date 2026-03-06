package gg.arcdev.practice.game.leaderboard;

import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.game.kit.Kit;
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

public class RankedLeaderboardMenu extends Menu {

    {
        setPlaceholder(true);
        setAutoUpdate(true);

        ItemStack placeholder = new ItemBuilder(Material.STAINED_GLASS_PANE)
                .durability(7)
                .name(" ")
                .build();
        setPlaceholderButton(new DisplayButton(placeholder, true));
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&8» &b&lRanked Leaderboards");
    }

    @Override
    public int getSize() {
        return 45;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        List<Kit> kits = new ArrayList<>(Kit.getKits());
        int startSlot = 10;

        for (int i = 0; i < kits.size(); i++) {
            Kit kit = kits.get(i);
            int slot = startSlot + i;

            if (slot >= 44) break;

            buttons.put(slot, new LeaderboardKitButton(kit));
        }

        return buttons;
    }

    @AllArgsConstructor
    private static class LeaderboardKitButton extends Button {

        private Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {

            List<String> lore = new ArrayList<>();
            lore.add("&7Top 10 players for &f" + kit.getName());
            lore.add("");

            List<Profile> profiles = new ArrayList<>(Profile.getProfiles().values());
            profiles.sort((a, b) -> b.getKitData().get(kit).getElo() - a.getKitData().get(kit).getElo());

            int rank = 1;
            for (Profile profile : profiles) {
                if (rank > 10) break;
                int elo = profile.getKitData().get(kit).getElo();
                Player p = profile.getPlayer();
                String name = (p != null) ? p.getName() : profile.getUuid().toString().substring(0, 6);
                lore.add("&b" + rank + ".) &f" + name + " &7[&b" + elo + "&7]");
                rank++;
            }

            if (lore.size() == 2) {
                lore.add("&7No players have ranked ELO yet!");
            }

            return new ItemBuilder(kit.getDisplayIcon())
                    .name("&b&l" + kit.getName())
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
        }
    }
}