// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import redis.clients.jedis.Jedis;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import java.util.ArrayList;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.rdbplugin.JedisFactory;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class PlayerJoinListener implements Listener
{
    @EventHandler
    public static void playerJoinListener(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final UUID uuid = player.getUniqueId();
        Redis.addPlayer(uuid, jedis);
        DataManager.updatePlayerName(uuid, player.getName());
        DataManager.updatePlayerUUID(player.getName(), uuid);
        PlayerData data = DataManager.getPlayerData(uuid);
        if (data == null) {
            data = new PlayerData(uuid, null, -1L, -1L, 1000, 0, 0, new ArrayList<UUID>(), new ArrayList<UUID>());
            data.setNeedSave(true);
            DataManager.updatePlayerData(uuid, data);
        }
        CcScoreboards.refreshPlayersFor(player);
        CcScoreboards.refreshPlayer(player);
        TabManager.refreshTab(player);
        CcScoreboards.refreshPlayerScore(player);
        JedisFactory.getInstance().returnJedis(jedis);
    }
}
