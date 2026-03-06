package gg.arcdev.practice.game.leaderboard.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import gg.arcdev.practice.game.leaderboard.RankedLeaderboardMenu;
import org.bukkit.entity.Player;

@CommandAlias("leaderboard|lb")
public class LeaderboardCommand extends BaseCommand {

    @Default
    public void onLeaderboard(Player player) {
        new RankedLeaderboardMenu().openMenu(player);
    }
}