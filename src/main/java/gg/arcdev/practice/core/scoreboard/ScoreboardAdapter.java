package gg.arcdev.practice.core.scoreboard;

import gg.arcdev.practice.Main;
import gg.arcdev.practice.core.config.ScoreboardConfig;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.game.event.game.EventGame;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.game.queue.QueueProfile;
import gg.arcdev.practice.util.TimeUtil;
import gg.arcdev.practice.util.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

	private int inQueues;
	private int inFights;
	private final ScoreboardConfig config;

	public ScoreboardAdapter() {
		this.config = new ScoreboardConfig(Main.getInstance());

		Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
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
					default:
						break;
				}
			}

			inQueues = queues;
			inFights = fights;
		}, 2L, 2L);
	}

	@Override
	public String getTitle(Player player) {
		return config.getTitle();
	}

	@Override
	public List<String> getLines(Player player) {
		Profile profile = Profile.getByUuid(player.getUniqueId());
		if (profile == null || !profile.getOptions().showScoreboard() || !config.isEnabled()) return null;

		String stateKey = getYamlState(profile.getState());
		List<String> template = config.getLines(stateKey);
		if (template == null) return new ArrayList<>();

		List<String> lines = new ArrayList<>();
		for (String line : template) {
			line = line.replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
					.replace("<fighting>", String.valueOf(inFights))
					.replace("<in-queue>", String.valueOf(inQueues))
					.replace("<footer>", config.getFooter())
					.replace("<date>", new SimpleDateFormat("dd/MM/yyyy z").format(new Date()));

			if (profile.getState() == ProfileState.QUEUEING) {
				QueueProfile queueProfile = profile.getQueueProfile();
				if (queueProfile != null) {
					if (line.contains("<kit>")) {
						line = line.replace("<kit>", queueProfile.getQueue().getKit().getName());
					}

					if (line.contains("<mode>")) {
						line = line.replace("<mode>", queueProfile.getQueue().isRanked() ? "Ranked" : "Unranked");
					}

					if (line.contains("<elapsed>")) {
						line = line.replace("<elapsed>", TimeUtil.millisToTimer(queueProfile.getPassed()));
					}
				}
			}

			if (line.contains("<party>") && profile.getState() == ProfileState.LOBBY && profile.getParty() != null) {
				Party party = profile.getParty();
				StringBuilder sb = new StringBuilder();
				int added = 0;
				for (Player p : party.getListOfPlayers()) {
					sb.append(added > 0 ? "\n" : "")
							.append(party.getLeader().equals(p) ? "*" : "-")
							.append(" ").append(p.getName());
					if (++added >= 4) break;
				}
				line = line.replace("<party>", sb.toString());
			}

			lines.add(line);
		}

		if ((profile.getState() == ProfileState.FIGHTING || profile.getState() == ProfileState.SPECTATING) && profile.getMatch() != null) {
			lines.addAll(profile.getMatch().getScoreboardLines(player));
		}

		if (profile.getState() == ProfileState.EVENT && EventGame.getActiveGame() != null) {
			lines.addAll(EventGame.getActiveGame().getGameLogic().getScoreboardEntries());
		}

		return lines;
	}

	private String getYamlState(ProfileState state) {
		switch (state) {
			case LOBBY: return "LOBBY";
			case QUEUEING: return "QUEUE";
			case FIGHTING: return "FIGHTING";
			case EVENT: return "EVENT";
			default: return "LOBBY";
		}
	}
}