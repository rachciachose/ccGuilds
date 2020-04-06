// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.ArrayList;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildMemberKickEvent;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.UUID;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdWyrzuc
{
    public static void wyrzuc(final Player player, final String target) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final PlayerData data = DataManager.getPlayerData(playerUUID);
        if (data.getGuildData() != null) {
            final GuildData playerGuild = data.getGuildData();
            UUID targetUUID = null;
            if (playerGuild.getLeader().toString().equals(playerUUID.toString()) || (playerGuild.getDeputy() != null && playerGuild.getDeputy().toString().equals(playerUUID.toString()))) {
                Player targetPlayer = null;
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().equalsIgnoreCase(target)) {
                        targetPlayer = online;
                        break;
                    }
                }
                for (final UUID member : playerGuild.getMembers()) {
                    final String memberNick = DataManager.getPlayerName(member);
                    if (memberNick.equalsIgnoreCase(target)) {
                        targetUUID = member;
                        break;
                    }
                }
                if (targetPlayer != null) {
                    targetUUID = DataManager.getPlayerUUID(targetPlayer);
                }
                if (targetUUID == null) {
                    player.sendMessage(MessagesData.getMessage("wyrzuc.playerNotInDataBase"));
                    return;
                }
                if (!playerGuild.getMembers().contains(targetUUID)) {
                    player.sendMessage(MessagesData.getMessage("wyrzuc.playerIsntInYourGuild"));
                    return;
                }
                final PlayerData targetData = DataManager.getPlayerData(targetUUID);
                if (targetData.getGuildName() != null && targetData.getGuildName().toLowerCase().equals(playerGuild.getShortCut().toLowerCase())) {
                    if (targetUUID.toString().equals(playerGuild.getLeader().toString())) {
                        player.sendMessage(MessagesData.getMessage("wyrzuc.youCannotKickGuildLeader"));
                        return;
                    }
                    if (targetUUID.toString().equals(playerUUID.toString())) {
                        player.sendMessage(MessagesData.getMessage("wyrzuc.youCannotKickYourself"));
                        return;
                    }
                    if (playerGuild.getDeputy() != null && playerGuild.getDeputy().toString().equals(targetUUID.toString())) {
                        playerGuild.setDeputy(null);
                    }
                    targetData.setGuildName(null);
                    playerGuild.getMembers().remove(targetUUID);
                    playerGuild.setNeedSave(true);
                    DataManager.updateGuildData(playerGuild);
                    DataManager.updatePlayerData(targetUUID, targetData);
                    PubSub.broadcastMessage(MessagesData.getMessage("wyrzuc.playerWasKickedFromGuild").replace("%player", target).replace("%guildFullName", playerGuild.getFullName()).replace("%guildShortCut", playerGuild.getShortCut()));
                    final GuildMemberKickEvent guildMemberKickEvent = new GuildMemberKickEvent(System.currentTimeMillis(), playerGuild.getShortCut(), targetUUID, player.getUniqueId());
                    Redis.addGuildEvent(guildMemberKickEvent);
                    if (targetPlayer != null) {
                        CcScoreboards.refreshPlayer(player);
                        CcScoreboards.refreshPlayer(targetPlayer);
                        CcScoreboards.refreshPlayersFor(player);
                        CcScoreboards.refreshPlayersFor(targetPlayer);
                    }
                    else {
                        PubSub.setPrefixSuffix(targetUUID);
                        PubSub.setPrefixSuffixFor(targetUUID);
                    }
                }
                else if (CmdZapros.invitations.containsKey(targetUUID)) {
                    final ArrayList<String> invs = CmdZapros.invitations.get(targetUUID);
                    if (invs != null && invs.contains(playerGuild.getShortCut().toLowerCase())) {
                        invs.remove(playerGuild.getShortCut());
                        CmdZapros.invitations.put(targetUUID, invs);
                        if (targetPlayer != null && targetPlayer.isOnline()) {
                            targetPlayer.sendMessage(MessagesData.getMessage("wyrzuc.invitationToGuildCanceled").replace("%guildFullName", playerGuild.getFullName()).replace("%guildShortCut", playerGuild.getShortCut()));
                        }
                        player.sendMessage(MessagesData.getMessage("wyrzuc.youCanceledInvitationToThisPlayer"));
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("wyrzuc.playerIsntInvitedToYourGuild"));
                    }
                }
                else {
                    player.sendMessage(MessagesData.getMessage("wyrzuc.playerIsntInvitedToYourGuild"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("wyrzuc.youMustBeLeaderOrDeputyToDoThat"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("wyrzuc.youAreNotInAnyGuild"));
        }
    }
}
