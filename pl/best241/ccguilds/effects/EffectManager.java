// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.effects;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import java.util.Iterator;
import java.util.UUID;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import pl.best241.rdbplugin.pubsub.PubSub;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EffectManager
{
    private static ConcurrentLinkedQueue<RecievedEffectData> recievedEffects;
    
    public static void broadcastEffect(final String shortCut, final EffectType type) {
        final String data = shortCut.toLowerCase() + ";" + type.toString();
        PubSub.broadcast("ccGuilds.effectBroadcast", data);
    }
    
    public static void recieveEffect(final String shortCut, final EffectType type) {
        final RecievedEffectData recievedEffectData = new RecievedEffectData(shortCut, type);
        EffectManager.recievedEffects.add(recievedEffectData);
    }
    
    public static void runEffectTicker() {
        Bukkit.getScheduler().runTaskTimer((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final RecievedEffectData data : EffectManager.recievedEffects) {
                    final long timeStarted = data.getTimeStarted();
                    final EffectType type = data.getEffectType();
                    if (timeStarted + type.getDuration() * 1000 <= System.currentTimeMillis()) {
                        EffectManager.recievedEffects.remove(data);
                    }
                    else if (data.getGuild() == null) {
                        EffectManager.recievedEffects.remove(data);
                    }
                    else {
                        for (final UUID uuid : data.getGuild().getMembers()) {
                            final Player guildMember = Bukkit.getPlayer(uuid);
                            if (guildMember != null && guildMember.isOnline()) {
                                EffectType.applyPotion(guildMember, type, timeStarted + type.getDuration() * 1000);
                            }
                        }
                    }
                }
            }
        }, 20L, 20L);
    }
    
    static {
        EffectManager.recievedEffects = new ConcurrentLinkedQueue<RecievedEffectData>();
    }
}
