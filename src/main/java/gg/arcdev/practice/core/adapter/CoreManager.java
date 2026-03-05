package gg.arcdev.practice.core.adapter;

import lombok.Getter;
import lombok.Setter;
import gg.arcdev.practice.core.adapter.impl.Default;
import org.bukkit.plugin.Plugin;

public class CoreManager {

    @Getter @Setter private static CoreManager instance;
    @Getter @Setter private Plugin plugin;
    @Getter @Setter private String coreSystem;
    @Getter @Setter private Core core;

    private CoreManager() {
        loadCore();
    }

    public static void initialize(Plugin plugin) {
        if (instance == null) {
            instance = new CoreManager();
            instance.setPlugin(plugin);
        }
    }

    private void loadCore() {
        this.setCore(new Default());
        this.setCoreSystem("Default");
    }
}