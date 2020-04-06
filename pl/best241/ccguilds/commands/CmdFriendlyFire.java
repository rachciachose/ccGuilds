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

public class CmdFriendlyFire
{
    public static void friendlyfire(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guild = playerData.getGuildData();
        if (guild != null) {
            if (guild.getLeader().toString().equals(uuid.toString()) || (guild.getDeputy() != null && guild.getDeputy().toString().equals(uuid.toString()))) {
                guild.setFriendlyFire(!guild.getFriendlyFire());
                final String state = guild.getFriendlyFire() ? MessagesData.getMessage("friendlyFire.onName") : MessagesData.getMessage("friendlyFire.offName");
                PubSub.sendMessageToGuild(guild.getShortCut(), MessagesData.getMessage("friendlyFire.friendlyFireWasChanged").replace("%state", state));
            }
            else {
                player.sendMessage(MessagesData.getMessage("friendlyFire.youMustBeLeaderOrDeputyToDoThat"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("friendlyFire.youAreNotInAnyGuild"));
        }
    }
}
