package gg.arcdev.practice.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.util.CC;
import org.bukkit.entity.Player;

@CommandAlias("setspawn")
@CommandPermission("praxi.setspawn")
public class SetSpawnCommand extends BaseCommand {

    @Default
    public void onSetSpawn(Player player) {
        Main.get().getEssentials().setSpawn(player.getLocation());
        player.sendMessage(CC.translate("&bSpawn set successfully!"));
    }
}