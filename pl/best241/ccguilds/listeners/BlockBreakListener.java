// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.WarData;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.block.Block;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.data.GuildHomeData;
import pl.best241.ccguilds.data.CuboidData;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.GuildManager;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.Listener;

public class BlockBreakListener implements Listener
{
    @EventHandler
    public static void blockBreakListener(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }
        final Player player = event.getPlayer();
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = DataManager.getGuildData(playerData.getGuildName());
        final Block block = event.getBlock();
        final GuildData guildAtBlock = GuildManager.getGuildDataByBlock(block);
        if (guildAtBlock != null) {
            final TeleportLocation baseBlock = guildAtBlock.getCuboid().getBaseBlock();
            if ((guildData == null || (!guildAtBlock.equals(guildData) && (baseBlock.getBlockX() != block.getX() || baseBlock.getBlockY() != block.getY() || baseBlock.getBlockZ() != block.getZ()))) && !player.hasPermission("ccGuilds.adminPlace")) {
                event.setCancelled(true);
                player.sendMessage(MessagesData.getMessage("blockBreakEvent.youCannotBuildOnEnemyTerrain"));
            }
            if (baseBlock.getBlockX() == block.getX() && baseBlock.getBlockY() == block.getY() && baseBlock.getBlockZ() == block.getZ()) {
                event.setCancelled(true);
                if (guildData.equals(guildAtBlock)) {
                    player.sendMessage(MessagesData.getMessage("blockBreakEvent.youCannotDestroyYourHeart"));
                }
                else if (!guildData.equals(guildAtBlock)) {
                    if (guildData.isInWar(guildAtBlock)) {
                        if (guildAtBlock.getCreationTime() + Config.defaultTimeGuildProtection <= System.currentTimeMillis()) {
                            if (guildAtBlock.getLastCuboidDestoryTime() + Config.defaultWarCuboidDestroyProtection <= System.currentTimeMillis()) {
                                final WarData guildWar = guildData.getWar(guildAtBlock.getShortCut());
                                int guildKills;
                                int guildAtBlockKills;
                                if (guildWar.isAgressor(guildAtBlock.getShortCut())) {
                                    guildKills = guildWar.getTargetKills();
                                    guildAtBlockKills = guildWar.getChallengingKills();
                                }
                                else {
                                    guildKills = guildWar.getChallengingKills();
                                    guildAtBlockKills = guildWar.getTargetKills();
                                    if (guildWar.getStartTime() > System.currentTimeMillis()) {
                                        player.sendMessage(MessagesData.getMessage("blockBreakEvent.warHaventStartedYetYouCannotAttack"));
                                        return;
                                    }
                                }
                                int defenderPoints = guildAtBlock.getBasePoints();
                                int attackerPoints = guildData.getBasePoints();
                                final double transferPoints = 1.0;
                                defenderPoints -= (int)Math.ceil(transferPoints);
                                attackerPoints += (int)Math.ceil(transferPoints);
                                guildAtBlock.setBasePoints(defenderPoints);
                                guildData.setBasePoints(attackerPoints);
                                if (guildAtBlock.getBasePoints() <= 0) {
                                    guildAtBlock.setCuboid(null);
                                    guildAtBlock.setGuildHomeData(null);
                                    guildAtBlock.setLastCuboidDestoryTime(System.currentTimeMillis());
                                    PubSub.broadcastMessage(MessagesData.getMessage("blockBreakEvent.guildDestroyedGuildBase").replace("%guild1fullName", guildData.getFullName()).replace("%guild1shortCut", guildData.getShortCut()).replace("%guild2fullName", guildAtBlock.getFullName()).replace("%guild2shortCut", guildAtBlock.getShortCut()).replace("%kills1", guildKills + "").replace("%kills2", guildAtBlockKills + ""));
                                    PubSub.sendMessageToGuild(guildAtBlock.getShortCut(), MessagesData.getMessage("blockBreakEvent.youCanClaimTerrainWithCommand"));
                                }
                                else {
                                    PubSub.broadcastMessage(MessagesData.getMessage("blockBreakEvent.guildDamagedGuildBase").replace("%guild1fullName", guildData.getFullName()).replace("%guild1shortCut", guildData.getShortCut()).replace("%guild2fullName", guildAtBlock.getFullName()).replace("%guild2shortCut", guildAtBlock.getShortCut()).replace("%kills1", guildKills + "").replace("%kills2", guildAtBlockKills + ""));
                                }
                                guildAtBlock.removeWar(guildData.getShortCut());
                                guildData.removeWar(guildAtBlock.getShortCut());
                                DataManager.updateGuildData(guildData);
                                DataManager.updateGuildData(guildAtBlock);
                            }
                            else {
                                player.sendMessage(MessagesData.getMessage("blockBreakEvent.youCannotDestroyHeartOfThisGuildProtection").replace("%time", GuildManager.getTimeTo(guildAtBlock.getLastCuboidDestoryTime() + Config.defaultWarCuboidDestroyProtection)));
                            }
                        }
                        else {
                            player.sendMessage(MessagesData.getMessage("blockBreakEvent.guildIsProtected"));
                        }
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("blockBreakEvent.youCannotDestroyHeartYouMustBeInWar"));
                    }
                }
            }
        }
    }
    
    public static void main(final String... args) {
        int defenderPoints = 1;
        int attackerPoints = 3;
        final double transferPoints = 1.0;
        defenderPoints -= (int)Math.ceil(transferPoints);
        attackerPoints += (int)Math.ceil(transferPoints);
        System.out.println(defenderPoints + " " + attackerPoints);
    }
}
