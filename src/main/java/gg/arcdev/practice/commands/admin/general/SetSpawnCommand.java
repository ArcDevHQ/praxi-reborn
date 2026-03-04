package gg.arcdev.practice.commands.admin.general;

import gg.arcdev.practice.util.command.command.CommandMeta;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandMeta(label = { "setspawn" }, permission = "praxi.setspawn")
public class SetSpawnCommand
{
    public void execute(Player player) {
        Main.get().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&bSpawn set successfully!"));
    }
}
