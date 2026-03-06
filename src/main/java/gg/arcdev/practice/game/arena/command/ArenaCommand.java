package gg.arcdev.practice.game.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.arena.*;
import gg.arcdev.practice.game.arena.generator.ArenaGenerator;
import gg.arcdev.practice.game.arena.Schematic;
import gg.arcdev.practice.game.arena.impl.SharedArena;
import gg.arcdev.practice.game.arena.impl.StandaloneArena;
import gg.arcdev.practice.game.arena.selection.Selection;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.util.CC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@CommandAlias("arena")
@CommandPermission("praxi.admin.arena")
public class ArenaCommand extends BaseCommand {

    @Default
    public void onDefault(CommandSender sender) {

        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
        sender.sendMessage(CC.translate("&b&lArena Commands"));
        sender.sendMessage(" ");

        sender.sendMessage(CC.translate("&7» &b/arena create <name> &7- &fCreate a new arena"));
        sender.sendMessage(CC.translate("&7» &b/arena delete <name> &7- &fDelete an existing arena"));
        sender.sendMessage(CC.translate("&7» &b/arena addkit <arena> <kit> &7- &fAdd a kit to an arena"));
        sender.sendMessage(CC.translate("&7» &b/arena removekit <arena> <kit> &7- &fRemove a kit from an arena"));
        sender.sendMessage(CC.translate("&7» &b/arena generate &7- &fGenerate arenas from schematics"));
        sender.sendMessage(CC.translate("&7» &b/arena genhelper &7- &fPlace generator helper block"));
        sender.sendMessage(CC.translate("&7» &b/arena setspawn <arena> <a|b> &7- &fSet arena spawn locations"));
        sender.sendMessage(CC.translate("&7» &b/arena status <arena> &7- &fView arena setup status"));
        sender.sendMessage(CC.translate("&7» &b/arena save &7- &fSave all arenas"));
        sender.sendMessage(CC.translate("&7» &b/arenas &7- &fView existing arenas"));

        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
    }

    /* ===================================================== */
    /* CREATE */
    /* ===================================================== */

    @Subcommand("create")
    @Syntax("<name>")
    public void onCreate(Player player, String arenaName) {

        if (Arena.getByName(arenaName) != null) {
            player.sendMessage(CC.RED + "An arena with that name already exists.");
            return;
        }

        Selection selection = Selection.createOrGetSelection(player);

        if (!selection.isFullObject()) {
            player.sendMessage(CC.RED + "Your selection is incomplete.");
            return;
        }

        Arena arena = new SharedArena(arenaName, Objects.requireNonNull(selection.getPoint1()), Objects.requireNonNull(selection.getPoint2()));
        Arena.getArenas().add(arena);
        arena.save();

        player.sendMessage(CC.GOLD + "Created new arena \"" + arenaName + "\"");
    }

    /* ===================================================== */
    /* DELETE */
    /* ===================================================== */

    @Subcommand("delete")
    @Syntax("<name>")
    public void onDelete(Player player, String arenaName) {
        Arena arena = Arena.getByName(arenaName);

        if (arena == null) {
            player.sendMessage(CC.RED + "An arena with that name does not exist.");
            return;
        }

        arena.delete();
        player.sendMessage(CC.GOLD + "Deleted arena \"" + arena.getName() + "\"");
    }

    /* ===================================================== */
    /* ADD KIT */
    /* ===================================================== */

    @Subcommand("addkit")
    @Syntax("<arena> <kit>")
    public void onAddKit(CommandSender sender, String arenaName, String kitName) {

        Arena arena = Arena.getByName(arenaName);
        Kit kit = Kit.getByName(kitName);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "An arena with that name does not exist.");
            return;
        }

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "A kit with that name does not exist.");
            return;
        }

        arena.getKits().add(kit.getName());
        arena.save();

        sender.sendMessage(ChatColor.GOLD + "Added kit \"" + kit.getName()
                + "\" to arena \"" + arena.getName() + "\"");
    }

    /* ===================================================== */
    /* REMOVE KIT */
    /* ===================================================== */

    @Subcommand("removekit")
    @Syntax("<arena> <kit>")
    public void onRemoveKit(CommandSender sender, String arenaName, String kitName) {

        Arena arena = Arena.getByName(arenaName);
        Kit kit = Kit.getByName(kitName);

        if (arena == null) {
            sender.sendMessage(ChatColor.RED + "An arena with that name does not exist.");
            return;
        }

        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "A kit with that name does not exist.");
            return;
        }

        arena.getKits().remove(kit.getName());
        arena.save();

        sender.sendMessage(ChatColor.GOLD + "Removed kit \"" + kit.getName()
                + "\" from arena \"" + arena.getName() + "\"");
    }

    /* ===================================================== */
    /* SAVE */
    /* ===================================================== */

    @Subcommand("save")
    public void onSave(CommandSender sender) {
        for (Arena arena : Arena.getArenas()) {
            arena.save();
        }

        sender.sendMessage(ChatColor.GREEN + "Saved all arenas!");
    }

    /* ===================================================== */
    /* GENERATE */
    /* ===================================================== */

    @Subcommand("generate")
    public void onGenerate(Player player) {

        File schematicsFolder = resolveSchematicsFolder();

        if (schematicsFolder == null || !schematicsFolder.exists()) {
            player.sendMessage(CC.RED + "No schematics folder found. Checked WorldEdit, FastAsyncWorldEdit, and Practice.");
            return;
        }

        File[] files = schematicsFolder.listFiles();
        if (files == null) {
            player.sendMessage(CC.RED + "No schematic files found.");
            return;
        }

        for (File file : files) {

            if (file.isDirectory() || !file.getName().endsWith(".schematic"))
                continue;

            boolean duplicate = file.getName().endsWith("_duplicate.schematic");
            String name = file.getName()
                    .replace(".schematic", "")
                    .replace("_duplicate", "");

            Arena parent = Arena.getByName(name);

            if (parent != null && !(parent instanceof StandaloneArena)) {
                player.sendMessage(CC.RED + "Skipping " + name + " because it's not duplicate and already exists.");
                continue;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        new ArenaGenerator(
                                name,
                                player.getWorld(),
                                new Schematic(file),
                                duplicate
                                        ? (parent != null ? ArenaType.DUPLICATE : ArenaType.STANDALONE)
                                        : ArenaType.SHARED
                        ).generate(file, (StandaloneArena) parent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTask(Main.getInstance());
        }

        player.sendMessage(CC.GREEN + "Generating arenas in world " + player.getWorld().getName()
                + " from " + schematicsFolder.getPath() + "...");
    }

    private File resolveSchematicsFolder() {
        File pluginsFolder = Main.getInstance().getDataFolder().getParentFile();

        List<File> candidates = Arrays.asList(
                new File(pluginsFolder, "WorldEdit/schematics"),
                new File(pluginsFolder, "FastAsyncWorldEdit/schematics"),
                new File(Main.getInstance().getDataFolder(), "schematics")
        );

        for (File candidate : candidates) {
            if (candidate.exists() && candidate.isDirectory()) {
                return candidate;
            }
        }

        return null;
    }

    @Subcommand("genhelper")
    public void onGenHelper(Player player) {

        Block origin = player.getLocation().getBlock();
        Block up = origin.getRelative(BlockFace.UP);

        origin.setType(Material.SPONGE);
        up.setType(Material.SIGN_POST);

        if (up.getState() instanceof Sign) {
            Sign sign = (Sign) up.getState();
            sign.setLine(0, String.valueOf((int) player.getLocation().getPitch()));
            sign.setLine(1, String.valueOf((int) player.getLocation().getYaw()));
            sign.update();

            player.sendMessage(CC.GREEN + "Generator helper placed.");
        }
    }

    @Subcommand("wand|selection")
    public void onSelectionToggle(Player player) {

        if (player.getInventory().first(Selection.SELECTION_WAND) != -1) {
            player.getInventory().remove(Selection.SELECTION_WAND);
            player.sendMessage(CC.RED + "Selection wand removed.");
        } else {
            player.getInventory().addItem(Selection.SELECTION_WAND);
            player.sendMessage(CC.GREEN + "Selection wand given.");
        }

        player.updateInventory();
    }

    @Subcommand("setspawn")
    @Syntax("<arena> <a|b>")
    public void onSetSpawn(Player player, String arenaName, String pos) {

        Arena arena = Arena.getByName(arenaName);

        if (arena == null) {
            player.sendMessage(CC.RED + "An arena with that name does not exist.");
            return;
        }

        if (pos.equalsIgnoreCase("a")) {
            arena.setSpawnA(player.getLocation());
        } else if (pos.equalsIgnoreCase("b")) {
            arena.setSpawnB(player.getLocation());
        } else {
            player.sendMessage(CC.RED + "Invalid spawn point. Try \"a\" or \"b\".");
            return;
        }

        arena.save();

        player.sendMessage(CC.GOLD + "Updated spawn point \"" + pos +
                "\" for arena \"" + arena.getName() + "\"");
    }

    @Subcommand("status")
    @Syntax("<arena>")
    public void onStatus(CommandSender sender, String arenaName) {

        Arena arena = Arena.getByName(arenaName);

        if (arena == null) {
            sender.sendMessage(CC.RED + "An arena with that name does not exist.");
            return;
        }

        sender.sendMessage(CC.GOLD + CC.BOLD + "Arena Status " + CC.GRAY + "(" +
                (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + ")");

        sender.sendMessage(CC.GREEN + "Cuboid Lower Location: " + CC.YELLOW +
                (arena.getLowerCorner() == null ? "✗" : "✓"));

        sender.sendMessage(CC.GREEN + "Cuboid Upper Location: " + CC.YELLOW +
                (arena.getUpperCorner() == null ? "✗" : "✓"));

        sender.sendMessage(CC.GREEN + "Spawn A Location: " + CC.YELLOW +
                (arena.getSpawnA() == null ? "✗" : "✓"));

        sender.sendMessage(CC.GREEN + "Spawn B Location: " + CC.YELLOW +
                (arena.getSpawnB() == null ? "✗" : "✓"));

        sender.sendMessage(CC.GREEN + "Kits: " + CC.YELLOW +
                (arena.getKits().isEmpty() ? "None" : String.join(", ", arena.getKits())));
    }
}
