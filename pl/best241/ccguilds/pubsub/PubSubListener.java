// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.pubsub;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.visualeffects.VisualEffectType;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.CcGuilds;
import java.util.UUID;
import org.bukkit.Bukkit;
import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub
{
    public void onMessage(final String channel, final String message) {
        try {
            if (channel.equals("ccGuilds.broadcast")) {
                Bukkit.broadcastMessage(message);
            }
            else if (channel.equals("ccGuilds.playerReload")) {
                final String[] args = message.split(";");
                final int key = Integer.valueOf(args[0]);
                final UUID uuid = UUID.fromString(args[1]);
                if (key != CcGuilds.getKey()) {
                    DataManager.reloadPlayerDataAsync(uuid);
                }
            }
            else if (channel.equals("ccGuilds.guildReload")) {
                final String[] args = message.split(";");
                final int key = Integer.valueOf(args[0]);
                final String shortCut = args[1];
                if (key != CcGuilds.getKey()) {
                    DataManager.reloadGuildDataAsync(shortCut);
                }
            }
            else if (channel.equals("ccGuilds.guildDelete")) {
                final String[] args = message.split(";");
                final int key = Integer.valueOf(args[0]);
                final String shortCut = args[1];
                if (key != CcGuilds.getKey()) {
                    DataManager.deleteGuildDataAsync(shortCut);
                }
            }
            else if (channel.equals("ccGuilds.playerRefresh")) {
                final UUID uuid2 = UUID.fromString(message);
                Bukkit.getScheduler().runTask((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Player player = Bukkit.getPlayer(uuid2);
                        if (player != null && player.isOnline()) {
                            CcScoreboards.refreshPlayer(player);
                        }
                    }
                });
            }
            else if (channel.equals("ccGuilds.playerRefreshFor")) {
                final UUID uuid2 = UUID.fromString(message);
                Bukkit.getScheduler().runTask((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final Player player = Bukkit.getPlayer(uuid2);
                        if (player != null && player.isOnline()) {
                            CcScoreboards.refreshPlayersFor(player);
                        }
                    }
                });
            }
            else if (channel.equals("ccGuilds.guildMessage")) {
                final Integer shortCutLength = Integer.parseInt(message.substring(0, 1));
                String parsedMessage = message.substring(1);
                final String shortCut = parsedMessage.substring(0, shortCutLength);
                final String broadcastMessage;
                parsedMessage = (broadcastMessage = parsedMessage.substring(shortCutLength));
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final UUID uuid3 = DataManager.getPlayerUUID(player);
                    final PlayerData data = DataManager.getPlayerData(uuid3);
                    if (data.getGuildName() != null && data.getGuildName().equalsIgnoreCase(shortCut)) {
                        player.sendMessage(broadcastMessage);
                    }
                }
            }
            else if (channel.equals("ccGuilds.ping")) {
                final String[] splittedData = message.split(":");
                final int key = Integer.parseInt(splittedData[0]);
                if (key == CcGuilds.getKey()) {
                    final int tickAlive = Integer.parseInt(splittedData[1]);
                    PubSub.lastSuccessfullCheckTickTime.set(tickAlive);
                }
            }
            else if (channel.equals("ccGuilds.effectBuy")) {
                final String[] split = message.split(";");
                final String guild = split[0];
                final VisualEffectType effect = VisualEffectType.valueOf(split[1]);
                final long time = Long.parseLong(split[2]);
                final GuildData guildData = DataManager.getGuildData(guild.toLowerCase());
                guildData.getEffects().put(effect, time);
                DataManager.updateGuildData(guildData);
            }
            else if (channel.equals("ccGuilds.guildMessageChannel")) {
                final String[] split = message.split(":");
                final UUID uuid2 = UUID.fromString(split[0]);
                final boolean enabled = Boolean.valueOf(split[1]);
                if (enabled) {
                    DataManager.addToDgChannel(uuid2);
                }
                else {
                    DataManager.removeFromDgChannel(uuid2);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onPMessage(final String pattern, final String channel, final String message) {
    }
    
    public void onSubscribe(final String channel, final int subscribedChannels) {
    }
    
    public void onUnsubscribe(final String channel, final int subscribedChannels) {
    }
    
    public void onPUnsubscribe(final String pattern, final int subscribedChannels) {
    }
    
    public void onPSubscribe(final String pattern, final int subscribedChannels) {
    }
}
