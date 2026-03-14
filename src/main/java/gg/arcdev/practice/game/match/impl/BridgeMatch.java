package gg.arcdev.practice.game.match.impl;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.game.match.MatchSnapshot;
import gg.arcdev.practice.game.match.MatchState;
import gg.arcdev.practice.game.match.participant.MatchGamePlayer;
import gg.arcdev.practice.game.participant.GameParticipant;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.meta.ProfileRematchData;
import gg.arcdev.practice.game.queue.Queue;
import gg.arcdev.practice.util.CC;
import gg.arcdev.practice.util.ChatComponentBuilder;
import gg.arcdev.practice.util.PlayerUtil;
import gg.arcdev.practice.util.TimeUtil;
import gg.arcdev.practice.core.profile.visibility.VisibilityLogic;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BridgeMatch extends Match {

    public static final int GOALS_TO_WIN = 5;

    @Getter
    private final GameParticipant<MatchGamePlayer> participantA;
    @Getter
    private final GameParticipant<MatchGamePlayer> participantB;

    @Getter
    private int goalsA = 0;
    @Getter
    private int goalsB = 0;

    @Getter
    private GameParticipant<MatchGamePlayer> winningParticipant;
    @Getter
    private GameParticipant<MatchGamePlayer> losingParticipant;

    public BridgeMatch(Queue queue, Kit kit, Arena arena, boolean ranked,
            GameParticipant<MatchGamePlayer> participantA,
            GameParticipant<MatchGamePlayer> participantB) {
        super(queue, kit, arena, ranked);
        this.participantA = participantA;
        this.participantB = participantB;
    }

    @Override
    public void setupPlayer(Player player) {
        super.setupPlayer(player);

        Location spawn = participantA.containsPlayer(player.getUniqueId())
                ? getArena().getSpawnA()
                : getArena().getSpawnB();

        if (spawn == null)
            return;

        if (spawn.getBlock().getType() == Material.AIR) {
            player.teleport(spawn);
        } else {
            player.teleport(spawn.clone().add(0, 1, 0));
        }
    }

    public void onGoal(Player scorer) {
        if (!(getState() == MatchState.STARTING_ROUND || getState() == MatchState.PLAYING_ROUND)) {
            return;
        }

        MatchGamePlayer scorerGamePlayer = getGamePlayer(scorer);
        if (scorerGamePlayer == null || scorerGamePlayer.isDead())
            return;

        boolean scorerIsA = participantA.containsPlayer(scorer.getUniqueId());

        if (scorerIsA) {
            goalsA++;
        } else {
            goalsB++;
        }

        String goalMsg = CC.YELLOW + CC.BOLD + scorer.getName() + CC.GOLD + " scored a goal! "
                + CC.WHITE + "[" + CC.AQUA + goalsA + CC.GRAY + "/" + CC.RED + goalsB + CC.WHITE + "]";
        sendMessage(goalMsg);
        sendSound(Sound.LEVEL_UP, 1.0F, 1.5F);

        if (canEndMatch()) {
            winningParticipant = scorerIsA ? participantA : participantB;
            losingParticipant = scorerIsA ? participantB : participantA;

            setState(MatchState.ENDING_ROUND);
            timeData = System.currentTimeMillis() - timeData;
            onRoundEnd();

            setState(MatchState.ENDING_MATCH);
            getLogicTask().setNextAction(4);
            return;
        }

        scorerGamePlayer.setDead(true);
        PlayerUtil.reset(scorer);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (scorer.isOnline() && !scorerGamePlayer.isDisconnected()) {
                    scorerGamePlayer.setDead(false);
                    setupPlayer(scorer);
                    scorer.setVelocity(new Vector());

                    for (GameParticipant<MatchGamePlayer> gp : getParticipants()) {
                        for (MatchGamePlayer gpl : gp.getPlayers()) {
                            if (!gpl.isDisconnected()) {
                                Player p = gpl.getPlayer();
                                if (p != null) {
                                    VisibilityLogic.handle(p);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 20L);
    }

    @Override
    public void onDeath(Player dead) {
        if (!(getState() == MatchState.STARTING_ROUND || getState() == MatchState.PLAYING_ROUND)) {
            return;
        }

        MatchGamePlayer deadGamePlayer = getGamePlayer(dead);
        if (deadGamePlayer == null || deadGamePlayer.isDead())
            return;

        deadGamePlayer.setDead(true);

        Player killer = PlayerUtil.getLastAttacker(dead);

        if (dead.isDead()) {
            dead.spigot().respawn();
        }

        dead.setVelocity(new Vector());

        MatchSnapshot snapshot = new MatchSnapshot(dead, true);
        snapshot.setPotionsMissed(deadGamePlayer.getPotionsMissed());
        snapshot.setPotionsThrown(deadGamePlayer.getPotionsThrown());
        snapshot.setLongestCombo(deadGamePlayer.getLongestCombo());
        snapshot.setTotalHits(deadGamePlayer.getHits());
        snapshots.add(snapshot);

        PlayerUtil.reset(dead);

        for (GameParticipant<MatchGamePlayer> gameParticipant : getParticipants()) {
            for (MatchGamePlayer gamePlayer : gameParticipant.getPlayers()) {
                if (!gamePlayer.isDisconnected()) {
                    Player player = gamePlayer.getPlayer();
                    if (player != null) {
                        VisibilityLogic.handle(player, dead);
                        sendDeathMessage(player, dead, killer);
                    }
                }
            }
        }

        for (Player player : getSpectatorsAsPlayers()) {
            VisibilityLogic.handle(player, dead);
            sendDeathMessage(player, dead, killer);
            sendDeathPackets(player, dead.getLocation());
        }

        final MatchGamePlayer finalDeadGamePlayer = deadGamePlayer;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (dead.isOnline() && !finalDeadGamePlayer.isDisconnected()) {
                    finalDeadGamePlayer.setDead(false);
                    setupPlayer(dead);
                    dead.setVelocity(new Vector());

                    for (GameParticipant<MatchGamePlayer> gp : getParticipants()) {
                        for (MatchGamePlayer gpl : gp.getPlayers()) {
                            if (!gpl.isDisconnected()) {
                                Player p = gpl.getPlayer();
                                if (p != null) {
                                    VisibilityLogic.handle(p);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 20L);
    }

    @Override
    public boolean canEndMatch() {
        return goalsA >= GOALS_TO_WIN || goalsB >= GOALS_TO_WIN;
    }

    @Override
    public boolean canStartRound() {
        return false;
    }

    @Override
    public boolean canEndRound() {
        return canEndMatch();
    }

    @Override
    public void onRoundEnd() {
        if (winningParticipant == null) {
            winningParticipant = goalsA >= GOALS_TO_WIN ? participantA : participantB;
            losingParticipant = goalsA >= GOALS_TO_WIN ? participantB : participantA;
        }

        losingParticipant.setEliminated(true);

        if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
            for (MatchSnapshot snapshot : snapshots) {
                if (snapshot.getUuid().equals(participantA.getLeader().getUuid())) {
                    snapshot.setOpponent(participantB.getLeader().getUuid());
                } else if (snapshot.getUuid().equals(participantB.getLeader().getUuid())) {
                    snapshot.setOpponent(participantA.getLeader().getUuid());
                }
            }
        }

        super.onRoundEnd();
    }

    @Override
    public void end() {
        super.end();

        if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
            UUID rematchKey = UUID.randomUUID();

            for (GameParticipant<MatchGamePlayer> gameParticipant : getParticipants()) {
                for (MatchGamePlayer gamePlayer : gameParticipant.getPlayers()) {
                    if (!gamePlayer.isDisconnected()) {
                        Profile profile = Profile.getByUuid(gamePlayer.getUuid());

                        if (profile.getParty() == null) {
                            UUID opponent;

                            if (gameParticipant.equals(participantA)) {
                                opponent = participantB.getLeader().getUuid();
                            } else {
                                opponent = participantA.getLeader().getUuid();
                            }

                            if (opponent != null) {
                                ProfileRematchData rematchData = new ProfileRematchData(rematchKey,
                                        gamePlayer.getUuid(), opponent, getKit());
                                profile.setRematchData(rematchData);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isOnSameTeam(Player first, Player second) {
        boolean[] b = {
                participantA.containsPlayer(first.getUniqueId()),
                participantB.containsPlayer(first.getUniqueId()),
                participantA.containsPlayer(second.getUniqueId()),
                participantB.containsPlayer(second.getUniqueId())
        };
        return (b[0] && b[2]) || (b[1] && b[3]);
    }

    @Override
    public List<GameParticipant<MatchGamePlayer>> getParticipants() {
        return Arrays.asList(participantA, participantB);
    }

    @Override
    public org.bukkit.ChatColor getRelationColor(Player viewer, Player target) {
        if (viewer.equals(target))
            return org.bukkit.ChatColor.GREEN;

        boolean[] b = {
                participantA.containsPlayer(viewer.getUniqueId()),
                participantB.containsPlayer(viewer.getUniqueId()),
                participantA.containsPlayer(target.getUniqueId()),
                participantB.containsPlayer(target.getUniqueId())
        };

        if ((b[0] && b[3]) || (b[2] && b[1]))
            return org.bukkit.ChatColor.RED;
        if ((b[0] && b[2]) || (b[1] && b[3]))
            return org.bukkit.ChatColor.GREEN;
        if (getSpectators().contains(viewer.getUniqueId())) {
            return participantA.containsPlayer(target.getUniqueId())
                    ? org.bukkit.ChatColor.GREEN
                    : org.bukkit.ChatColor.RED;
        }
        return org.bukkit.ChatColor.YELLOW;
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();

        if (getParticipant(player) != null) {
            if (getState() == MatchState.STARTING_ROUND || getState() == MatchState.PLAYING_ROUND) {
                boolean isA = participantA.containsPlayer(player.getUniqueId());

                lines.add("&aDuration: &r" + getDuration());
                lines.add("");
                lines.add("&aYour Goals: &f" + (isA ? goalsA : goalsB));
                lines.add("&cOpponent Goals: &f" + (isA ? goalsB : goalsA));
                lines.add("");
                lines.add("&7First to &b" + GOALS_TO_WIN + " &7wins!");
            } else {
                lines.add("&cDuration: &7" + TimeUtil.millisToTimer(timeData));
            }
        } else {
            lines.add("&cKit: &7" + getKit().getName());
            lines.add("&cDuration: &7" + getDuration());
            lines.add("");
            lines.add(participantA.getConjoinedNames() + " &b" + goalsA);
            lines.add("&7vs");
            lines.add(participantB.getConjoinedNames() + " &c" + goalsB);
        }

        return lines;
    }

    @Override
    public void addSpectator(Player spectator, Player target) {
        super.addSpectator(spectator, target);

        ChatColor firstColor;
        ChatColor secondColor;

        if (participantA.containsPlayer(target.getUniqueId())) {
            firstColor = ChatColor.GREEN;
            secondColor = ChatColor.RED;
        } else {
            firstColor = ChatColor.RED;
            secondColor = ChatColor.GREEN;
        }

        spectator.sendMessage(Locale.MATCH_START_SPECTATING.format(
                firstColor.toString(),
                participantA.getConjoinedNames(),
                secondColor.toString(),
                participantB.getConjoinedNames()));
    }

    @Override
    public List<BaseComponent[]> generateEndComponents() {
        List<BaseComponent[]> componentsList = new ArrayList<>();

        for (String line : Locale.MATCH_END_DETAILS.formatLines()) {
            if (line.equalsIgnoreCase("%INVENTORIES%")) {
                BaseComponent[] winners = generateInventoriesComponents(
                        Locale.MATCH_END_WINNER_INVENTORY.format(participantA.getPlayers().size() == 1 ? "" : "s"),
                        winningParticipant);

                BaseComponent[] losers = generateInventoriesComponents(
                        Locale.MATCH_END_LOSER_INVENTORY.format(participantB.getPlayers().size() == 1 ? "" : "s"),
                        losingParticipant);

                if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
                    ChatComponentBuilder builder = new ChatComponentBuilder("");

                    for (BaseComponent component : winners) {
                        builder.append((TextComponent) component);
                    }

                    builder.append(new ChatComponentBuilder("&7 - ").create());

                    for (BaseComponent component : losers) {
                        builder.append((TextComponent) component);
                    }

                    componentsList.add(builder.create());
                } else {
                    componentsList.add(winners);
                    componentsList.add(losers);
                }

                continue;
            }

            if (line.equalsIgnoreCase("%ELO_CHANGES%")) {
                continue;
            }

            componentsList.add(new ChatComponentBuilder("").parse(line).create());
        }

        return componentsList;
    }

}
