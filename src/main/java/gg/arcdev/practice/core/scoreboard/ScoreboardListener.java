package gg.arcdev.practice.core.scoreboard;

import gg.arcdev.practice.util.assemble.Assemble;
import gg.arcdev.practice.util.assemble.AssembleBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardListener implements Listener {

private final Assemble assemble;

public ScoreboardListener(Assemble assemble) {
    this.assemble = assemble;
}

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!assemble.getBoards().containsKey(event.getPlayer().getUniqueId())) {
            assemble.getBoards().put((event.getPlayer().getUniqueId()), new AssembleBoard(event.getPlayer(), assemble));
        }
    }
}
