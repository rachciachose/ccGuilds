// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import java.util.HashSet;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildDeleteEvent;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.ArrayList;

public class CmdRozwiaz
{
    private static ArrayList<UUID> confirmations;
    
    public static void rozwiaz(final Player player) {
        final UUID playerUUID = player.getUniqueId();
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        final GuildData data = playerData.getGuildData();
        if (data != null) {
            if (data.isLeader(playerUUID)) {
                if (CmdRozwiaz.confirmations.contains(playerUUID)) {
                    CmdRozwiaz.confirmations.remove(playerUUID);
                    final HashSet<UUID> members = data.getMembers();
                    final GuildDeleteEvent guildDeleteEvent = new GuildDeleteEvent(System.currentTimeMillis(), data.getShortCut(), playerUUID);
                    Redis.addGuildEvent(guildDeleteEvent);
                    DataManager.deleteGuildData(data.getShortCut().toLowerCase());
                    for (final UUID uuid : members) {
                        final PlayerData memberData = DataManager.getPlayerData(uuid);
                        memberData.setGuildName(null);
                        DataManager.updatePlayerData(uuid, memberData);
                        CcScoreboards.refreshPlayer(player);
                        CcScoreboards.refreshPlayersFor(player);
                        PubSub.setPrefixSuffix(uuid);
                        PubSub.setPrefixSuffixFor(uuid);
                        PubSub.deleteGuildData(data.getShortCut().toLowerCase());
                        TabManager.refreshPlayerGuild(player);
                    }
                    PubSub.broadcastMessage(MessagesData.getMessage("rozwiaz.guildTerminated").replace("%guildFullName", data.getFullName()).replace("%guildShortCut", data.getShortCut()).replace("%nick", player.getName()));
                }
                else {
                    player.sendMessage(MessagesData.getMessage("rozwiaz.toDeleteYourGuildTypeAgain"));
                    CmdRozwiaz.confirmations.add(playerUUID);
                    Bukkit.getScheduler().runTaskLater((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (CmdRozwiaz.confirmations.contains(playerUUID)) {
                                CmdRozwiaz.confirmations.remove(playerUUID);
                            }
                        }
                    }, 100L);
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("rozwiaz.toDoThatYouMustBeLeaderOfThisGuild"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("rozwiaz.youAreNotMemberInAnyGuild"));
        }
    }
    
    static {
        CmdRozwiaz.confirmations = new ArrayList<UUID>();
    }
}
