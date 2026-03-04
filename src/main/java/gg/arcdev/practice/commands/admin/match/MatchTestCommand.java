package gg.arcdev.practice.commands.admin.match;

import gg.arcdev.practice.Locale;
import gg.arcdev.practice.game.match.Match;
import gg.arcdev.practice.game.match.participant.MatchGamePlayer;
import gg.arcdev.practice.game.participant.GameParticipant;
import gg.arcdev.practice.util.command.command.CommandMeta;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

@CommandMeta(label = "match test", permission = "praxi.admin")
public class MatchTestCommand {

	public void execute(Player player) {
		List<GameParticipant<MatchGamePlayer>> list = Arrays.asList(
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test1")),
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test2")),
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test3")),
				new GameParticipant<>(new MatchGamePlayer(UUID.randomUUID(), "Test4")));

		BaseComponent[] components = Match.generateInventoriesComponents(
				Locale.MATCH_END_WINNER_INVENTORY.format("s"), list);

		player.spigot().sendMessage(components);
	}

}
