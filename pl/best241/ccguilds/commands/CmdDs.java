// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.data.AllianceData;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdDs
{
    public static void ds(final Player player, final String message) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName());
        if (guildData != null) {
            PubSub.sendMessageToGuild(guildData.getShortCut(), MessagesData.dsMessageToGuilds.replace("%guild", guildData.getShortCut()).replace("%nick", player.getName()).replace("%message", message));
            for (final AllianceData ally : guildData.getAlliances()) {
                GuildData allyGuild;
                if (ally.getAcceptedGuild().equalsIgnoreCase(guildData.getShortCut())) {
                    allyGuild = DataManager.getGuildData(ally.getRequestorGuild());
                }
                else {
                    allyGuild = DataManager.getGuildData(ally.getAcceptedGuild());
                }
                if (allyGuild == null) {
                    continue;
                }
                PubSub.sendMessageToGuild(allyGuild.getShortCut(), MessagesData.dsMessageToGuilds.replace("%guild", guildData.getShortCut()).replace("%nick", player.getName()).replace("%message", message));
            }
        }
        else {
            player.sendMessage(MessagesData.dsYouAreNotInAnyGuild);
        }
    }
}
