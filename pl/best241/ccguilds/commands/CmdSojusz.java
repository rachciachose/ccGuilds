// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.WarData;
import java.util.ArrayList;
import redis.clients.jedis.Jedis;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.data.AllianceData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.rdbplugin.JedisFactory;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdSojusz
{
    public static void sojusz(final Player player, final String reqGuild) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName());
        if (guildData != null) {
            if (!guildData.getLeader().toString().equals(uuid.toString()) && (guildData.getDeputy() == null || !guildData.getDeputy().toString().equals(uuid.toString()))) {
                player.sendMessage(MessagesData.getMessage("sojusz.youMustBeLeaderOrDeputyToDoThat"));
                return;
            }
            final GuildData requestedGuild = DataManager.getGuildData(reqGuild.toLowerCase());
            if (requestedGuild != null) {
                if (guildData.getShortCut().equalsIgnoreCase(reqGuild)) {
                    player.sendMessage(MessagesData.getMessage("sojusz.yourGuildCannotApplyAllianceWithHerself"));
                    return;
                }
                if (haveAlliance(guildData, reqGuild)) {
                    player.sendMessage(MessagesData.getMessage("sojusz.yourGuildIsAlredyInAlliance"));
                    return;
                }
                if (!haveWar(guildData, reqGuild)) {
                    final Jedis jedis = JedisFactory.getInstance().getJedis();
                    final ArrayList<String> requestedGuildRequests = Redis.getGuildAlliancesRequest(reqGuild.toLowerCase(), jedis);
                    if (requestedGuildRequests.contains(guildData.getShortCut().toLowerCase())) {
                        final AllianceData alliace = new AllianceData(requestedGuild.getShortCut().toLowerCase(), guildData.getShortCut().toLowerCase());
                        guildData.addAlly(alliace);
                        requestedGuild.addAlly(alliace);
                        DataManager.updateGuildData(reqGuild.toLowerCase(), requestedGuild);
                        DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                        PubSub.broadcastMessage(MessagesData.getMessage("sojusz.guildMadeAllianceWithGuild").replace("%guild1fullName", guildData.getFullName()).replace("%guild1shortCut", guildData.getShortCut()).replace("%guild2fullName", requestedGuild.getFullName()).replace("%guild2shortCut", requestedGuild.getShortCut()));
                        Redis.removeGuildAllianceRequest(reqGuild.toLowerCase(), guildData.getShortCut().toLowerCase(), jedis);
                        Redis.removeGuildAllianceRequest(guildData.getShortCut().toLowerCase(), reqGuild.toLowerCase(), jedis);
                        CcScoreboards.refreshPlayer(player);
                        CcScoreboards.refreshPlayersFor(player);
                    }
                    else {
                        Redis.addGuildAlliancesRequest(guildData.getShortCut().toLowerCase(), reqGuild.toLowerCase(), jedis);
                        PubSub.sendMessageToGuild(reqGuild, MessagesData.getMessage("sojusz.guildPorposedYouAnAlliance").replace("%guildFullName", guildData.getFullName()).replace("%guildShortCut", guildData.getShortCut()));
                        player.sendMessage(MessagesData.getMessage("sojusz.youProposedThisGuildAnAlliance"));
                    }
                    JedisFactory.getInstance().returnJedis(jedis);
                }
                else {
                    player.sendMessage(MessagesData.getMessage("sojusz.youAreDuringWarWithThisGuild"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("sojusz.thisGuildDoesNotExists"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("sojusz.youAreNotMemberInAnyGuild"));
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
