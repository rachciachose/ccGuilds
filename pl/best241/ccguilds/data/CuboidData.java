// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

import org.bukkit.World;
import org.bukkit.block.Block;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccsectors.data.SectorType;

public class CuboidData
{
    private SectorType sectorType;
    private int radius;
    private int x;
    private int z;
    private int baseHeigth;
    
    public CuboidData(final SectorType world, final int radius, final int x, final int z, final int baseHeigth) {
        this.sectorType = world;
        this.radius = radius;
        this.x = x;
        this.z = z;
        this.baseHeigth = baseHeigth;
    }
    
    public CuboidData(final TeleportLocation loc, final int radius, final int baseHeigth) {
        this(loc.getSectorType(), radius, (int)loc.getX(), (int)loc.getZ(), baseHeigth);
    }
    
    public boolean isOnCuboid(final TeleportLocation loc) {
        return this.sectorType == loc.getSectorType() && loc.getX() <= this.x + this.radius && loc.getZ() <= this.z + this.radius && loc.getX() >= this.x - this.radius && loc.getZ() >= this.z - this.radius;
    }
    
    public boolean isOnCuboid(final TeleportLocation loc, final int modifier) {
        return this.sectorType == loc.getSectorType() && loc.getX() <= this.x + this.radius + modifier && loc.getZ() <= this.z + this.radius + modifier && loc.getX() >= this.x - this.radius - modifier && loc.getZ() >= this.z - this.radius - modifier;
    }
    
    public SectorType getSectorType() {
        return this.sectorType;
    }
    
    public int getMaxX() {
        return this.x + this.radius;
    }
    
    public int getMaxZ() {
        return this.z + this.radius;
    }
    
    public int getMinX() {
        return this.x - this.radius;
    }
    
    public int getMinZ() {
        return this.z - this.radius;
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public void setRadius(final int radius) {
        this.radius = radius;
    }
    
    public int getBaseHeigth() {
        return this.baseHeigth;
    }
    
    public boolean canBeComplicated(final CuboidData data, final int maxRadius) {
        final int xToMinX = (int)getDistance(this.getMinX(), this.getZ(), data.getX(), data.getZ());
        final int xToMaxX = (int)getDistance(this.getMaxX(), this.getZ(), data.getX(), data.getZ());
        final int zToMinZ = (int)getDistance(this.getZ(), this.getMinZ(), data.getX(), data.getZ());
        final int zToMaxZ = (int)getDistance(this.getZ(), this.getMaxZ(), data.getX(), data.getZ());
        final int diffX = Math.min(xToMinX, xToMaxX);
        final int diffZ = Math.min(zToMinZ, zToMaxZ);
        final int diff = Math.min(diffX, diffZ);
        return diff < maxRadius * 2;
    }
    
    public static double getDistance(final double x1, final double z1, final double x2, final double z2) {
        final double distance = Math.sqrt(Math.pow(x2 - x1, 2.0) + Math.pow(z2 - z1, 2.0));
        return distance;
    }
    
    public TeleportLocation getBaseBlock() {
        return new TeleportLocation(this.sectorType, this.x, this.baseHeigth, this.z);
    }
    
    public boolean isBaseBlock(final Block block) {
        SectorType blockSectorType = null;
        if (block.getWorld().getEnvironment() == World.Environment.NETHER) {
            blockSectorType = SectorType.NETHER;
        }
        else if (block.getWorld().getEnvironment() == World.Environment.NORMAL) {
            blockSectorType = SectorType.WORLD;
        }
        return blockSectorType == this.sectorType && block.getX() == this.x && block.getY() == this.baseHeigth && block.getZ() == this.z;
    }
}
