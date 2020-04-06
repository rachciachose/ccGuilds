// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.UUID;
import pl.best241.ccguilds.manager.DataManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import pl.best241.ccsectors.api.CcSectorsAPI;
import org.bukkit.entity.Player;

public class CmdCzlonek
{
    public static void czlonek(final Player player, final String targetName) {
        UUID targetUUID;
        try {
            targetUUID = CcSectorsAPI.getUUID(targetName);
        }
        catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Blad podczas pobierania UUID z bazy danych!");
            Logger.getLogger(CmdCzlonek.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (targetUUID != null) {
            final PlayerData targetPlayerData = DataManager.getPlayerData(targetUUID);
            final GuildData targetGuild = targetPlayerData.getGuildData();
            if (targetGuild != null) {
                final UUID uuid = player.getUniqueId();
                final PlayerData playerData = DataManager.getPlayerData(uuid);
                final GuildData playerGuild = playerData.getGuildData();
                if (playerGuild != null) {
                    if (playerGuild.equals(targetGuild)) {
                        if (playerGuild.isLeader(uuid)) {
                            if (playerGuild.isDeputy(targetUUID)) {
                                playerGuild.setDeputy(null);
                                DataManager.updateGuildData(playerGuild);
                                player.sendMessage(MessagesData.getMessage("czlonek.playerWasDegradated").replace("%nick", targetName));
                            }
                            else {
                                player.sendMessage(MessagesData.getMessage("czlonek.playerIsNotDeputyOfYourGuild"));
                            }
                        }
                        else {
                            player.sendMessage(MessagesData.getMessage("czlonek.youAreNotLeaderOfYourGuild"));
                        }
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("czlonek.targetPlayerIsNotInYourGuild"));
                    }
                }
                else {
                    player.sendMessage(MessagesData.getMessage("czlonek.youAreNotInAnyGuild"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("czlonek.playerIsNotInAnyGuild"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("czlonek.playerIsNotInDatabase"));
        }
    }
}
