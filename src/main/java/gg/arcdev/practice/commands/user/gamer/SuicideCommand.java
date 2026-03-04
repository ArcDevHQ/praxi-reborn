package gg.arcdev.practice.commands.user.gamer;

import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = "suicide")
public class SuicideCommand {
    public void execute(Player player) {
        player.setHealth(0);
        player.sendMessage(CC.translate("&cYou have killed yourself! Oh noes"));
    }
}
