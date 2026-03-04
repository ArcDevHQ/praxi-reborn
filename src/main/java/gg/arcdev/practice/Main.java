package gg.arcdev.practice;

import gg.arcdev.practice.util.aether.Aether;
import gg.arcdev.practice.util.aether.AetherOptions;
import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.arena.ArenaListener;
import gg.arcdev.practice.game.arena.ArenaType;
import gg.arcdev.practice.game.arena.ArenaTypeAdapter;
import gg.arcdev.practice.game.arena.ArenaTypeTypeAdapter;
import gg.arcdev.practice.commands.admin.general.SetSpawnCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaAddKitCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaCreateCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaDeleteCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaGenHelperCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaGenerateCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaRemoveKitCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaSaveCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaSelectionCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaSetSpawnCommand;
import gg.arcdev.practice.commands.admin.arena.ArenaStatusCommand;
import gg.arcdev.practice.commands.admin.arena.ArenasCommand;
import gg.arcdev.practice.commands.user.gamer.SuicideCommand;
import gg.arcdev.practice.core.essentials.Essentials;
import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.game.event.EventTypeAdapter;
import gg.arcdev.practice.commands.event.admin.EventAdminCommand;
import gg.arcdev.practice.commands.event.admin.EventHelpCommand;
import gg.arcdev.practice.commands.event.admin.EventSetLobbyCommand;
import gg.arcdev.practice.game.event.game.EventGameListener;
import gg.arcdev.practice.commands.event.user.EventCancelCommand;
import gg.arcdev.practice.commands.event.user.EventClearCooldownCommand;
import gg.arcdev.practice.commands.event.user.EventForceStartCommand;
import gg.arcdev.practice.commands.event.user.EventHostCommand;
import gg.arcdev.practice.commands.event.user.EventInfoCommand;
import gg.arcdev.practice.commands.event.user.EventJoinCommand;
import gg.arcdev.practice.commands.event.user.EventLeaveCommand;
import gg.arcdev.practice.commands.event.admin.EventsCommand;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.game.event.game.map.EventGameMapTypeAdapter;
import gg.arcdev.practice.commands.event.map.EventMapCreateCommand;
import gg.arcdev.practice.commands.event.map.EventMapDeleteCommand;
import gg.arcdev.practice.commands.event.map.EventMapSetSpawnCommand;
import gg.arcdev.practice.commands.event.map.EventMapStatusCommand;
import gg.arcdev.practice.commands.event.map.EventMapsCommand;
import gg.arcdev.practice.commands.event.admin.EventAddMapCommand;
import gg.arcdev.practice.commands.event.admin.EventRemoveMapCommand;
import gg.arcdev.practice.commands.event.vote.EventMapVoteCommand;
import gg.arcdev.practice.commands.admin.match.MatchTestCommand;
import gg.arcdev.practice.commands.user.match.ViewInventoryCommand;
import gg.arcdev.practice.commands.user.duels.DuelAcceptCommand;
import gg.arcdev.practice.commands.user.duels.DuelCommand;
import gg.arcdev.practice.commands.user.duels.RematchCommand;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.kit.KitTypeAdapter;
import gg.arcdev.practice.commands.admin.kits.KitCreateCommand;
import gg.arcdev.practice.commands.admin.kits.KitGetLoadoutCommand;
import gg.arcdev.practice.commands.admin.kits.KitsCommand;
import gg.arcdev.practice.commands.admin.kits.KitSetLoadoutCommand;
import gg.arcdev.practice.game.kit.KitEditorListener;
import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.commands.user.match.SpectateCommand;
import gg.arcdev.practice.commands.user.match.StopSpectatingCommand;
import gg.arcdev.practice.game.match.MatchListener;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.commands.user.party.PartyChatCommand;
import gg.arcdev.practice.commands.user.party.PartyCloseCommand;
import gg.arcdev.practice.commands.user.party.PartyCreateCommand;
import gg.arcdev.practice.commands.user.party.PartyDisbandCommand;
import gg.arcdev.practice.commands.user.party.PartyHelpCommand;
import gg.arcdev.practice.commands.user.party.PartyInfoCommand;
import gg.arcdev.practice.commands.user.party.PartyInviteCommand;
import gg.arcdev.practice.commands.user.party.PartyJoinCommand;
import gg.arcdev.practice.commands.user.party.PartyKickCommand;
import gg.arcdev.practice.commands.user.party.PartyLeaveCommand;
import gg.arcdev.practice.commands.user.party.PartyOpenCommand;
import gg.arcdev.practice.game.party.PartyListener;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.commands.donater.FlyCommand;
import gg.arcdev.practice.core.profile.ProfileListener;
import gg.arcdev.practice.core.hotbar.Hotbar;
import gg.arcdev.practice.commands.user.settings.ToggleDuelRequestsCommand;
import gg.arcdev.practice.commands.user.settings.ToggleScoreboardCommand;
import gg.arcdev.practice.commands.user.settings.ToggleSpectatorsCommand;
import gg.arcdev.practice.game.queue.QueueListener;
import gg.arcdev.practice.game.queue.QueueThread;
import gg.arcdev.practice.core.scoreboard.ScoreboardAdapter;
import gg.arcdev.practice.util.InventoryUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import lombok.Getter;
import gg.arcdev.practice.util.command.Honcho;
import gg.arcdev.practice.util.config.BasicConfigurationFile;
import gg.arcdev.practice.util.menu.MenuListener;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main practice;

	@Getter private BasicConfigurationFile mainConfig;
	@Getter private BasicConfigurationFile arenasConfig;
	@Getter private BasicConfigurationFile kitsConfig;
	@Getter private BasicConfigurationFile eventsConfig;
	@Getter private MongoDatabase mongoDatabase;
	@Getter private Honcho honcho;
	@Getter private Essentials essentials;

	@Override
	public void onEnable() {
		practice = this;
		getServer().getPluginManager().registerEvents(new MenuListener(), this);
		honcho = new Honcho(this);
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

		getHoncho().registerTypeAdapter(Arena.class, new ArenaTypeAdapter());
		getHoncho().registerTypeAdapter(ArenaType.class, new ArenaTypeTypeAdapter());
		getHoncho().registerTypeAdapter(Kit.class, new KitTypeAdapter());
		getHoncho().registerTypeAdapter(EventGameMap.class, new EventGameMapTypeAdapter());
		getHoncho().registerTypeAdapter(Event.class, new EventTypeAdapter());

		Arrays.asList(
				new ArenaAddKitCommand(),
				new ArenaRemoveKitCommand(),
				new ArenaCreateCommand(),
				new ArenaDeleteCommand(),
				new ArenaGenerateCommand(),
				new ArenaGenHelperCommand(),
				new ArenaSaveCommand(),
				new ArenasCommand(),
				new ArenaSelectionCommand(),
				new ArenaSetSpawnCommand(),
				new ArenaStatusCommand(),
				new DuelCommand(),
				new DuelAcceptCommand(),
				new EventAdminCommand(),
				new EventHelpCommand(),
				new EventCancelCommand(),
				new EventClearCooldownCommand(),
				new EventForceStartCommand(),
				new EventHostCommand(),
				new EventInfoCommand(),
				new EventJoinCommand(),
				new EventLeaveCommand(),
				new EventSetLobbyCommand(),
				new EventMapCreateCommand(),
				new EventMapDeleteCommand(),
				new EventMapsCommand(),
				new EventMapSetSpawnCommand(),
				new EventMapStatusCommand(),
				new EventMapVoteCommand(),
				new EventAddMapCommand(),
				new EventRemoveMapCommand(),
				new EventsCommand(),
				new RematchCommand(),
				new SpectateCommand(),
				new StopSpectatingCommand(),
				new FlyCommand(),
				new SetSpawnCommand(),
				new PartyChatCommand(),
				new PartyCloseCommand(),
				new PartyCreateCommand(),
				new PartyDisbandCommand(),
				new PartyHelpCommand(),
				new PartyInfoCommand(),
				new PartyInviteCommand(),
				new PartyJoinCommand(),
				new PartyKickCommand(),
				new PartyLeaveCommand(),
				new PartyOpenCommand(),
				new KitCreateCommand(),
				new KitGetLoadoutCommand(),
				new KitSetLoadoutCommand(),
				new KitsCommand(),
				new ViewInventoryCommand(),
				new MatchTestCommand(),
				new ToggleScoreboardCommand(),
				new ToggleSpectatorsCommand(),
				new ToggleDuelRequestsCommand(),
				new SuicideCommand()
		).forEach(command -> getHoncho().registerCommand(command));

		Arrays.asList(
				new KitEditorListener(),
				new PartyListener(),
				new ProfileListener(),
				new PartyListener(),
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

		// Set the difficulty for each world to HARD
		// Clear the droppedItems for each world
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
					new ServerAddress(
							mainConfig.getString("MONGO.HOST"),
							mainConfig.getInteger("MONGO.PORT")
					),
					MongoCredential.createCredential(
							mainConfig.getString("MONGO.AUTHENTICATION.USERNAME"),
							mainConfig.getString("MONGO.AUTHENTICATION.ADMIN"), mainConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()
					),
					MongoClientOptions.builder().build()
			).getDatabase(mainConfig.getString("MONGO.DATABASE"));
		} else {
			mongoDatabase = new MongoClient(mainConfig.getString("MONGO.HOST"), mainConfig.getInteger("MONGO.PORT"))
					.getDatabase(mainConfig.getString("MONGO.DATABASE"));
		}
	}

	public static Main get() {
		return practice;
	}

}
