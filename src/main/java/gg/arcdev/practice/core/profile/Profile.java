package gg.arcdev.practice.core.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.kit.KitLoadout;
import gg.arcdev.practice.core.profile.meta.ProfileKitEditorData;
import gg.arcdev.practice.core.profile.meta.ProfileKitData;
import gg.arcdev.practice.core.profile.meta.ProfileRematchData;
import gg.arcdev.practice.core.profile.meta.option.ProfileOptions;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.Cooldown;
import gg.arcdev.practice.util.InventoryUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.duel.DuelProcedure;
import gg.arcdev.practice.game.duel.DuelRequest;
import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.game.queue.QueueProfile;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Profile {

	@Getter private static Map<UUID, Profile> profiles = new HashMap<>();
	@Getter private static Map<String, UUID> usernames = new HashMap<>();
	private static MongoCollection<Document> collection;

	@Getter private UUID uuid;
	@Getter @Setter private String username;
	@Getter @Setter private ProfileState state;
	@Getter private final ProfileOptions options;
	@Getter private final ProfileKitEditorData kitEditorData;
	@Getter private final Map<Kit, ProfileKitData> kitData;
	@Getter private final List<DuelRequest> duelRequests;
	@Getter @Setter private DuelProcedure duelProcedure;
	@Getter @Setter private ProfileRematchData rematchData;
	@Getter @Setter private Party party;
	@Getter @Setter private Match match;
	@Getter @Setter private QueueProfile queueProfile;
	@Getter @Setter private Cooldown enderpearlCooldown;
	@Getter @Setter private Cooldown voteCooldown;

	public Profile(UUID uuid) {
		this.uuid = uuid;
		this.state = ProfileState.LOBBY;
		this.options = new ProfileOptions();
		this.kitEditorData = new ProfileKitEditorData();
		this.kitData = new HashMap<>();
		this.duelRequests = new ArrayList<>();
		this.enderpearlCooldown = new Cooldown(0);
		this.voteCooldown = new Cooldown(0);

		for (Kit kit : Kit.getKits()) {
			this.kitData.put(kit, new ProfileKitData());
		}
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public void setUsername(String username) {
		this.username = username;
		cacheUsername(this);
	}

	public DuelRequest getDuelRequest(Player sender) {
		for (DuelRequest duelRequest : duelRequests) {
			if (duelRequest.getSender().equals(sender.getUniqueId())) {
				return duelRequest;
			}
		}

		return null;
	}

	public boolean isDuelRequestExpired(DuelRequest duelRequest) {
		if (duelRequest != null) {
			if (duelRequest.isExpired()) {
				duelRequests.remove(duelRequest);
				return true;
			}
		}

		return false;
	}

	public boolean isBusy() {
		return state != ProfileState.LOBBY;
	}

	void load() {
		Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

		if (document == null) {
			this.save();
			return;
		}

		String storedUsername = document.getString("username");
		if (storedUsername != null && !storedUsername.trim().isEmpty()) {
			setUsername(storedUsername);
		}

		Document options = (Document) document.get("options");

		this.options.showScoreboard(options.getBoolean("showScoreboard"));
		this.options.allowSpectators(options.getBoolean("allowSpectators"));
		this.options.receiveDuelRequests(options.getBoolean("receiveDuelRequests"));

		Document kitStatistics = (Document) document.get("kitStatistics");

		for (String key : kitStatistics.keySet()) {
			Document kitDocument = (Document) kitStatistics.get(key);
			Kit kit = Kit.getByName(key);

			if (kit != null) {
				ProfileKitData profileKitData = new ProfileKitData();
				profileKitData.setElo(kitDocument.getInteger("elo"));
				profileKitData.setWon(kitDocument.getInteger("won"));
				profileKitData.setLost(kitDocument.getInteger("lost"));

				kitData.put(kit, profileKitData);
			}
		}

		Document kitsDocument = (Document) document.get("loadouts");

		for (String key : kitsDocument.keySet()) {
			Kit kit = Kit.getByName(key);

			if (kit != null) {
				JsonArray kitsArray = new JsonParser().parse(kitsDocument.getString(key)).getAsJsonArray();
				KitLoadout[] loadouts = new KitLoadout[4];

				for (JsonElement kitElement : kitsArray) {
					JsonObject kitObject = kitElement.getAsJsonObject();

					KitLoadout loadout = new KitLoadout(kitObject.get("name").getAsString());
					loadout.setArmor(InventoryUtil.deserializeInventory(kitObject.get("armor").getAsString()));
					loadout.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

					loadouts[kitObject.get("index").getAsInt()] = loadout;
				}

				kitData.get(kit).setLoadouts(loadouts);
			}
		}
	}

	public void save() {
		Document document = new Document();
		document.put("uuid", uuid.toString());
		document.put("username", username);
		document.put("usernameLower", username == null ? null : normalizeUsername(username));

		Document optionsDocument = new Document();
		optionsDocument.put("showScoreboard", options.showScoreboard());
		optionsDocument.put("allowSpectators", options.allowSpectators());
		optionsDocument.put("receiveDuelRequests", options.receiveDuelRequests());
		document.put("options", optionsDocument);

		Document kitStatisticsDocument = new Document();

		for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
			Document kitDocument = new Document();
			kitDocument.put("elo", entry.getValue().getElo());
			kitDocument.put("won", entry.getValue().getWon());
			kitDocument.put("lost", entry.getValue().getLost());
			kitStatisticsDocument.put(entry.getKey().getName(), kitDocument);
		}

		document.put("kitStatistics", kitStatisticsDocument);

		Document kitsDocument = new Document();

		for (Map.Entry<Kit, ProfileKitData> entry : kitData.entrySet()) {
			JsonArray kitsArray = new JsonArray();

			for (int i = 0; i < 4; i++) {
				KitLoadout loadout = entry.getValue().getLoadout(i);

				if (loadout != null) {
					JsonObject kitObject = new JsonObject();
					kitObject.addProperty("index", i);
					kitObject.addProperty("name", loadout.getCustomName());
					kitObject.addProperty("armor", InventoryUtil.serializeInventory(loadout.getArmor()));
					kitObject.addProperty("contents", InventoryUtil.serializeInventory(loadout.getContents()));
					kitsArray.add(kitObject);
				}
			}

			kitsDocument.put(entry.getKey().getName(), kitsArray.toString());
		}

		document.put("loadouts", kitsDocument);

		collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
	}

	public static void init() {
		collection = Main.getInstance().getMongoDatabase().getCollection("profiles");

		// Players might have joined before the plugin finished loading
		for (Player player : Bukkit.getOnlinePlayers()) {
			Profile profile = new Profile(player.getUniqueId());

			try {
				profile.load();
			} catch (Exception e) {
				player.kickPlayer(CC.RED + "The server is loading...");
				continue;
			}

			profile.setUsername(player.getName());
			profiles.put(player.getUniqueId(), profile);
		}

		// Expire duel requests
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					Iterator<DuelRequest> iterator = profile.duelRequests.iterator();

					while (iterator.hasNext()) {
						DuelRequest duelRequest = iterator.next();

						if (duelRequest.isExpired()) {
							duelRequest.expire();
							iterator.remove();
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 60L, 60L);

		// Save every 5 minutes to prevent data loss
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Profile profile : Profile.getProfiles().values()) {
					profile.save();
				}
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 6000L, 6000L);
	}

	public static Profile getByUuid(UUID uuid) {
		Profile profile = profiles.get(uuid);

		if (profile == null) {
			profile = new Profile(uuid);
		}

		return profile;
	}

	public static Profile getByUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}

		Player onlinePlayer = Bukkit.getPlayerExact(username);
		if (onlinePlayer != null) {
			Profile onlineProfile = profiles.get(onlinePlayer.getUniqueId());
			if (onlineProfile != null) {
				onlineProfile.setUsername(onlinePlayer.getName());
				return onlineProfile;
			}
		}

		UUID cachedUuid = usernames.get(normalizeUsername(username));
		if (cachedUuid != null) {
			Profile cachedProfile = profiles.get(cachedUuid);
			if (cachedProfile != null) {
				return cachedProfile;
			}
		}

		Document document = collection.find(Filters.eq("usernameLower", normalizeUsername(username))).first();
		if (document == null) {
			document = collection.find(Filters.eq("username", username)).first();
		}

		if (document == null) {
			return null;
		}

		UUID profileUuid = UUID.fromString(document.getString("uuid"));
		Profile profile = profiles.get(profileUuid);
		if (profile == null) {
			profile = new Profile(profileUuid);
			profile.load();
		}

		if (profile.getUsername() == null) {
			profile.setUsername(document.getString("username"));
		}

		cacheUsername(profile);
		return profile;
	}

	public static Collection<String> getKnownUsernames() {
		List<String> knownUsernames = new ArrayList<>();

		for (Profile profile : profiles.values()) {
			if (profile.getUsername() != null && !profile.getUsername().trim().isEmpty()
					&& !knownUsernames.contains(profile.getUsername())) {
				knownUsernames.add(profile.getUsername());
			}
		}

		return knownUsernames;
	}

	private static void cacheUsername(Profile profile) {
		if (profile == null || profile.username == null || profile.username.trim().isEmpty()) {
			return;
		}

		usernames.put(normalizeUsername(profile.username), profile.uuid);
	}

	private static String normalizeUsername(String username) {
		return username.trim().toLowerCase();
	}

}
