package gg.arcdev.practice.util.menu.category;

import lombok.Getter;
import lombok.Setter;
import gg.arcdev.practice.util.menu.Button;
import gg.arcdev.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CategoryPaginatedMenu<T> extends PaginatedMenu {

    @Getter
    private final List<Category<T>> categories = new ArrayList<>();

    @Getter @Setter
    private Category<T> activeCategory;

    {
        categories.addAll(getCategories());

        if (!categories.isEmpty()) {
            activeCategory = categories.get(0);
        }
    }

    public abstract List<Category<T>> getCategories();

    public abstract Map<Integer, Button> getCategoryButtons(Player player);

    public abstract List<T> getObjects(Player player);

    public abstract Button getButton(Player player, T object);

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return getCategoryButtons(player);
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {

        Map<Integer, Button> buttons = new HashMap<>();

        List<T> filtered = new ArrayList<>();

        for (T object : getObjects(player)) {

            if (activeCategory == null || activeCategory.test(object)) {
                filtered.add(object);
            }
        }

        int index = 0;

        for (T object : filtered) {
            buttons.put(index++, getButton(player, object));
        }

        return buttons;
    }

}
