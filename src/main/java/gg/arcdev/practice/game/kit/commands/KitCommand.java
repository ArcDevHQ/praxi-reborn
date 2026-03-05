package gg.arcdev.practice.game.kit.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("kit")
public class KitCommand extends BaseCommand {

    @Default
    @CatchUnknown
    public void onDefault(CommandSender sender) {

        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
        sender.sendMessage(CC.translate("&b&lKit Commands"));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&7» &b/kit create <name> &7- &fCreate a new kit"));
        sender.sendMessage(CC.translate("&7» &b/kit setloadout <kit> &7- &fSet a kit's loadout"));
        sender.sendMessage(CC.translate("&7» &b/kit getloadout <kit> &7- &fReceive a kit's loadout"));
        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
    }

    @Subcommand("create")
    @CommandPermission("praxi.kit.create")
    @Syntax("<name>")
    public void onCreate(Player player, String kitName) {

        if (Kit.getByName(kitName) != null) {
            player.sendMessage(CC.RED + "A kit with that name already exists.");
            return;
        }

        Kit kit = new Kit(kitName);
        kit.save();

        Kit.getKits().add(kit);

        player.sendMessage(CC.GREEN + "You created a new kit.");
    }

    @Subcommand("setloadout")
    @CommandPermission("praxi.kit.setloadout")
    @Syntax("<kit>")
    public void onSetLoadout(Player player, String kitName) {

        Kit kit = Kit.getByName(kitName);

        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        kit.getKitLoadout().setArmor(player.getInventory().getArmorContents());
        kit.getKitLoadout().setContents(player.getInventory().getContents());
        kit.save();

        player.sendMessage(CC.GREEN + "You updated the kit's loadout.");
    }

    @Subcommand("getloadout")
    @CommandPermission("praxi.kit.getloadout")
    @Syntax("<kit>")
    public void onGetLoadout(Player player, String kitName) {

        Kit kit = Kit.getByName(kitName);

        if (kit == null) {
            player.sendMessage(CC.RED + "A kit with that name does not exist.");
            return;
        }

        player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
        player.getInventory().setContents(kit.getKitLoadout().getContents());
        player.updateInventory();

        player.sendMessage(CC.GREEN + "You received the kit's loadout.");
    }
}