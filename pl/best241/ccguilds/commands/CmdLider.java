// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildLeaderTransferEvent;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.UUID;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdLider
{
    public static void lider(final Player player, String newLeader) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        final GuildData playerGuild = playerData.getGuildData();
        if (playerGuild != null) {
            final UUID lastLeaderUUID = playerGuild.getLeader();
            if (playerGuild.getLeader().equals(playerUUID)) {
                if (!newLeader.equalsIgnoreCase(player.getName())) {
                    UUID newLeaderUUID = null;
                    for (final UUID memberUUID : playerGuild.getMembers()) {
                        final String memberName = DataManager.getPlayerName(memberUUID);
                        if (memberName.equalsIgnoreCase(newLeader)) {
                            newLeader = memberName;
                            newLeaderUUID = memberUUID;
                            break;
                        }
                    }
                    if (newLeaderUUID != null) {
                        playerGuild.setLeader(newLeaderUUID);
                        DataManager.updateGuildData(playerGuild.getShortCut().toLowerCase(), playerGuild);
                        PubSub.broadcastMessage(MessagesData.getMessage("lider.playerWasElectedToLeaderPosition").replace("%newLeader", newLeader).replace("%shortCut", playerGuild.getShortCut()).replace("%fullName", playerGuild.getFullName()));
                        PubSub.setPrefixSuffix(newLeaderUUID);
                        PubSub.setPrefixSuffixFor(newLeaderUUID);
                        TabManager.refreshPlayerGuild(player);
                        final GuildLeaderTransferEvent guildLeaderTransferEvent = new GuildLeaderTransferEvent(System.currentTimeMillis(), playerGuild.getShortCut(), newLeaderUUID, lastLeaderUUID);
                        Redis.addGuildEvent(guildLeaderTransferEvent);
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("lider.playerIsNotInYourGuild"));
                    }
                }
                else {
                    player.sendMessage(MessagesData.getMessage("lider.youCannotGiveLeaderYourself"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("lider.youMustBeLeaderToDoThat"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("lider.youDontBelongToAnyGuild"));
        }
    }
}
