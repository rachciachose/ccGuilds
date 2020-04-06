// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.GuildManager;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.Listener;

public class PlayerBucketEmptyListener implements Listener
{
    @EventHandler
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        final PlayerData playerData = DataManager.getPlayerData(player.getUniqueId());
        final GuildData guildData = playerData.getGuildData();
        final GuildData guildOnBlock = GuildManager.getGuildDataByBlock(block);
        if (guildOnBlock != null && !player.hasPermission("ccGuilds.adminDestroy")) {
            if (guildOnBlock.equals(guildData)) {
                return;
            }
            event.setCancelled(true);
            player.sendMessage(MessagesData.getMessage("bucketEmptyEvent.thisTerrainDoesNotBelongToYourGuild"));
        }
        if (guildOnBlock == null && event.getBucket() == Material.LAVA_BUCKET) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(MessagesData.getMessage("bucketEmptyEvent.cannotEmptyLavaOnNeutralTerrain"));
        }
    }
    
    @EventHandler
    public void onLavaDrop(final PlayerBucketFillEvent event) {
    }
}
