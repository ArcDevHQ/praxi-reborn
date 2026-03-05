package gg.arcdev.practice;

import gg.arcdev.practice.core.CommandRegistry;
import co.aikar.commands.PaperCommandManager;
import gg.arcdev.practice.core.adapter.CoreManager;
import gg.arcdev.practice.game.kit.KitEditorListener;
import gg.arcdev.practice.util.aether.Aether;
import gg.arcdev.practice.util.aether.AetherOptions;
import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.arena.ArenaListener;
import gg.arcdev.practice.core.essentials.Essentials;
import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.game.event.game.EventGameListener;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.core.hotbar.Hotbar;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.game.match.MatchListener;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.game.party.PartyListener;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileListener;
import gg.arcdev.practice.game.queue.QueueListener;
import gg.arcdev.practice.game.queue.QueueThread;
import gg.arcdev.practice.core.scoreboard.ScoreboardAdapter;
import gg.arcdev.practice.util.InventoryUtil;
import gg.arcdev.practice.util.config.BasicConfigurationFile;
import gg.arcdev.practice.util.menu.MenuListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Main extends JavaPlugin {

	private static Main practice;

	@Getter private BasicConfigurationFile mainConfig;
	@Getter private BasicConfigurationFile arenasConfig;
	@Getter private BasicConfigurationFile kitsConfig;
	@Getter private BasicConfigurationFile eventsConfig;
	@Getter private MongoDatabase mongoDatabase;
	@Getter private Essentials essentials;
	@Getter private PaperCommandManager commandManager;

	@Override
	public void onEnable() {
		practice = this;

		CoreManager.initialize(this);

		getServer().getPluginManager().registerEvents(new MenuListener(), this);

		mainConfig = new BasicConfigurationFile(this, "config");
		arenasConfig = new BasicConfigurationFile(this, "arenas");
		kitsConfig = new BasicConfigurationFile(this, "kits");
		eventsConfig = new BasicConfigurationFile(this, "events");

		this.essentials = new Essentials(this);
		loadMongo();

		Hotbar.init();
		Kit.init();
		Arena.init();
		Profile.init();
		Match.init();
		Party.init();
		Event.init();
		EventGameMap.init();

		new Aether(this, new ScoreboardAdapter(), new AetherOptions().hook(true));
		new QueueThread().start();

		commandManager = new PaperCommandManager(this);
		commandManager.enableUnstableAPI("help");

		new CommandRegistry(this).registerCommands();

		Arrays.asList(
				new KitEditorListener(),
				new PartyListener(),
				new ProfileListener(),
				new MatchListener(),
				new QueueListener(),
				new ArenaListener(),
				new EventGameListener()
		).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

		Arrays.asList(
				Material.WORKBENCH,
				Material.STICK,
				Material.WOOD_PLATE,
				Material.WOOD_BUTTON,
				Material.SNOW_BLOCK
		).forEach(InventoryUtil::removeCrafting);

		getServer().getWorlds().forEach(world -> {
			world.setDifficulty(Difficulty.HARD);
			getEssentials().clearEntities(world);
		});
	}

	@Override
	public void onDisable() {
		Match.cleanup();
	}

	private void loadMongo() {
		if (mainConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
			mongoDatabase = new MongoClient(
					new ServerAddress(mainConfig.getString("MONGO.HOST"), mainConfig.getInteger("MONGO.PORT")),
					MongoCredential.createCredential(
							mainConfig.getString("MONGO.AUTHENTICATION.USERNAME"),
							mainConfig.getString("MONGO.AUTHENTICATION.ADMIN"),
							mainConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()
					),
					MongoClientOptions.builder().build()
			).getDatabase(mainConfig.getString("MONGO.DATABASE"));
		} else {
			mongoDatabase = new MongoClient(
					mainConfig.getString("MONGO.HOST"),
					mainConfig.getInteger("MONGO.PORT")
			).getDatabase(mainConfig.getString("MONGO.DATABASE"));
		}
	}

	public static Main getInstance() {
		return practice;
	}
}