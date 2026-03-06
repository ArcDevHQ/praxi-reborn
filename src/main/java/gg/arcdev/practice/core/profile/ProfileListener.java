package gg.arcdev.practice.core.profile;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.core.essentials.event.SpawnTeleportEvent;
import gg.arcdev.practice.core.hotbar.Hotbar;
import gg.arcdev.practice.core.hotbar.HotbarItem;
import gg.arcdev.practice.core.profile.meta.option.button.AllowSpectatorsOptionButton;
import gg.arcdev.practice.core.profile.meta.option.button.DuelRequestsOptionButton;
import gg.arcdev.practice.core.profile.meta.option.button.ShowScoreboardOptionButton;
import gg.arcdev.practice.core.profile.option.OptionsOpenedEvent;
import gg.arcdev.practice.core.profile.visibility.VisibilityLogic;
import gg.arcdev.practice.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ProfileListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onSpawnTeleportEvent(SpawnTeleportEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (!profile.isBusy() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			Hotbar.giveHotbarItems(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.getState() != ProfileState.FIGHTING) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.getState() != ProfileState.FIGHTING) {
			if (event.getPlayer().getGameMode() != GameMode.CREATIVE || !event.getPlayer().isOp()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
		Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

		if (profile.getState() == ProfileState.LOBBY) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Profile profile = Profile.getByUuid(event.getEntity().getUniqueId());

			if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
				event.setCancelled(true);

				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					Main.getInstance().getEssentials().teleportToSpawn((Player) event.getEntity());
				}
			}
		}
	}

	@EventHandler
	public void onOptionsOpenedEvent(OptionsOpenedEvent event) {
		event.getButtons().add(new ShowScoreboardOptionButton());
		event.getButtons().add(new AllowSpectatorsOptionButton());
		event.getButtons().add(new DuelRequestsOptionButton());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();

			HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

			if (hotbarItem != null) {
				if (hotbarItem.getCommand() != null) {
					event.setCancelled(true);
					player.chat("/" + hotbarItem.getCommand());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		Profile profile = new Profile(event.getUniqueId());
		profile.setUsername(event.getName());

		try {
			profile.load();
		} catch (Exception e) {
			e.printStackTrace();
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage(ChatColor.RED + "Failed to load your profile. Try again later.");
			return;
		}

		Profile.getProfiles().put(event.getUniqueId(), profile);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Profile profile = Profile.getProfiles().get(event.getPlayer().getUniqueId());
		if (profile != null) {
			profile.setUsername(event.getPlayer().getName());
		}

		for (String line : Main.getInstance().getMainConfig().getStringList("JOIN_MESSAGES")) {
			event.getPlayer().sendMessage(CC.translate(line));
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Hotbar.giveHotbarItems(event.getPlayer());
				Main.getInstance().getEssentials().teleportToSpawn(event.getPlayer());

				for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
					VisibilityLogic.handle(event.getPlayer(), otherPlayer);
					VisibilityLogic.handle(otherPlayer, event.getPlayer());
				}
			}
		}.runTaskLater(Main.getInstance(), 4L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Profile profile = Profile.getProfiles().remove(event.getPlayer().getUniqueId());

		new BukkitRunnable() {
			@Override
			public void run() {
				profile.save();
			}
		}.runTaskAsynchronously(Main.getInstance());

		if (profile.getRematchData() != null) {
			profile.getRematchData().validate();
		}
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		if (event.getReason() != null) {
			if (event.getReason().contains("Flying is not enabled")) {
				event.setCancelled(true);
			}
		}
	}

}
