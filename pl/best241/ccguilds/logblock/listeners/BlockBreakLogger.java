// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.listeners;

import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.logblock.data.actions.Action;
import pl.best241.ccguilds.logblock.data.LoggingManager;
import pl.best241.ccguilds.logblock.data.actions.BlockBreakAction;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class BlockBreakLogger implements Listener
{
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final ItemStack itemInHand = player.getItemInHand();
        Material tool;
        if (itemInHand == null) {
            tool = Material.AIR;
        }
        else {
            tool = itemInHand.getType();
        }
        final BlockBreakAction blockBreakAction = new BlockBreakAction(new SimpleLocation(block.getLocation()), player.getUniqueId(), player.getName(), block.getType(), tool);
        LoggingManager.addToQueue(blockBreakAction);
    }
    
    public static void enableLogger() {
        Bukkit.getPluginManager().registerEvents((Listener)new BlockBreakLogger(), (Plugin)CcGuilds.getPlugin());
    }
}
