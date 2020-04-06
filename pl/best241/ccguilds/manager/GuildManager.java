// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import pl.best241.ccguilds.data.CuboidData;
import java.util.Iterator;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.data.GuildData;
import org.bukkit.block.Block;

public class GuildManager
{
    public static GuildData getGuildDataByBlock(final Block block) {
        return getGuildDataByLocation(new TeleportLocation(block.getLocation()));
    }
    
    public static GuildData getGuildDataByLocation(final TeleportLocation loc) {
        for (final GuildData data : DataManager.getAllGuilds().values()) {
            if (data.getCuboid() == null) {
                continue;
            }
            if (data.getCuboid().isOnCuboid(loc)) {
                return data;
            }
        }
        return null;
    }
    
    public static GuildData getGuildDataByFullName(final String fullName) {
        for (final GuildData data : DataManager.getAllGuilds().values()) {
            if (data.getFullName().equalsIgnoreCase(fullName)) {
                return data;
            }
        }
        return null;
    }
    
    public static String getTimeTo(final long to) {
        final long now = System.currentTimeMillis();
        long diff = to - now;
        long seconds = diff / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        final long days = hours / 24L;
        diff -= seconds * 1000L;
        seconds -= minutes * 60L;
        minutes -= hours * 60L;
        hours -= days * 24L;
        String time = "";
        if (days != 0L) {
            time = time + days + "d ";
        }
        if (hours != 0L) {
            time = time + hours + "h ";
        }
        if (minutes != 0L) {
            time = time + minutes + "m ";
        }
        if (seconds != 0L) {
            time = time + seconds + "s ";
        }
        return time;
    }
    
    public static boolean otherGuildsOnThisArea(final CuboidData cuboid, final int maxRadius) {
        for (final String guildShortcut : DataManager.getAllGuilds().keySet()) {
            final GuildData data = DataManager.getGuildData(guildShortcut.toLowerCase());
            final CuboidData guildCuboid = data.getCuboid();
            if (guildCuboid == null) {
                continue;
            }
            if (guildCuboid.canBeComplicated(cuboid, maxRadius)) {
                return true;
            }
        }
        return false;
    }
    
    public static int calculateGuildRank(final GuildData victim, final GuildData damager) {
        if (victim == null || damager == null) {
            return 0;
        }
        if (victim.equals(damager)) {
            return 0;
        }
        int victimRank = victim.getRank();
        int damagerRank = damager.getRank();
        final int x = victimRank - damagerRank + 300;
        int damager_reward = 0;
        int victim_reward = 0;
        if (x >= 0) {
            damager_reward = (int)(Math.round(Math.pow(x / 20, 0.95)) + 30L);
            victim_reward = (int)(-(Math.round(Math.pow(x / 20, 0.9)) + 20L));
        }
        else {
            damager_reward = (int)(-Math.round(Math.pow(Math.abs(x / 34), 0.59))) + 3;
            victim_reward = (int)(-Math.round(Math.pow(Math.abs(x / 22), 0.46)));
        }
        damagerRank += damager_reward;
        victimRank += victim_reward;
        victim.setRank(victimRank);
        damager.setRank(damagerRank);
        victim.setGuildDeaths(victim.getGuildDeaths() + 1);
        damager.setGuildKills(damager.getGuildKills() + 1);
        victim.setNeedSave(true);
        damager.setNeedSave(true);
        DataManager.updateGuildData(victim);
        DataManager.updateGuildData(damager);
        return damager_reward;
    }
}
