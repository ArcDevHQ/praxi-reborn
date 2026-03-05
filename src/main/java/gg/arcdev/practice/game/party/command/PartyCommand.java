package gg.arcdev.practice.game.party.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import gg.arcdev.practice.Locale;
import gg.arcdev.practice.core.hotbar.Hotbar;
import gg.arcdev.practice.core.profile.Profile;
import gg.arcdev.practice.core.profile.ProfileState;
import gg.arcdev.practice.game.party.Party;
import gg.arcdev.practice.game.party.PartyPrivacy;
import gg.arcdev.practice.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("party|p")
public class PartyCommand extends BaseCommand {

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
        sender.sendMessage(CC.translate("&b&lParty Commands"));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate("&7» &b/party create &7- &fCreate a party"));
        sender.sendMessage(CC.translate("&7» &b/party disband &7- &fDisband your party"));
        sender.sendMessage(CC.translate("&7» &b/party leave &7- &fLeave your party"));
        sender.sendMessage(CC.translate("&7» &b/party info &7- &fView party information"));
        sender.sendMessage(CC.translate("&7» &b/party invite <player> &7- &fInvite a player"));
        sender.sendMessage(CC.translate("&7» &b/party join <player> &7- &fJoin a player's party"));
        sender.sendMessage(CC.translate("&7» &b/party kick <player> &7- &fKick a member"));
        sender.sendMessage(CC.translate("&7» &b/party open &7- &fOpen your party"));
        sender.sendMessage(CC.translate("&7» &b/party close &7- &fClose your party"));
        sender.sendMessage(CC.translate("&7» &b/party chat <message> &7- &fSend a party message"));
        sender.sendMessage(CC.translate("&7&m------------------------------------------------"));
    }

    @Subcommand("create")
    public void onCreate(Player player) {

        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot create a party while frozen.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() != null) {
            player.sendMessage(CC.RED + "You already have a party.");
            return;
        }

        if (profile.getState() != ProfileState.LOBBY) {
            player.sendMessage(CC.RED + "You must be in the lobby to create a party.");
            return;
        }

        profile.setParty(new Party(player));
        Hotbar.giveHotbarItems(player);
        player.sendMessage(Locale.PARTY_CREATE.format());
    }

    @Subcommand("disband")
    public void onDisband(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().getLeader().equals(player)) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        profile.getParty().disband();
    }

    @Subcommand("leave")
    public void onLeave(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (profile.getParty().getLeader().equals(player)) {
            profile.getParty().disband();
        } else {
            profile.getParty().leave(player, false);
        }
    }

    @Subcommand("info")
    public void onInfo(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        profile.getParty().sendInformation(player);
    }

    @Subcommand("open|unlock")
    public void onOpen(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().getLeader().equals(player)) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        profile.getParty().setPrivacy(PartyPrivacy.OPEN);
        player.sendMessage(CC.GREEN + "Your party is now open.");
    }

    @Subcommand("close|lock")
    public void onClose(Player player) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().getLeader().equals(player)) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        profile.getParty().setPrivacy(PartyPrivacy.CLOSED);
        player.sendMessage(CC.GREEN + "Your party has been closed.");
    }

    @Subcommand("invite")
    @Syntax("<player>")
    public void onInvite(Player player, Player target) {

        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().getLeader().equals(player)) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        if (profile.getParty().getInvite(target.getUniqueId()) != null) {
            player.sendMessage(CC.RED + "That player has already been invited.");
            return;
        }

        if (profile.getParty().containsPlayer(target.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is already in your party.");
            return;
        }

        if (profile.getParty().getPrivacy() == PartyPrivacy.OPEN) {
            player.sendMessage(CC.RED + "Party is open. No invite needed.");
            return;
        }

        if (Profile.getByUuid(target.getUniqueId()).isBusy()) {
            player.sendMessage(target.getDisplayName() + CC.RED + " is currently busy.");
            return;
        }

        profile.getParty().invite(target);
    }

    @Subcommand("join")
    @Syntax("<player>")
    public void onJoin(Player player, Player target) {

        if (target == null) {
            player.sendMessage(CC.RED + "A player with that name could not be found.");
            return;
        }

        if (player.hasMetadata("frozen")) {
            player.sendMessage(CC.RED + "You cannot join a party while frozen.");
            return;
        }

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() != null) {
            player.sendMessage(CC.RED + "You already have a party.");
            return;
        }

        Party party = Profile.getByUuid(target.getUniqueId()).getParty();

        if (party == null) {
            player.sendMessage(CC.RED + "That player does not have a party.");
            return;
        }

        if (party.getPrivacy() == PartyPrivacy.CLOSED &&
                party.getInvite(player.getUniqueId()) == null) {
            player.sendMessage(CC.RED + "You have not been invited.");
            return;
        }

        if (party.getPlayers().size() >= 32) {
            player.sendMessage(CC.RED + "That party is full.");
            return;
        }

        party.join(player);
    }

    @Subcommand("kick")
    @Syntax("<player>")
    public void onKick(Player player, Player target) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().getLeader().equals(player)) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        if (!profile.getParty().containsPlayer(target.getUniqueId())) {
            player.sendMessage(CC.RED + "That player is not in your party.");
            return;
        }

        if (player.equals(target)) {
            player.sendMessage(CC.RED + "You cannot kick yourself.");
            return;
        }

        profile.getParty().leave(target, true);
    }

    @Subcommand("chat")
    @Syntax("<message>")
    public void onChat(Player player, String message) {

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You are not in a party.");
            return;
        }

        profile.getParty().sendChat(player, message);
    }
}