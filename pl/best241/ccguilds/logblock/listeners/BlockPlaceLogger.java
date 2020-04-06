// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.listeners;

import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import pl.best241.ccguilds.logblock.data.actions.Action;
import pl.best241.ccguilds.logblock.data.LoggingManager;
import pl.best241.ccguilds.logblock.data.actions.BlockPlaceAction;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class BlockPlaceLogger implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlockPlaced();
        final Player player = event.getPlayer();
        final BlockPlaceAction blockPlaceAction = new BlockPlaceAction(new SimpleLocation(block.getLocation()), player.getUniqueId(), player.getName(), block.getType());
        LoggingManager.addToQueue(blockPlaceAction);
    }
    
    public static void enableLogger() {
        Bukkit.getPluginManager().registerEvents((Listener)new BlockPlaceLogger(), (Plugin)CcGuilds.getPlugin());
    }
}
