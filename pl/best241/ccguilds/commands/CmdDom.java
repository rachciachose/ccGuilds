// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import org.bukkit.Location;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccsectors.data.DimSwitch;
import pl.best241.ccsectors.data.SafeTeleportType;
import org.bukkit.scheduler.BukkitTask;
import pl.best241.ccguilds.data.GuildHomeData;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.data.DamageData;
import pl.best241.ccguilds.listeners.EntityDamageByEntityListener;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;
import java.util.UUID;
import java.util.ArrayList;

public class CmdDom
{
    public static ArrayList<UUID> teleporting;
    
    public static void baza(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData data = DataManager.getPlayerData(uuid);
        final GuildData guild = data.getGuildData();
        if (guild != null) {
            if (guild.getCuboid() != null) {
                final GuildHomeData guildHomeData = guild.getGuildHomeData();
                if (EntityDamageByEntityListener.antiRelog.get(player.getUniqueId()) != null) {
                    final DamageData damageData = EntityDamageByEntityListener.antiRelog.get(player.getUniqueId());
                    if (System.currentTimeMillis() - damageData.time <= MessagesData.bazaTpAntilogoutTime) {
                        player.sendMessage(MessagesData.getMessage("baza.youAreDuringFightCantTeleport:"));
                        return;
                    }
                }
                if (CmdDom.teleporting.contains(player.getUniqueId())) {
                    player.sendMessage(MessagesData.bazaYouAreAlredyTeleporting);
                    return;
                }
                player.sendMessage(MessagesData.bazaStartTeleporting);
                CmdDom.teleporting.add(player.getUniqueId());
                TeleportLocation loc;
                if (guildHomeData == null) {
                    loc = new TeleportLocation(guild.getCuboid().getSectorType(), guild.getCuboid().getX(), 1, guild.getCuboid().getZ());
                }
                else {
                    loc = new TeleportLocation(guildHomeData.getSectorType(), guildHomeData.getX(), guildHomeData.getY(), guildHomeData.getZ());
                }
                if (player.hasPermission("ccGildie.instantTeleport")) {
                    teleportRequest(player, loc, 0);
                }
                else if (player.hasPermission("ccGildie.fastTeleport")) {
                    teleportRequest(player, loc, 3);
                }
                else {
                    teleportRequest(player, loc, 6);
                }
            }
            else {
                player.sendMessage(MessagesData.bazaYourGuildHaveNoTerrain);
            }
        }
        else {
            player.sendMessage(MessagesData.bazaYouAreNotMemberOfThisGuild);
        }
    }
    
    public static void teleportRequest(final Player player, final TeleportLocation targetLoc, final int time) {
        final Location startLoc = player.getLocation();
        final ArrayList<BukkitTask> tasks = new ArrayList<BukkitTask>();
        if (time == 0) {
            try {
                CcSectorsAPI.teleportPlayerAuto(player.getUniqueId(), new TeleportLocation(player.getLocation()), targetLoc, SafeTeleportType.SAFE, DimSwitch.AUTO, false);
            }
            catch (Exception ex) {
                Logger.getLogger(CmdDom.class.getName()).log(Level.SEVERE, null, ex);
            }
            player.sendMessage(MessagesData.bazaTeleportedSuccessfully);
            CmdDom.teleporting.remove(player.getUniqueId());
            return;
        }
        for (int i = 1; i <= time; ++i) {
            final boolean last = i == time;
            final BukkitTask runTaskLater = Bukkit.getScheduler().runTaskLater((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    if (player == null || !player.isOnline()) {
                        if (player != null) {
                            CmdDom.teleporting.remove(player.getUniqueId());
                        }
                        return;
                    }
                    if (player.getLocation().getBlockX() != startLoc.getBlockX() || player.getLocation().getBlockY() != startLoc.getBlockY() || player.getLocation().getBlockZ() != startLoc.getBlockZ()) {
                        for (final BukkitTask task : tasks) {
                            task.cancel();
                        }
                        player.sendMessage(MessagesData.bazaTeleportationCanceled);
                        CmdDom.teleporting.remove(player.getUniqueId());
                        return;
                    }
                    if (last) {
                        try {
                            CcSectorsAPI.teleportPlayerAuto(player.getUniqueId(), new TeleportLocation(player.getLocation()), targetLoc, SafeTeleportType.SAFE, DimSwitch.AUTO, false);
                        }
                        catch (Exception ex) {
                            Logger.getLogger(CmdDom.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        player.sendMessage(MessagesData.bazaTeleportedSuccessfully);
                        CmdDom.teleporting.remove(player.getUniqueId());
                    }
                }
            }, (long)(20 * i));
            tasks.add(runTaskLater);
        }
    }
    
    public static Location findSafeLocation(final World world, final int x, final int z) {
        for (int y = 0; y < 256; ++y) {
            final Location loc1 = new Location(world, (double)x, (double)y, (double)z);
            final Location loc2 = new Location(world, (double)x, (double)(y + 1), (double)z);
            if (loc1.getBlock().getType() == Material.AIR && loc2.getBlock().getType() == Material.AIR) {
                return loc1;
            }
        }
        return null;
    }
    
    static {
        CmdDom.teleporting = new ArrayList<UUID>();
    }
}
