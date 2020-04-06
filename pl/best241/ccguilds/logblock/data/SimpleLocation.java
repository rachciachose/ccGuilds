// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data;

import org.bukkit.Location;

public class SimpleLocation
{
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;
    
    public SimpleLocation(final Location loc) {
        this.worldName = loc.getWorld().getName();
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }
    
    public SimpleLocation(final String worldName, final int x, final int y, final int z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public String getWorldName() {
        return this.worldName;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public boolean equals(final SimpleLocation loc) {
        return this.getWorldName().equals(loc.getWorldName()) && this.getX() == loc.getX() && this.getY() == loc.getY() && this.getZ() == loc.getZ();
    }
}
