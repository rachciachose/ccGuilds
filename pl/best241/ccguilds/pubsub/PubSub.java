// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.pubsub;

import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.CcGuilds;
import java.util.UUID;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import pl.best241.rdbplugin.JedisFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PubSub
{
    private static PubSubListener psl;
    private static Thread listenThread;
    public static AtomicInteger currentTickAliveTick;
    public static AtomicInteger lastSuccessfullCheckTickTime;
    
    public static void listen() {
        final Jedis jedis = JedisFactory.getInstance().getNewUnpooledJedis();
        final Jedis jedis2;
        (PubSub.listenThread = new Thread(() -> {
            System.out.println("Launched ccGuilds pubsub!");
            PubSub.lastSuccessfullCheckTickTime.set(PubSub.currentTickAliveTick.get());
            jedis2.subscribe((JedisPubSub)PubSub.psl, new String[] { "ccGuilds.broadcast", "ccGuilds.playerReload", "ccGuilds.guildReload", "ccGuilds.guildDelete", "ccGuilds.playerRefresh", "ccGuilds.playerRefreshFor", "ccGuilds.guildMessage", "ccGuilds.ping" });
        })).start();
    }
    
    public static void stopListen() {
        PubSub.psl.unsubscribe();
    }
    
    public static void reloadPlayerData(final UUID uuid, final Jedis jedis) {
        jedis.publish("ccGuilds.playerReload", CcGuilds.getKey() + ";" + uuid.toString());
    }
    
    public static void reloadGuildData(final String shortCut, final Jedis jedis) {
        jedis.publish("ccGuilds.guildReload", CcGuilds.getKey() + ";" + shortCut.toLowerCase());
    }
    
    public static void deleteGuildData(final String shortCut, final Jedis jedis) {
        jedis.publish("ccGuilds.guildDelete", CcGuilds.getKey() + ";" + shortCut.toLowerCase());
    }
    
    public static void deleteGuildData(final String shortCut) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccGuilds.guildDelete", CcGuilds.getKey() + ";" + shortCut.toLowerCase());
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void refreshPlayer(final UUID playerUUID, final Jedis jedis) {
        jedis.publish("ccGuilds.playerRefresh", playerUUID.toString());
    }
    
    public static void refreshPlayerFor(final UUID playerUUID, final Jedis jedis) {
        jedis.publish("ccGuilds.playerRefreshFor", playerUUID.toString());
    }
    
    public static void broadcastMessageAsync(final String message, final Jedis jedis) {
        jedis.publish("ccGuilds.broadcast", message);
    }
    
    public static void broadcastMessage(final String message) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccGuilds.broadcast", message);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setPrefixSuffix(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        refreshPlayer(uuid, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setPrefixSuffix(final Player player) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        setPrefixSuffix(playerUUID);
    }
    
    public static void setPrefixSuffixFor(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        refreshPlayerFor(uuid, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setPrefixSuffixFor(final Player player) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        setPrefixSuffixFor(playerUUID);
    }
    
    public static void sendMessageToGuildAsync(final String shortCut, final String message, final Jedis jedis) {
        jedis.publish("ccGuilds.guildMessage", shortCut.length() + shortCut + message);
    }
    
    public static void sendMessageToGuild(final String shortCut, final String message) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccGuilds.guildMessage", shortCut.length() + shortCut + message);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setupMessageToGuildChannel(final UUID uuid, final Boolean enabled) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccGuilds.guildMessageChannel", uuid.toString() + ":" + enabled.toString());
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setupMessageToAllyChannel(final UUID uuid, final Boolean enabled) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.publish("ccGuilds.allyMessageChannel", uuid.toString() + ":" + enabled.toString());
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void keepAliveTicker() {
        final Jedis jedis;
        final int currentTickIncremented;
        Bukkit.getScheduler().runTaskTimer((Plugin)CcGuilds.getPlugin(), () -> {
            jedis = JedisFactory.getInstance().getJedis();
            currentTickIncremented = PubSub.currentTickAliveTick.incrementAndGet();
            jedis.publish("ccGuilds.ping", "" + CcGuilds.getKey() + ":" + currentTickIncremented);
            JedisFactory.getInstance().returnJedis(jedis);
            if (PubSub.currentTickAliveTick.get() - PubSub.lastSuccessfullCheckTickTime.get() >= 2) {
                System.out.println("Redis ccGuilds PubSub crashed! Attemping to start new instance!");
                System.out.println("Killing dead ccGuilds pubsub thread...");
                if (PubSub.listenThread != null) {
                    PubSub.listenThread.stop();
                    PubSub.listenThread = null;
                }
                System.out.println("Launching ccGuilds pubsub...");
                listen();
            }
        }, 150L, 150L);
    }
    
    static {
        PubSub.psl = new PubSubListener();
        PubSub.listenThread = null;
        PubSub.currentTickAliveTick = new AtomicInteger(0);
        PubSub.lastSuccessfullCheckTickTime = new AtomicInteger(0);
    }
}
