// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.best241.ccguilds.data.DamageData;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class EntityDamageByEntityListener implements Listener
{
    public static HashMap<UUID, DamageData> antiRelog;
    
    @EventHandler(priority = EventPriority.HIGH)
    public static void entityDamageByEntityListener(final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            Player damager = null;
            if (event.getDamager() instanceof Player) {
                damager = (Player)event.getDamager();
                final PlayerData playerData = DataManager.getPlayerData(player.getUniqueId());
                final GuildData playerGuild = playerData.getGuildData();
                final PlayerData damagerData = DataManager.getPlayerData(damager.getUniqueId());
                final GuildData damagerGuild = damagerData.getGuildData();
                if (playerGuild != null && damagerGuild != null && playerGuild.equals(damagerGuild) && !playerGuild.getFriendlyFire()) {
                    event.setCancelled(true);
                }
            }
            else if (event.getDamager() instanceof Arrow) {
                final Arrow arrow = (Arrow)event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    damager = (Player)arrow.getShooter();
                    final PlayerData playerData2 = DataManager.getPlayerData(player.getUniqueId());
                    final GuildData playerGuild2 = playerData2.getGuildData();
                    final PlayerData damagerData2 = DataManager.getPlayerData(damager.getUniqueId());
                    final GuildData damagerGuild2 = damagerData2.getGuildData();
                    if (playerGuild2 != null && damagerGuild2 != null && playerGuild2.equals(damagerGuild2) && !playerGuild2.getFriendlyFireBow()) {
                        event.setCancelled(true);
                    }
                }
            }
            if (damager == null) {
                return;
            }
            if (!event.isCancelled()) {
                EntityDamageByEntityListener.antiRelog.put(damager.getUniqueId(), new DamageData((Entity)player, 30L));
                EntityDamageByEntityListener.antiRelog.put(player.getUniqueId(), new DamageData((Entity)damager, 30L));
            }
        }
    }
    
    static {
        EntityDamageByEntityListener.antiRelog = new HashMap<UUID, DamageData>();
    }
}
