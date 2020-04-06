// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import java.util.Date;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import java.util.Iterator;
import org.bukkit.Location;
import pl.best241.ccguilds.messages.MessagesData;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.manager.GuildManager;
import java.util.Calendar;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.Listener;

public class EntityExplodeListener implements Listener
{
    @EventHandler
    public static void entityExplodeListener(final EntityExplodeEvent event) {
        final TeleportLocation tntTeleportLoc = new TeleportLocation(event.getLocation());
        final Location tntLoc = event.getLocation();
        final int day = Calendar.getInstance().get(7);
        int startHour = -1;
        int endHour = -1;
        if (day == 6 && day == 7) {
            startHour = 100;
            endHour = 900;
        }
        else {
            startHour = 2300;
            endHour = 1300;
        }
        if (!isHourBetween(endHour, startHour)) {
            final GuildData guildAtLoc = GuildManager.getGuildDataByLocation(tntTeleportLoc);
            if (guildAtLoc != null) {
                guildAtLoc.setTntExplosionTime(System.currentTimeMillis());
                DataManager.updateGuildDataWithoutSyncing(guildAtLoc);
                Block blockToRemove = null;
                for (final Block block : event.blockList()) {
                    if (guildAtLoc.getCuboid().isBaseBlock(block)) {
                        blockToRemove = block;
                    }
                }
                if (blockToRemove != null) {
                    final Block finalBlock = blockToRemove;
                    event.blockList().remove(blockToRemove);
                    Bukkit.getScheduler().runTask((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            guildAtLoc.rebuildBaseBlock();
                        }
                    });
                }
            }
        }
        else {
            event.setCancelled(true);
            for (final Player online : Bukkit.getOnlinePlayers()) {
                if (online.getLocation().distance(tntLoc) <= 20.0) {
                    online.sendMessage(MessagesData.getMessage("blockPlaceEvent.tntExplodeBetweenHours").replace("%hour1", startHour / 100 + "").replace("%hour2", endHour / 100 + ""));
                }
            }
        }
    }
    
    public static boolean isHourBetween(final int to, final int from) {
        final Date date = new Date();
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        final int t = c.get(11) * 100 + c.get(12);
        final boolean isBetween = (to > from && t >= from && t <= to) || (to < from && (t >= from || t <= to));
        return isBetween;
    }
}
