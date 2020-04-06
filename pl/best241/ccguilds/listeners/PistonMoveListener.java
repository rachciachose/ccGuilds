// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.block.Block;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.Listener;

public class PistonMoveListener implements Listener
{
    @EventHandler
    public static void pistonMoveListener(final BlockPistonExtendEvent event) {
        for (final GuildData guild : DataManager.getAllGuilds().values()) {
            if (guild.getCuboid() == null) {
                continue;
            }
            for (final Block block : event.getBlocks()) {
                final TeleportLocation blockTeleportLoc = new TeleportLocation(block.getLocation());
                if (blockTeleportLoc.getSectorType().equals((Object)guild.getCuboid().getSectorType()) && block.getX() == guild.getCuboid().getX() && block.getY() == guild.getCuboid().getBaseHeigth() && block.getZ() == guild.getCuboid().getZ()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
