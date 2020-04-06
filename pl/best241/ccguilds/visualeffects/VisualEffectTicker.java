// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.visualeffects;

import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.Effect;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;

public class VisualEffectTicker
{
    public static void startTicker() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final PlayerData playerData = DataManager.getPlayerData(player.getUniqueId());
                    final GuildData guild = playerData.getGuildData();
                    if (guild != null) {
                        for (final VisualEffectType effect : guild.getEffects().keySet()) {
                            final long time = guild.getEffects().get(effect);
                            if (time >= System.currentTimeMillis()) {
                                if (effect == VisualEffectType.NUTKI) {
                                    player.getWorld().playEffect(player.getLocation().add(0.0, 0.8, 0.0), Effect.NOTE, 0);
                                }
                                if (effect == VisualEffectType.CHMURKI) {
                                    player.getWorld().playEffect(player.getLocation().add(0.0, 0.8, 0.0), Effect.CLOUD, 0);
                                }
                                if (effect == VisualEffectType.OGIEN) {
                                    player.getWorld().playEffect(player.getLocation().add(0.0, 0.8, 0.0), Effect.LAVA_POP, 0);
                                }
                                if (effect != VisualEffectType.SERCA) {
                                    continue;
                                }
                                player.getWorld().playEffect(player.getLocation().add(0.0, 0.8, 0.0), Effect.SLIME, 0);
                            }
                        }
                    }
                }
            }
        }, 5L, 5L);
    }
}
