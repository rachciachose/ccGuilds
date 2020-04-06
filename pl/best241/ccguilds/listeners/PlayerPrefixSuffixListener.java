// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccscoreboards.events.PlayerPrefixSuffixEvent;
import org.bukkit.event.Listener;

public class PlayerPrefixSuffixListener implements Listener
{
    @EventHandler
    public static void playerPrefixSuffixListener(final PlayerPrefixSuffixEvent event) {
        final Player player = event.getPlayer();
        final Player namedPlayer = event.getNamedPlayer();
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final UUID namedUUID = DataManager.getPlayerUUID(namedPlayer);
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        final PlayerData namedData = DataManager.getPlayerData(namedUUID);
        final GuildData playerGuild = playerData.getGuildData();
        final GuildData namedGuild = namedData.getGuildData();
        if (playerGuild == null && namedGuild == null) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildNullNamedGuildNull"));
        }
        else if (playerGuild == null && namedGuild != null) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildNullNamedGuildNotNull").replace("%shortcut", namedGuild.getShortCut()));
        }
        else if (playerGuild != null && namedGuild == null) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildNotNullNamedGuildNull"));
        }
        else if (playerGuild != null && namedGuild != null && playerGuild.isInAlliance(namedGuild)) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildAllyNamedGuild").replace("%shortcut", namedGuild.getShortCut()));
        }
        else if (playerGuild != null && namedGuild != null && playerGuild.isInWar(namedGuild)) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildWarNamedGuild").replace("%shortcut", namedGuild.getShortCut()));
        }
        else if (playerGuild != null && namedGuild != null && playerGuild.equals(namedGuild)) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildEqualsNamedGuild").replace("%shortcut", namedGuild.getShortCut()));
        }
        else if (playerGuild != null && namedGuild != null && !playerGuild.equals(namedGuild)) {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildNotEqualsNamedGuild").replace("%shortcut", namedGuild.getShortCut()));
        }
        else {
            event.setPrefix(MessagesData.getMessage("prefixSuffix.guildNullNamedGuildNull"));
        }
    }
}
