package gg.arcdev.practice.core.config;

import gg.arcdev.practice.util.config.BasicConfigurationFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardConfig {

    private final BasicConfigurationFile file;

    public ScoreboardConfig(JavaPlugin plugin) {
        this.file = new BasicConfigurationFile(plugin, "scoreboard");
    }

    public BasicConfigurationFile getFile() {
        return file;
    }

    public boolean isEnabled() {
        return file.getBoolean("SCOREBOARD.ENABLED");
    }

    public String getTitle() {
        return file.getString("SCOREBOARD.TITLE");
    }

    public String getFooter() {
        return file.getString("SCOREBOARD.FOOTER");
    }

    public java.util.List<String> getLines(String state) {
        return file.getStringList("SCOREBOARD." + state.toUpperCase() + ".LINES");
    }
}