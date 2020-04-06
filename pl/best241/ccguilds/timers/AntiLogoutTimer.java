// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.timers;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import pl.best241.addonapi.bossbar.BossBarApi;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.data.DamageData;
import pl.best241.ccguilds.listeners.EntityDamageByEntityListener;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;

public class AntiLogoutTimer
{
    private static final int logout = 30000;
    
    public static void antiLogoutTimerRun() {
        Bukkit.getScheduler().runTaskTimer((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (EntityDamageByEntityListener.antiRelog == null) {
                        break;
                    }
                    final DamageData damageData = EntityDamageByEntityListener.antiRelog.get(player.getUniqueId());
                    if (damageData != null) {
                        final Long lastHit = System.currentTimeMillis() - damageData.time;
                        if (lastHit <= 30000L) {
                            final float part = 1.0f - lastHit / 30000.0f;
                            final String time = (30000L - lastHit) / 1000L + "";
                            BossBarApi.setName(player, MessagesData.getMessage("antirelog.timerStyle").replace("%time", time), part);
                        }
                        else {
                            EntityDamageByEntityListener.antiRelog.remove(player.getUniqueId());
                            BossBarApi.removeBar(player);
                        }
                    }
                }
            }
        }, 10L, 10L);
    }
}
