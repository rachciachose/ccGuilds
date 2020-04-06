// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdDg
{
    public static void dg(final Player player, final String message) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName());
        if (guildData != null) {
            PubSub.sendMessageToGuild(guildData.getShortCut(), MessagesData.dgDgMessage.replace("%nick", player.getName()).replace("%message", message));
        }
        else {
            player.sendMessage(MessagesData.dgYouAreNotInAnyGuild);
        }
    }
    
    public static void dgChannel(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName());
        if (guildData != null) {
            PubSub.setupMessageToGuildChannel(uuid, !DataManager.isInDgChannel(uuid));
        }
        else {
            player.sendMessage(MessagesData.dgYouAreNotInAnyGuild);
        }
    }
}
