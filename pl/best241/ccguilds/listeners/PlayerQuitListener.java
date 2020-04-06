// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.manager.RelogManager;
import pl.best241.ccsectors.events.SectorsServerQuitEvent;
import org.bukkit.event.Listener;

public class PlayerQuitListener implements Listener
{
    @EventHandler
    public static void onServerQuit(final SectorsServerQuitEvent event) {
        final Player player = event.getPlayer();
        RelogManager.checkRelog(player);
    }
}
