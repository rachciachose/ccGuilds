// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.WarData;
import pl.best241.ccguilds.data.AllianceData;
import redis.clients.jedis.Jedis;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.rdbplugin.JedisFactory;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdPokoj
{
    public static void pokoj(final Player player, final String guild) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName().toLowerCase());
        if (guildData != null) {
            if (!guildData.getLeader().toString().equals(uuid.toString()) && (guildData.getDeputy() == null || !guildData.getDeputy().toString().equals(uuid.toString()))) {
                player.sendMessage(MessagesData.getMessage("pokoj.youMustBeLeaderOrDeputyToDoThat"));
                return;
            }
            final GuildData reqGuild = DataManager.getGuildData(guild.toLowerCase());
            if (reqGuild != null) {
                final Jedis jedis = JedisFactory.getInstance().getJedis();
                if (haveAlliance(guildData, guild)) {
                    final AllianceData guildAlly = getAllianceData(guildData, guild);
                    final AllianceData reqGuildAlly = getAllianceData(reqGuild, guild);
                    guildData.remAlly(guildAlly);
                    reqGuild.remAlly(reqGuildAlly);
                    DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                    DataManager.updateGuildData(reqGuild.getShortCut().toLowerCase(), reqGuild);
                    PubSub.broadcastMessage(MessagesData.getMessage("pokoj.guildHaveBrokenAllianceWithOtherGuild").replace("%guild1fullName", guildData.getFullName()).replace("%guild2fullName", reqGuild.getFullName()).replace("%guild1shortCut", guildData.getShortCut()).replace("%guild2shortCut", reqGuild.getShortCut()));
                }
                else if (haveWar(guildData, guild)) {
                    final WarData guildWarData = getWarData(guildData, guild.toLowerCase());
                    if (guildWarData.getChallangedGuild().equalsIgnoreCase(guildData.getShortCut())) {
                        if (guildWarData.getStartTime() > System.currentTimeMillis()) {
                            guildData.remWar(guildWarData);
                            final WarData reqWarData = getWarData(reqGuild, guild.toLowerCase());
                            reqGuild.remWar(reqWarData);
                            DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                            DataManager.updateGuildData(reqGuild.getShortCut().toLowerCase(), reqGuild);
                            CcScoreboards.refreshPlayer(player);
                            CcScoreboards.refreshPlayersFor(player);
                            PubSub.broadcastMessage(MessagesData.getMessage("pokoj.guildMadePeaceWithOtherGuild").replace("%guild1fullName", guildData.getFullName()).replace("%guild2fullName", reqGuild.getFullName()).replace("%guild1shortCut", guildData.getShortCut()).replace("%guild2shortCut", reqGuild.getShortCut()));
                        }
                        else {
                            player.sendMessage(MessagesData.getMessage("pokoj.warHasAlredyStarted"));
                        }
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("pokoj.notYourGuildStartedThisWar"));
                    }
                }
                else if (Redis.getGuildAlliancesRequest(guild.toLowerCase(), jedis).contains(guildData.getShortCut().toLowerCase())) {
                    PubSub.sendMessageToGuild(guild.toLowerCase(), MessagesData.getMessage("pokoj.guildRefusedAllianceAsk").replace("%guildShortCut", guildData.getShortCut()).replace("%guildFullName", guildData.getFullName()));
                    Redis.removeGuildAllianceRequest(guild.toLowerCase(), guildData.getShortCut().toLowerCase(), jedis);
                    Redis.removeGuildAllianceRequest(guildData.getShortCut().toLowerCase(), guild.toLowerCase(), jedis);
                }
                else {
                    player.sendMessage(MessagesData.getMessage("pokoj.yourGuildHasAlredyPeaceWithYou"));
                }
                JedisFactory.getInstance().returnJedis(jedis);
            }
            else {
                player.sendMessage(MessagesData.getMessage("pokoj.guildDoesNotExists"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("pokoj.youAreNotInAnyGuild"));
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
    
    public static AllianceData getAllianceData(final GuildData data, final String guild) {
        for (final AllianceData ally : data.getAlliances()) {
            if (ally.getAcceptedGuild().equalsIgnoreCase(guild) || ally.getRequestorGuild().equalsIgnoreCase(guild)) {
                return ally;
            }
        }
        return null;
    }
    
    public static WarData getWarData(final GuildData data, final String guild) {
        for (final WarData war : data.getWars()) {
            if (war.getChallangedGuild().equalsIgnoreCase(guild) || war.getTargetGuild().equalsIgnoreCase(guild)) {
                return war;
            }
        }
        return null;
    }
}
