// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.CuboidData;
import java.util.Iterator;
import org.bukkit.block.Block;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.manager.RelogManager;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.data.GuildData;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.GuildManager;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class BlockPlaceListener implements Listener
{
    @EventHandler
    public static void blockPlaceListener(final BlockPlaceEvent event) {
        System.out.println("Block place event " + event.getBlock().getType());
        final Player player = event.getPlayer();
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName());
        final Block block = event.getBlock();
        final GuildData guildAtBlock = GuildManager.getGuildDataByBlock(block);
        if (guildAtBlock != null && !player.hasPermission("ccGuilds.adminDestroy")) {
            if (guildData == null || !guildAtBlock.getShortCut().equalsIgnoreCase(guildData.getShortCut())) {
                event.setCancelled(true);
                player.sendMessage(MessagesData.getMessage("blockPlaceEvent.youCannotBuildOnEnemyTerrain"));
            }
            else if (guildData.getLastTntExplosionTime() + Config.defaultBlockTimeAfterTntExplosion >= System.currentTimeMillis()) {
                event.setCancelled(true);
                player.sendMessage(MessagesData.getMessage("blockPlaceEvent.onYourTerrainTNThadExplodedCannotBuild").replace("%time", (System.currentTimeMillis() - guildData.getLastTntExplosionTime() - Config.defaultBlockTimeAfterTntExplosion) / 1000L + ""));
            }
        }
        boolean isOnOrNearCuboid = false;
        if (guildAtBlock != null) {
            isOnOrNearCuboid = true;
        }
        else {
            final Block blockPlacedOn = block.getRelative(BlockFace.DOWN);
            if (block.getType() == Material.TNT && (blockPlacedOn.getType() == Material.GRASS || blockPlacedOn.getType() == Material.DIRT || blockPlacedOn.getType() == Material.SAND)) {
                event.setCancelled(true);
                player.sendMessage(MessagesData.getMessage("blockPlaceEvent.cannotPlaceTntOnDefinedBlocks"));
            }
            for (final GuildData guildDataToCuboidCheck : DataManager.getAllGuilds().values()) {
                final CuboidData cuboidToCheck = guildDataToCuboidCheck.getCuboid();
                if (cuboidToCheck == null) {
                    continue;
                }
                if (cuboidToCheck.isOnCuboid(new TeleportLocation(player.getLocation()), 15)) {
                    isOnOrNearCuboid = true;
                    break;
                }
            }
        }
        if (isOnOrNearCuboid && player.getLocation().getY() <= 30.0 && RelogManager.isInFight(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(MessagesData.getMessage("blockPlaceEvent.cannotBuildInFightUnderCerainLevel"));
        }
    }
}
