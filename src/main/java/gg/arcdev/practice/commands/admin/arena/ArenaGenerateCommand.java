package gg.arcdev.practice.commands.admin.arena;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.arena.ArenaType;
import gg.arcdev.practice.game.arena.generator.ArenaGenerator;
import gg.arcdev.practice.game.arena.generator.Schematic;
import gg.arcdev.practice.game.arena.impl.StandaloneArena;
import gg.arcdev.practice.util.command.command.CommandMeta;
import java.io.File;

import gg.arcdev.practice.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

@CommandMeta(label = "arena generate", permission = "praxi.admin.arena", async = true)
public class ArenaGenerateCommand {

	public void execute(CommandSender sender) {
		File schematicsFolder = new File(Main.get().getDataFolder().getPath() + File.separator + "schematics");

		if (!schematicsFolder.exists()) {
			sender.sendMessage(CC.RED + "The schematics folder does not exist.");
			return;
		}

		for (File file : schematicsFolder.listFiles()) {
			if (!file.isDirectory() && file.getName().contains(".schematic")) {
				boolean duplicate = file.getName().endsWith("_duplicate.schematic");

				String name = file.getName()
				                  .replace(".schematic", "")
				                  .replace("_duplicate", "");

				Arena parent = Arena.getByName(name);

				if (parent != null) {
					if (!(parent instanceof StandaloneArena)) {
						System.out.println("Skipping " + name + " because it's not duplicate and an arena with that name already exists.");
						continue;
					}
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							new ArenaGenerator(name, Bukkit.getWorlds().get(0), new Schematic(file), duplicate ?
									(parent != null ? ArenaType.DUPLICATE : ArenaType.STANDALONE) : ArenaType.SHARED)
									.generate(file, (StandaloneArena) parent);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}.runTask(Main.get());
			}
		}

		sender.sendMessage(CC.GREEN + "Generating arenas... See console for details.");
	}

}
