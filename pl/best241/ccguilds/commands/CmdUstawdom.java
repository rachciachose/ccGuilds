// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.data.GuildHomeData;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdUstawdom
{
    public static void ustawdom(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guild = DataManager.getGuildData(playerData.getGuildName());
        if (guild != null) {
            if (guild.getCuboid() != null) {
                if (guild.isLeader(uuid) || guild.isDeputy(uuid)) {
                    final TeleportLocation playerTeleportLoc = new TeleportLocation(player.getLocation());
                    if (guild.getCuboid().isOnCuboid(playerTeleportLoc)) {
                        final GuildHomeData guildHomeData = new GuildHomeData(playerTeleportLoc.getSectorType(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                        guild.setGuildHomeData(guildHomeData);
                        guild.setNeedSave(true);
                        DataManager.updateGuildData(guild);
                        player.sendMessage(MessagesData.getMessage("ustawdom.guildHomeSelected"));
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("ustawdom.youCannotSetGuildHomeHere"));
                    }
                }
                else {
                    player.sendMessage(MessagesData.getMessage("ustawdom.toDoThatYouMustBeLeaderOrDeputyOfThisGuild"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("ustawdom.yourGuildHaventGotCuboid"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("ustawdom.youAreNotInAnyGuild"));
        }
    }
}
