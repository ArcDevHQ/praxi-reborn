package gg.arcdev.practice.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder implements Listener {

    private final ItemStack is;

    public ItemBuilder(Material mat) {
        is = new ItemStack(mat);
    }

    public ItemBuilder(ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String name) {
        ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);

        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(String... lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = is.getItemMeta();

        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }

        meta.setLore(toSet);
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = is.getItemMeta();

        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }

        meta.setLore(toSet);
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        is.setDurability((short) durability);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment) {
        is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(Material material) {
        is.setType(material);
        return this;
    }

    public ItemBuilder texture(String texture) {
        applyHeadTexture(texture);
        return this;
    }

    public ItemBuilder headTexture(String texture) {
        return texture(texture);
    }

    public ItemBuilder owner(String name) {
        ensureHeadItem();

        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwner(name);
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder player(String name) {
        return owner(name);
    }

    public ItemBuilder head(String textureOrName) {
        if (looksLikeTexture(textureOrName)) {
            return texture(textureOrName);
        }

        return owner(textureOrName);
    }

    public ItemBuilder clearLore() {
        ItemMeta meta = is.getItemMeta();

        meta.setLore(new ArrayList<>());
        is.setItemMeta(meta);

        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (Enchantment e : is.getEnchantments().keySet()) {
            is.removeEnchantment(e);
        }

        return this;
    }

    public ItemStack build() {
        return is;
    }

    private void ensureHeadItem() {
        is.setType(Material.SKULL_ITEM);
        is.setDurability((short) 3);
    }

    private void applyHeadTexture(String texture) {
        ensureHeadItem();

        SkullMeta meta = (SkullMeta) is.getItemMeta();
        GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8)), null);
        profile.getProperties().put("textures", new Property("textures", normalizeTexture(texture)));

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to apply head texture", exception);
        }

        is.setItemMeta(meta);
    }

    private boolean looksLikeTexture(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        String trimmed = value.trim();
        return trimmed.startsWith("http://")
                || trimmed.startsWith("https://")
                || trimmed.contains("textures.minecraft.net")
                || trimmed.length() > 32
                || trimmed.startsWith("eyJ0ZXh0dXJlcy");
    }

    private String normalizeTexture(String texture) {
        String trimmed = texture.trim();

        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + trimmed + "\"}}}";
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        }

        return trimmed;
    }

}
