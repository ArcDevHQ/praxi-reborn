package gg.arcdev.practice.core;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import gg.arcdev.practice.commands.SetSpawnCommand;
import gg.arcdev.practice.game.event.commands.admin.*;
import gg.arcdev.practice.game.event.commands.map.*;
import gg.arcdev.practice.game.event.commands.user.*;
import gg.arcdev.practice.game.event.commands.vote.EventMapVoteCommand;
import gg.arcdev.practice.commands.user.duels.*;
import gg.arcdev.practice.commands.user.match.*;
import gg.arcdev.practice.commands.user.settings.*;
import gg.arcdev.practice.Main;
import gg.arcdev.practice.game.arena.Arena;
import gg.arcdev.practice.game.arena.command.*;
import gg.arcdev.practice.game.event.Event;
import gg.arcdev.practice.game.event.game.map.EventGameMap;
import gg.arcdev.practice.game.kit.Kit;
import gg.arcdev.practice.game.kit.commands.KitCommand;
import gg.arcdev.practice.game.kit.commands.KitsCommand;
import gg.arcdev.practice.game.party.command.*;
import lombok.Getter;
import java.util.Arrays;
import java.util.Collections;

public class CommandRegistry {

    @Getter
    private final PaperCommandManager manager;

    public CommandRegistry(Main plugin) {
        this.manager = new PaperCommandManager(plugin);
        this.manager.enableUnstableAPI("help");
    }

    public void registerCommands() {

        manager.getCommandContexts().registerContext(Arena.class, c -> {
            String name = c.popFirstArg();
            Arena arena = Arena.getByName(name);
            if (arena == null) {
                throw new InvalidCommandArgument("Arena with that name does not exist!");
            }
            return arena;
        });

        manager.getCommandContexts().registerContext(Kit.class, c -> {
            String name = c.popFirstArg();
            Kit kit = Kit.getByName(name);
            if (kit == null) {
                throw new InvalidCommandArgument("Kit with that name does not exist!");
            }
            return kit;
        });

        manager.getCommandContexts().registerContext(Event.class, c -> {
            String name = c.popFirstArg();
            Event event = Event.getByName(name);
            if (event == null) {
                throw new InvalidCommandArgument("Event with that name does not exist!");
            }
            return event;
        });

        manager.getCommandContexts().registerContext(EventGameMap.class, c -> {
            String name = c.popFirstArg();
            EventGameMap map = EventGameMap.getByName(name);
            if (map == null) {
                throw new InvalidCommandArgument("Event map with that name does not exist!");
            }
            return map;
        });

        Arrays.asList(
                new ArenaCommand(),
                new ArenasCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new DuelCommand(),
                new DuelAcceptCommand(),
                new RematchCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new EventAdminCommand(),
                new EventHelpCommand(),
                new EventSetLobbyCommand(),
                new EventsCommand(),
                new EventAddMapCommand(),
                new EventRemoveMapCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new EventCancelCommand(),
                new EventClearCooldownCommand(),
                new EventForceStartCommand(),
                new EventHostCommand(),
                new EventInfoCommand(),
                new EventJoinCommand(),
                new EventLeaveCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new EventMapCreateCommand(),
                new EventMapDeleteCommand(),
                new EventMapsCommand(),
                new EventMapSetSpawnCommand(),
                new EventMapStatusCommand(),
                new EventMapVoteCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new KitCommand(),
                new KitsCommand()
        ).forEach(manager::registerCommand);

        Collections.singletonList(
                new PartyCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new SpectateCommand(),
                new StopSpectatingCommand(),
                new ViewInventoryCommand()
        ).forEach(manager::registerCommand);

        Arrays.asList(
                new SetSpawnCommand(),
                new ToggleScoreboardCommand(),
                new ToggleSpectatorsCommand(),
                new ToggleDuelRequestsCommand()
        ).forEach(manager::registerCommand);
    }
}