// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.cctools.commands.Vanish;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.HashMap;

public class GuildShowManager
{
    private static HashMap<UUID, String> uuidFrom;
    
    public static void startTicker() {
        final Player[] array;
        int length;
        int i;
        Player player;
        TeleportLocation playerTeleportLocation;
        UUID uuid;
        PlayerData playerData;
        GuildData from;
        GuildData to;
        GuildData guildData;
        Bukkit.getScheduler().runTaskTimer((Plugin)CcGuilds.getPlugin(), () -> {
            Bukkit.getOnlinePlayers();
            for (length = array.length; i < length; ++i) {
                player = array[i];
                playerTeleportLocation = new TeleportLocation(player.getLocation());
                uuid = DataManager.getPlayerUUID(player);
                playerData = DataManager.getPlayerData(uuid);
                from = DataManager.getGuildData(GuildShowManager.uuidFrom.get(uuid));
                to = GuildManager.getGuildDataByLocation(playerTeleportLocation);
                if ((from == null && to != null) || (from != null && to != null && !from.getShortCut().equalsIgnoreCase(to.getShortCut()))) {
                    guildData = DataManager.getGuildData(playerData.getGuildName());
                    if (guildData != null) {
                        if (guildData.equals(to)) {
                            player.sendMessage(MessagesData.getMessage("guildShowInfos.guildInfoMember").replace("%shortCut", to.getShortCut()).replace("%fullName", to.getFullName()));
                        }
                        else if (guildData.isInAlliance(to)) {
                            if (!Vanish.isVanished(player)) {
                                PubSub.sendMessageToGuild(to.getShortCut(), MessagesData.getMessage("guildShowInfos.allyEnteredYourTerrain"));
                            }
                            player.sendMessage(MessagesData.getMessage("guildShowInfos.guildInfoAlly").replace("%shortCut", to.getShortCut()).replace("%fullName", to.getFullName()));
                        }
                        else {
                            player.sendMessage(MessagesData.getMessage("guildShowInfos.guildInfoEnemy").replace("%shortCut", to.getShortCut()).replace("%fullName", to.getFullName()));
                            if (!Vanish.isVanished(player)) {
                                PubSub.sendMessageToGuild(to.getShortCut(), MessagesData.getMessage("guildShowInfos.enemyEnteredYourTerrain"));
                            }
                        }
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("guildShowInfos.guildInfoEnemy").replace("%shortCut", to.getShortCut()).replace("%fullName", to.getFullName()));
                        if (!Vanish.isVanished(player)) {
                            PubSub.sendMessageToGuild(to.getShortCut(), MessagesData.getMessage("guildShowInfos.enemyEnteredYourTerrain"));
                        }
                    }
                    GuildShowManager.uuidFrom.put(uuid, to.getShortCut());
                }
                else if (from != null && to == null) {
                    player.sendMessage(MessagesData.getMessage("guildShowInfos.terrainDoesNotBelongToAnyGuild"));
                    GuildShowManager.uuidFrom.remove(uuid);
                }
            }
        }, 20L, 20L);
    }
    
    static {
        GuildShowManager.uuidFrom = new HashMap<UUID, String>();
    }
}
