// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildDeputyTransferEvent;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.UUID;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdZastepca
{
    public static void zastepca(final Player player, String target) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName().toLowerCase());
        if (guildData != null) {
            final UUID lastDeputy = guildData.getDeputy();
            if (playerUUID.toString().equals(guildData.getLeader().toString()) || (guildData.getDeputy() != null && guildData.getDeputy().toString().equals(playerUUID.toString()))) {
                UUID targetUUID = null;
                boolean loopAgain = true;
                for (final UUID member : guildData.getMembers()) {
                    String memberName = DataManager.getPlayerName(member);
                    if (memberName == null) {
                        memberName = DataManager.getPlayerNameFromDb(member);
                        DataManager.updatePlayerName(member, memberName);
                        DataManager.updatePlayerUUID(memberName, member);
                    }
                    if (memberName.equals(target)) {
                        targetUUID = member;
                        target = DataManager.getPlayerName(member);
                        loopAgain = false;
                        break;
                    }
                }
                if (loopAgain) {
                    for (final UUID member : guildData.getMembers()) {
                        String memberName = DataManager.getPlayerName(member);
                        if (memberName == null) {
                            memberName = DataManager.getPlayerNameFromDb(member);
                            DataManager.updatePlayerName(member, memberName);
                            DataManager.updatePlayerUUID(memberName, member);
                        }
                        if (memberName.equalsIgnoreCase(target)) {
                            targetUUID = member;
                            target = DataManager.getPlayerName(member);
                            break;
                        }
                    }
                }
                if (targetUUID != null) {
                    if (targetUUID.toString().equals(playerUUID.toString())) {
                        player.sendMessage(MessagesData.getMessage("zastepca.youCannotGiveDeputyYourself"));
                        return;
                    }
                    if (guildData.getLeader().toString().equals(targetUUID.toString())) {
                        player.sendMessage(MessagesData.getMessage("zastepca.playerIsLeaderInYourGuild"));
                        return;
                    }
                    if (guildData.getDeputy() != null && guildData.getDeputy().toString().equals(targetUUID.toString())) {
                        player.sendMessage(MessagesData.getMessage("zastepca.playerIsAlredyDeputy"));
                        return;
                    }
                    final GuildDeputyTransferEvent guildDeputyTransferEvent = new GuildDeputyTransferEvent(System.currentTimeMillis(), guildData.getShortCut(), targetUUID, lastDeputy);
                    Redis.addGuildEvent(guildDeputyTransferEvent);
                    guildData.setDeputy(targetUUID);
                    DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                    PubSub.broadcastMessage(MessagesData.getMessage("zastepca.playerWasNominatedToBeDeputy").replace("%player", target).replace("%guildFullName", guildData.getFullName()).replace("%guildShortCut", guildData.getShortCut()));
                    PubSub.setPrefixSuffix(targetUUID);
                    PubSub.setPrefixSuffixFor(targetUUID);
                }
                else {
                    player.sendMessage(MessagesData.getMessage("zastepca.playerIsNotInYourGuild"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("zastepca.youMustBeLeaderOrDeputyToDoThat"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("zastepca.youAreNotInAnyGuild"));
        }
    }
}
