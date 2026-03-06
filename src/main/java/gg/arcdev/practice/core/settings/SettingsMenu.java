package gg.arcdev.practice.core.settings;

import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.settings.buttons.ToggleDuelRequestsButton;
import gg.arcdev.practice.core.settings.buttons.ToggleScoreboardButton;
import gg.arcdev.practice.core.settings.buttons.ToggleSpectatorsButton;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.Menu;
import gg.arcdev.practice.util.menu.button.DisplayButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenu extends Menu {

    {
        setPlaceholder(true);
        setAutoUpdate(true);

        ItemStack placeholder = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        setPlaceholderButton(new DisplayButton(placeholder, true));
    }

    @Override
    public String getTitle(Player player) {
        return "§8» §b§lSettings";
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        buttons.put(0, new ToggleDuelRequestsButton(profile));
        buttons.put(1, new ToggleScoreboardButton());
        buttons.put(2, new ToggleSpectatorsButton());

        return buttons;
    }
}