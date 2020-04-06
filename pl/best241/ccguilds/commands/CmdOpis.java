// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdOpis
{
    public static void opis(final Player player, final String description) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = playerData.getGuildData();
        if (guildData != null) {
            if (guildData.isLeader(uuid) || guildData.isDeputy(uuid)) {
                guildData.setDescription(description);
                DataManager.updateGuildData(guildData);
                player.sendMessage(MessagesData.getMessage("opis.descriptionChanged"));
            }
            else {
                player.sendMessage(MessagesData.getMessage("opis.toDoThatYouMustBeLeaderOrDeputy"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("opis.youDontBelongToAnyGuild"));
        }
    }
}
