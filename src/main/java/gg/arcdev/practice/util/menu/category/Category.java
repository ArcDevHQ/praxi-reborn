package gg.arcdev.practice.util.menu.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class Category<T> {

    private final String name;
    private final ItemStack icon;
    private final Predicate<T> predicate;

    public boolean test(T object) {
        return predicate.test(object);
    }

}
