package gg.arcdev.practice.util.menu.category;

import lombok.RequiredArgsConstructor;
import gg.arcdev.practice.util.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class CategoryButton<T> extends Button {

    private final CategoryPaginatedMenu<T> menu;
    private final Category<T> category;

    @Override
    public ItemStack getButtonItem(Player player) {

        boolean active = menu.getActiveCategory() != null &&
                menu.getActiveCategory().equals(category);

        return active
                ? glow(category.getIcon())
                : category.getIcon();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
        if(menu.getActiveCategory().equals(category)) {
            Button.playFail(player);
            return;
        }
        menu.setActiveCategory(category);
        Button.playSuccess(player);

    }

    @Override
    public boolean shouldUpdate(Player player, ClickType clickType) {
        return true;
    }

}
