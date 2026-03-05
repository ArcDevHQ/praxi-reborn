package gg.arcdev.practice.core.scoreboard;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.game.queue.QueueProfile;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.TimeUtil;
import gg.arcdev.practice.util.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

	private int inQueues;
	private int inFights;

	public ScoreboardAdapter() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), new Runnable() {
			@Override
			public void run() {
				int queues = 0;
				int fights = 0;

				for (Player player : Bukkit.getOnlinePlayers()) {
					Profile profile = Profile.getByUuid(player.getUniqueId());
					if (profile == null) continue;

					switch (profile.getState()) {
						case QUEUEING:
							queues++;
							break;
						case FIGHTING:
						case EVENT:
							fights++;
							break;
					}
				}

				inQueues = queues;
				inFights = fights;
			}
		}, 2L, 2L);
	}
	@Override
	public String getTitle(Player player) {
		return Main.getInstance().getMainConfig().getString("SCOREBOARD.TITLE");
	}

	@Override
	public List<String> getLines(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile == null || !profile.getOptions().showScoreboard()) return null;

		List<String> lines = new ArrayList<>();

		if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
			lines.add("&cOnline: &7" + Bukkit.getOnlinePlayers().size());
			lines.add("&cIn Fights: &7" + inFights);
			lines.add("&cIn Queues: &7" + inQueues);

			if (EventGame.getActiveGame() == null && !EventGame.getCooldown().hasExpired()) {
				lines.add("&cEvent Cooldown: &7" +
						TimeUtil.millisToTimer(EventGame.getCooldown().getRemaining()));
			}
		}

		if (profile.getState() == ProfileState.LOBBY && profile.getParty() != null) {
			Party party = profile.getParty();
			lines.add("&cYour Party");

			int added = 0;
			for (Player other : party.getListOfPlayers()) {
				lines.add(" &7" + (party.getLeader().equals(other) ? "*" : "-") + " &r" + other.getName());
				if (++added >= 4) break;
			}
		}

		if (profile.getState() == ProfileState.QUEUEING) {
			QueueProfile queueProfile = profile.getQueueProfile();
			lines.add(CC.SB_BAR);
			lines.add("&a&oSearching for a match...");
			lines.add(" ");
			lines.add("&c" + queueProfile.getQueue().getQueueName());
			lines.add("&cElapsed: &7" + TimeUtil.millisToTimer(queueProfile.getPassed()));

			if (queueProfile.getQueue().isRanked()) {
				lines.add("&cELO Range: &7" + queueProfile.getMinRange() + " -> " + queueProfile.getMaxRange());
			}
		}

		if (profile.getState() == ProfileState.FIGHTING || profile.getState() == ProfileState.SPECTATING) {
			lines.addAll(profile.getMatch().getScoreboardLines(player));
		}

		if (profile.getState() == ProfileState.EVENT && EventGame.getActiveGame() != null) {
			lines.addAll(EventGame.getActiveGame().getGameLogic().getScoreboardEntries());
		}

		lines.add(0, CC.SB_BAR);
		lines.add(CC.SB_BAR);

		return lines;
	}
}