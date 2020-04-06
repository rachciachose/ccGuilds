// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildLeaveEvent;
import pl.best241.ccscoreboards.CcScoreboards;
import java.util.UUID;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdOpusc
{
    public static void opusc(final Player player) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        if (playerData.getGuildName() != null) {
            final GuildData guild = DataManager.getGuildData(playerData.getGuildName().toLowerCase());
            if (guild.getLeader().toString().equals(playerUUID.toString())) {
                player.sendMessage(MessagesData.getMessage("opusc.youCannotLeaveYouAreLeader"));
                return;
            }
            if (guild.getDeputy() != null && guild.getDeputy().toString().equals(playerUUID.toString())) {
                guild.setDeputy(null);
            }
            playerData.setGuildName(null);
            guild.getMembers().remove(playerUUID);
            DataManager.updateGuildData(guild.getShortCut().toLowerCase(), guild);
            DataManager.updatePlayerData(playerUUID, playerData);
            CcScoreboards.refreshPlayer(player);
            CcScoreboards.refreshPlayersFor(player);
            final GuildLeaveEvent guildLeaveEvent = new GuildLeaveEvent(System.currentTimeMillis(), guild.getShortCut(), playerUUID);
            Redis.addGuildEvent(guildLeaveEvent);
            PubSub.broadcastMessage(MessagesData.getMessage("opusc.playerLeftGuild").replace("%player", DataManager.getPlayerName(player.getUniqueId())).replace("%fullName", guild.getFullName()).replace("%shortCut", guild.getShortCut()));
        }
        else {
            player.sendMessage(MessagesData.getMessage("opusc.youDontBelongToAnyGuild"));
        }
    }
}
