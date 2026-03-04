package gg.arcdev.practice.core.adapter;

import lombok.Getter;
import lombok.Setter;
import gg.arcdev.practice.core.adapter.impl.Default;
import org.bukkit.plugin.Plugin;

public class CoreManager {

    @Getter @Setter public static CoreManager instance;
    @Getter @Setter private Plugin plugin;
    @Getter @Setter private String coreSystem;
    @Getter @Setter private Core core;

    public CoreManager() {
        instance = this;
        loadRank();
    }

    public void loadRank() {
        this.setCore(new Default());
        setCoreSystem("Default");
    }

}
