package gg.arcdev.practice.core.profile.visibility;

import gg.arcdev.practice.core.adapter.CoreManager;
import gg.arcdev.practice.game.match.participant.MatchGamePlayer;
import gg.arcdev.practice.core.profile.nametag.NameTags;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VisibilityLogic {

	public static void handle(Player viewer) {
		for (Player target : Bukkit.getOnlinePlayers()) {
			handle(viewer, target);
		}
	}

	public static void handle(Player viewer, Player target) {
		if (viewer == null || target == null) {
			return;
		}

		Profile viewerProfile = Profile.getByUuid(viewer.getUniqueId());
		Profile targetProfile = Profile.getByUuid(target.getUniqueId());

		if (viewerProfile.getState() == ProfileState.LOBBY || viewerProfile.getState() == ProfileState.QUEUEING) {
			if (viewer.equals(target)) {
				NameTags.color(viewer, target, getColor(viewer), false);
				return;
			}

			if (viewerProfile.getParty() != null && viewerProfile.getParty().containsPlayer(target.getUniqueId())) {
				viewer.showPlayer(target);
				NameTags.color(viewer, target, ChatColor.BLUE, false);
			} else {
				viewer.hidePlayer(target);
				NameTags.reset(viewer, target);
			}
		} else if (viewerProfile.getState() == ProfileState.FIGHTING) {
			if (viewer.equals(target)) {
				NameTags.color(viewer, target, ChatColor.GREEN, false);
				return;
			}

			MatchGamePlayer targetGamePlayer = viewerProfile.getMatch().getGamePlayer(target);

			if (targetGamePlayer != null) {
				if (!targetGamePlayer.isDead()) {
					viewer.showPlayer(target);
					NameTags.color(viewer, target, viewerProfile.getMatch().getRelationColor(viewer, target), false);
				} else {
					viewer.hidePlayer(target);
					NameTags.reset(viewer, target);
				}
			} else {
				viewer.hidePlayer(target);
				NameTags.reset(viewer, target);
			}
		} else if (viewerProfile.getState() == ProfileState.EVENT) {
			if (targetProfile.getState() == ProfileState.EVENT) {
				viewer.showPlayer(target);
				NameTags.color(viewer, target, ChatColor.AQUA, false);
			} else {
				viewer.hidePlayer(target);
				NameTags.reset(viewer, target);
			}
		} else if (viewerProfile.getState() == ProfileState.SPECTATING) {
			MatchGamePlayer targetGamePlayer = viewerProfile.getMatch().getGamePlayer(target);
			
			if (targetGamePlayer != null) {
				if (!targetGamePlayer.isDead() && !targetGamePlayer.isDisconnected()) {
					viewer.showPlayer(target);
					NameTags.color(viewer, target, viewerProfile.getMatch().getRelationColor(viewer, target), false);
				} else {
					viewer.hidePlayer(target);
					NameTags.reset(viewer, target);
				}
			} else {
				viewer.hidePlayer(target);
				NameTags.reset(viewer, target);
			}
		}
	}
	private static ChatColor getColor(Player player) {
		return CoreManager.getInstance().getCore().getColor(player.getUniqueId());
	}
}
