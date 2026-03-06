package gg.arcdev.practice.integration.lunar;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.game.party.Party;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class LobbyTeamService {

	private final Main plugin;
	private final Set<java.util.UUID> managedPlayers = new HashSet<>();
	private TeamModule teamModule;
	private BukkitTask task;
	private boolean initWarningLogged;

	public LobbyTeamService(Main plugin) {
		this.plugin = plugin;
	}

	public void start() {
		task = new BukkitRunnable() {
			@Override
			public void run() {
				if (!ensureInitialized()) {
					return;
				}

				refreshAllPlayers();
			}
		}.runTaskTimer(plugin, 20L, 5L);
	}

	public void shutdown() {
		if (task != null) {
			task.cancel();
			task = null;
		}

		if (teamModule == null) {
			return;
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			Apollo.getPlayerManager().getPlayer(player.getUniqueId()).ifPresent(this::reset);
		}
	}

	private void refreshAllPlayers() {
		if (teamModule == null) {
			return;
		}

		for (Player viewer : Bukkit.getOnlinePlayers()) {
			ApolloPlayer apolloPlayer = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId()).orElse(null);
			if (apolloPlayer == null) {
				continue;
			}

			Profile profile = Profile.getProfiles().get(viewer.getUniqueId());

			List<TeamMember> teammates = resolveTeamMembers(profile);
			if (teammates.isEmpty()) {
				resetManaged(viewer.getUniqueId(), apolloPlayer);
				continue;
			}

			teamModule.updateTeamMembers(apolloPlayer, teammates);
			managedPlayers.add(viewer.getUniqueId());
		}
	}

	private List<TeamMember> resolveTeamMembers(Profile profile) {
		return profile != null
				&& profile.getState() == ProfileState.LOBBY
				&& profile.getParty() != null
				? buildPartyTeamMembers(profile.getParty())
				: new ArrayList<>();
	}

	private List<TeamMember> buildPartyTeamMembers(Party party) {
		List<TeamMember> teammates = new ArrayList<>();

		for (Player teammate : party.getListOfPlayers()) {
			Profile teammateProfile = Profile.getProfiles().get(teammate.getUniqueId());
			if (teammateProfile == null || teammateProfile.getState() != ProfileState.LOBBY) {
				continue;
			}

			teammates.add(TeamMember.builder()
					.playerUuid(teammate.getUniqueId())
					.displayName(Component.text(teammate.getName()))
					.markerColor(party.getLeader().getUniqueId().equals(teammate.getUniqueId()) ? Color.YELLOW : Color.CYAN)
					.location(BukkitApollo.toApolloLocation(teammate.getLocation()))
					.build());
		}

		return teammates;
	}

	private void reset(ApolloPlayer apolloPlayer) {
		teamModule.resetTeamMembers(apolloPlayer);
	}

	private void resetManaged(java.util.UUID playerUuid, ApolloPlayer apolloPlayer) {
		if (managedPlayers.remove(playerUuid)) {
			reset(apolloPlayer);
		}
	}

	private boolean ensureInitialized() {
		if (teamModule != null) {
			return true;
		}

		try {
			if (!Apollo.getModuleManager().isEnabled(TeamModule.class)) {
				if (!initWarningLogged) {
					plugin.getLogger().warning("Apollo TeamModule is not enabled; lobby teammate markers will be skipped.");
					initWarningLogged = true;
				}
				return false;
			}

			teamModule = Apollo.getModuleManager().getModule(TeamModule.class);
			initWarningLogged = false;
			return true;
		} catch (Throwable throwable) {
			if (!initWarningLogged) {
				plugin.getLogger().warning("Apollo is not ready yet; lobby teammate markers will retry automatically.");
				initWarningLogged = true;
			}
			return false;
		}
	}
}
