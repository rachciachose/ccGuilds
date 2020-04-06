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

public class CmdFriendlyFireBow
{
    public static void friendlyfirebow(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guild = playerData.getGuildData();
        if (guild != null) {
            if (guild.getLeader().toString().equals(uuid.toString()) || (guild.getDeputy() != null && guild.getDeputy().toString().equals(uuid.toString()))) {
                guild.setFriendlyFireBow(!guild.getFriendlyFireBow());
                final String state = guild.getFriendlyFireBow() ? MessagesData.getMessage("friendlyFireBow.onName") : MessagesData.getMessage("friendlyFireBow.offName");
                PubSub.sendMessageToGuild(guild.getShortCut(), MessagesData.getMessage("friendlyFireBow.friendlyFireWasChanged").replace("%state", state));
            }
            else {
                player.sendMessage(MessagesData.getMessage("friendlyFireBow.youMustBeLeaderOrDeputyToDoThat"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("friendlyFireBow.youAreNotInAnyGuild"));
        }
    }
}
