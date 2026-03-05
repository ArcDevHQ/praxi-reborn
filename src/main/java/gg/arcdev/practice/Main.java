package gg.arcdev.practice;

import gg.arcdev.practice.core.CommandRegistry;
import co.aikar.commands.PaperCommandManager;
import gg.arcdev.practice.core.adapter.CoreManager;
import gg.arcdev.practice.core.scoreboard.ScoreboardListener;
import gg.arcdev.practice.game.kit.KitEditorListener;
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
import gg.arcdev.practice.util.assemble.Assemble;
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

	@Getter private ScoreboardAdapter adapter;
	@Getter private Assemble assemble;

	@Override
	public void onEnable() {
		practice = this;

		initializeCore();
		loadConfigs();
		initializeServices();
		initializeModules();
		registerListeners();
		removeCrafting();
		setupWorlds();
	}

	@Override
	public void onDisable() {
		Match.cleanup();
	}

	private void initializeCore() {
		CoreManager.initialize(this);
	}

	private void loadConfigs() {
		mainConfig = new BasicConfigurationFile(this, "config");
		arenasConfig = new BasicConfigurationFile(this, "arenas");
		kitsConfig = new BasicConfigurationFile(this, "kits");
		eventsConfig = new BasicConfigurationFile(this, "events");
	}

	private void initializeServices() {
		essentials = new Essentials(this);
		loadMongo();
	}

	private void initializeModules() {
		Hotbar.init();
		Kit.init();
		Arena.init();
		Profile.init();
		Match.init();
		Party.init();
		Event.init();
		EventGameMap.init();

		adapter = new ScoreboardAdapter();
		assemble = new Assemble(this, adapter);

		new QueueThread().start();

		commandManager = new PaperCommandManager(this);
		commandManager.enableUnstableAPI("help");
		new CommandRegistry(this).registerCommands();
	}

	private void registerListeners() {
		Arrays.asList(
				new KitEditorListener(),
				new PartyListener(),
				new ProfileListener(),
				new MatchListener(),
				new QueueListener(),
				new ArenaListener(),
				new EventGameListener(),
				new MenuListener(),
				new ScoreboardListener(assemble)
		).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
	}

	private void removeCrafting() {
		Arrays.asList(
				Material.WORKBENCH,
				Material.STICK,
				Material.WOOD_PLATE,
				Material.WOOD_BUTTON,
				Material.SNOW_BLOCK
		).forEach(InventoryUtil::removeCrafting);
	}

	private void setupWorlds() {
		getServer().getWorlds().forEach(world -> {
			world.setDifficulty(Difficulty.HARD);
			essentials.clearEntities(world);
		});
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