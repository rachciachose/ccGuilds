// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.backend;

import java.util.concurrent.TimeUnit;
import org.bukkit.Material;

public class Config
{
    public static int defaultMinDistanceToSector;
    public static int defaultRankToCreateGuild;
    public static int defaultBasePoints;
    public static int defaultBlocksPerIncrease;
    public static int defaultGuildRadius;
    public static int defaultMaxGuildRadiusIncreased;
    public static int defaultBaseBlockFrom;
    public static int defaultBaseBlockTo;
    public static int defaultBlockTimeAfterTntExplosion;
    public static long defaultWarStartDelay;
    public static long defaultWarCuboidDestroyProtection;
    public static int defaultGuildRank;
    public static int defaultBasePointsAfterCuboidRenew;
    public static long defaultTimeToWarExpire;
    public static long defaultTimeGuildProtection;
    public static long defaultTimeBetweenBaseCreations;
    public static Material baseBlock;
    public static boolean displayPolishItemsWhenNotComplete;
    
    static {
        Config.defaultMinDistanceToSector = 40;
        Config.defaultRankToCreateGuild = 1100;
        Config.defaultBasePoints = 2;
        Config.defaultBlocksPerIncrease = 2;
        Config.defaultGuildRadius = 15;
        Config.defaultMaxGuildRadiusIncreased = 35;
        Config.defaultBaseBlockFrom = 50;
        Config.defaultBaseBlockTo = 80;
        Config.defaultBlockTimeAfterTntExplosion = 120000;
        Config.defaultWarStartDelay = 3600000L;
        Config.defaultWarCuboidDestroyProtection = 10800000L;
        Config.defaultGuildRank = 1000;
        Config.defaultBasePointsAfterCuboidRenew = 1;
        Config.defaultTimeToWarExpire = 259200000L;
        Config.defaultTimeGuildProtection = 14400000L;
        Config.defaultTimeBetweenBaseCreations = TimeUnit.MINUTES.toMillis(10L);
        Config.baseBlock = Material.EMERALD_BLOCK;
        Config.displayPolishItemsWhenNotComplete = false;
    }
}
