// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import java.util.UUID;
import org.bukkit.World;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccdeathbans.api.CcDeathBansAPI;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.CcSectors;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccsectors.data.PlayerData;
import pl.best241.ccsectors.data.DataStore;
import pl.best241.ccscoreboards.CcScoreboards;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import pl.best241.ccsectors.api.TeleportLocation;
import org.bukkit.inventory.ItemStack;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.messages.MessagesData;
import org.bukkit.entity.Arrow;
import pl.best241.ccguilds.data.DamageData;
import pl.best241.ccguilds.listeners.EntityDamageByEntityListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RelogManager
{
    public static Location checkRelog(final Player player) {
        if (player == null || player.getName() == null || EntityDamageByEntityListener.antiRelog == null) {
            return null;
        }
        final DamageData damageData = EntityDamageByEntityListener.antiRelog.get(player.getUniqueId());
        if (damageData != null && System.currentTimeMillis() - damageData.time <= 29000L) {
            Player damager;
            if (damageData.damager instanceof Player) {
                damager = (Player)damageData.damager;
            }
            else {
                if (!(damageData instanceof Arrow)) {
                    return null;
                }
                final Arrow arrow = (Arrow)damageData;
                if (!(arrow.getShooter() instanceof Player)) {
                    return null;
                }
                damager = (Player)arrow.getShooter();
            }
            final pl.best241.ccguilds.data.PlayerData damagerPlayerData = DataManager.getPlayerData(damager.getUniqueId());
            final pl.best241.ccguilds.data.PlayerData victimPlayerData = DataManager.getPlayerData(player.getUniqueId());
            final GuildData damagerGuild = damagerPlayerData.getGuildData();
            final GuildData victimGuild = victimPlayerData.getGuildData();
            String victimGuildFormat = "";
            if (victimGuild != null) {
                victimGuildFormat = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", victimGuild.getShortCut());
            }
            String attackerGuildFormat = "";
            if (damagerGuild != null) {
                attackerGuildFormat = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", damagerGuild.getShortCut());
            }
            PubSub.broadcastMessage(MessagesData.getMessage("antirelog.playerLogOutDuringFight").replace("%player", player.getName()));
            if (damager != null) {
                final int calculatedRank = RankManager.calculateRank(player, damager);
                String points;
                if (calculatedRank > 0) {
                    points = "+" + calculatedRank;
                }
                else {
                    points = calculatedRank + "";
                }
                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager.getName()).replace("%points", points).replace("%victimGuildFormat", victimGuildFormat).replace("%damagerGuildFormat", attackerGuildFormat));
            }
            player.getWorld().strikeLightningEffect(player.getLocation());
            for (final ItemStack is : player.getInventory().getContents()) {
                if (is != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), is);
                    player.getInventory().removeItem(new ItemStack[] { is });
                }
            }
            for (final ItemStack is : player.getInventory().getArmorContents()) {
                if (is.getAmount() != 0) {
                    player.getWorld().dropItemNaturally(player.getLocation(), is);
                }
            }
            player.getInventory().setArmorContents(new ItemStack[4]);
            final TeleportLocation loc = new TeleportLocation(player.getWorld().getSpawnLocation());
            final int orbs = (int)player.getExp();
            final int level = player.getLevel();
            final int total = (2 * level * level + 5 * orbs) / 5;
            final World world = player.getWorld();
            ((ExperienceOrb)world.spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB)).setExperience(total);
            player.setExp(0.0f);
            player.setLevel(0);
            player.teleport(player.getWorld().getSpawnLocation());
            player.setFoodLevel(20);
            player.setHealth(20.0);
            player.getActivePotionEffects().clear();
            CcScoreboards.refreshPlayer(damager);
            CcScoreboards.refreshPlayer(player);
            TabManager.refreshPlayerStats(player);
            TabManager.refreshPlayerStats(damager);
            final PlayerData oldData = DataStore.getPlayerData(player.getUniqueId());
            final PlayerData newData = new PlayerData(player, oldData.getDimSwitch(), oldData.getSafeTeleportType());
            newData.setLocation(loc);
            newData.setLastSector(CcSectorsAPI.getCorrectSectorName(loc));
            try {
                CcSectors.getBackend().setPlayerData(player.getUniqueId(), newData);
            }
            catch (Exception ex) {
                Logger.getLogger(RelogManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            try {
                CcSectors.getBackend().setPlayerLastSector(player.getUniqueId(), CcSectorsAPI.getCorrectSectorName(loc));
            }
            catch (Exception ex) {
                Logger.getLogger(RelogManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            DataStore.setPlayerData(player.getUniqueId(), newData);
            EntityDamageByEntityListener.antiRelog.remove(player.getUniqueId());
            CcDeathBansAPI.banPlayerDeath(player);
            return player.getWorld().getSpawnLocation();
        }
        return null;
    }
    
    public static boolean isInFight(final UUID uuid) {
        final DamageData damageData = EntityDamageByEntityListener.antiRelog.get(uuid);
        return damageData != null && System.currentTimeMillis() - damageData.time <= 30000L;
    }
}
