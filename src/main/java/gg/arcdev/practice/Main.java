package gg.arcdev.practice;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
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
import gg.arcdev.practice.integration.lunar.FightTeamService;
import gg.arcdev.practice.integration.lunar.LobbyTeamService;
import gg.arcdev.practice.core.scoreboard.ScoreboardAdapter;
import gg.arcdev.practice.util.InventoryUtil;
import gg.arcdev.practice.util.assemble.Assemble;
import gg.arcdev.practice.util.config.BasicConfigurationFile;
import gg.arcdev.practice.util.menu.MenuListener;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main extends JavaPlugin {

	private static Main practice;

	@Getter private BasicConfigurationFile mainConfig;
	@Getter private BasicConfigurationFile arenasConfig;
	@Getter private BasicConfigurationFile kitsConfig;
	@Getter private BasicConfigurationFile eventsConfig;
	@Getter private MongoClient mongoClient;
	@Getter private MongoDatabase mongoDatabase;
	@Getter private Essentials essentials;
	@Getter private PaperCommandManager commandManager;

	@Getter private ScoreboardAdapter adapter;
	@Getter private Assemble assemble;
	@Getter private FightTeamService fightTeamService;
	@Getter private LobbyTeamService lobbyTeamService;

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
		for (Arena arena : Arena.getArenas()) {
			arena.save();
		}

		Match.cleanup();
		if (fightTeamService != null) {
			fightTeamService.shutdown();
			fightTeamService = null;
		}
		if (lobbyTeamService != null) {
			lobbyTeamService.shutdown();
			lobbyTeamService = null;
		}
		if (mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
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
		preloadArenaWorlds();
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
		fightTeamService = new FightTeamService(this);
		lobbyTeamService = new LobbyTeamService(this);
		fightTeamService.start();
		lobbyTeamService.start();

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

	private void preloadArenaWorlds() {
		ConfigurationSection section = arenasConfig.getConfiguration().getConfigurationSection("arenas");
		if (section == null) {
			return;
		}

		Set<String> worldNames = new LinkedHashSet<>();

		for (String arenaName : section.getKeys(false)) {
			String path = "arenas." + arenaName;
			addWorldName(worldNames, arenasConfig.getConfiguration().getString(path + ".cuboid.location1"));
			addWorldName(worldNames, arenasConfig.getConfiguration().getString(path + ".cuboid.location2"));
			addWorldName(worldNames, arenasConfig.getConfiguration().getString(path + ".spawnA"));
			addWorldName(worldNames, arenasConfig.getConfiguration().getString(path + ".spawnB"));
		}

		File worldContainer = Bukkit.getWorldContainer();

		for (String worldName : worldNames) {
			if (Bukkit.getWorld(worldName) != null) {
				continue;
			}

			File worldFolder = new File(worldContainer, worldName);
			if (!worldFolder.exists() || !worldFolder.isDirectory()) {
				getLogger().warning("Arena world \"" + worldName + "\" is referenced in arenas.yml but is not loaded.");
				continue;
			}

			Bukkit.createWorld(new WorldCreator(worldName));
		}
	}

	private void addWorldName(Set<String> worldNames, String serializedLocation) {
		if (serializedLocation == null || serializedLocation.equalsIgnoreCase("null")) {
			return;
		}

		int separatorIndex = serializedLocation.indexOf(':');
		if (separatorIndex <= 0) {
			return;
		}

		worldNames.add(serializedLocation.substring(0, separatorIndex));
	}

	private void loadMongo() {
		String mongoUri = mainConfig.getStringOrDefault("MONGO.URI", "").trim();
		String databaseName = mainConfig.getStringOrDefault("MONGO.DATABASE", "").trim();

		if (mongoUri.isEmpty()) {
			throw new IllegalStateException("MONGO.URI must be configured in config.yml");
		}

		if (databaseName.isEmpty()) {
			throw new IllegalStateException("MONGO.DATABASE must be configured in config.yml");
		}

		mongoClient = new MongoClient(new MongoClientURI(mongoUri));
		mongoDatabase = mongoClient.getDatabase(databaseName);
	}

	public static Main getInstance() {
		return practice;
	}
}
