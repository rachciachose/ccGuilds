// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.AllianceData;
import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.data.WarData;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdWojna
{
    public static void wojna(final Player player, final String requestGuild) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = playerData.getGuildData();
        if (guildData != null) {
            if (guildData.isDeputy(uuid) || guildData.isLeader(uuid)) {
                final GuildData reqGuild = DataManager.getGuildData(requestGuild.toLowerCase());
                if (reqGuild == null) {
                    player.sendMessage(MessagesData.getMessage("wojna.thisGuildDoesNotExist"));
                    return;
                }
                if (reqGuild.getShortCut().equalsIgnoreCase(guildData.getShortCut())) {
                    player.sendMessage(MessagesData.getMessage("wojna.youCannotMakeWarsWithYourself"));
                    return;
                }
                if (guildData.getWar(requestGuild) != null) {
                    player.sendMessage(MessagesData.getMessage("wojna.thisGuildIsAlredyInWarWithThatGuild"));
                    return;
                }
                if (haveAlliance(guildData, requestGuild)) {
                    player.sendMessage(MessagesData.getMessage("wojna.yourGuildHaveNoGuildWithThatGuild"));
                    return;
                }
                final WarData war = new WarData(guildData.getShortCut(), requestGuild, System.currentTimeMillis(), 0, 0);
                guildData.addWar(war);
                reqGuild.addWar(war);
                DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                DataManager.updateGuildData(requestGuild.toLowerCase(), reqGuild);
                CcScoreboards.refreshPlayer(player);
                CcScoreboards.refreshPlayersFor(player);
                PubSub.broadcastMessage(MessagesData.getMessage("wojna.guildChallengedGuildToWar").replace("%guildFullName", guildData.getFullName()).replace("%guildShortCut", guildData.getShortCut()).replace("%warGuildFullName", reqGuild.getFullName()).replace("%warGuildShortCut", reqGuild.getShortCut()));
            }
            else {
                player.sendMessage(MessagesData.getMessage("wojna.toDoThatYouMustBeLeader"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("wojna.youAreNotInAnyGuild"));
        }
    }
    
    public static boolean haveWar(final GuildData guild, final String shortCut) {
        if (guild.getWars() == null || guild.getWars().isEmpty()) {
            return false;
        }
        for (final WarData war : guild.getWars()) {
            if (war.getChallangedGuild().equalsIgnoreCase(shortCut) || war.getTargetGuild().equalsIgnoreCase(shortCut)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean haveAlliance(final GuildData guild, final String shortCut) {
        if (guild.getAlliances() == null || guild.getAlliances().isEmpty()) {
            return false;
        }
        for (final AllianceData ally : guild.getAlliances()) {
            if (ally.getAcceptedGuild().equalsIgnoreCase(shortCut) || ally.getRequestorGuild().equalsIgnoreCase(shortCut)) {
                return true;
            }
        }
        return false;
    }
}
