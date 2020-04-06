// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

import pl.best241.ccsectors.data.SectorType;

public class GuildHomeData
{
    private SectorType sectorType;
    private int x;
    private int y;
    private int z;
    
    public GuildHomeData(final SectorType sectorType, final int x, final int y, final int z) {
        this.sectorType = sectorType;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public SectorType getSectorType() {
        return this.sectorType;
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
    
    public void setX(final int x) {
        this.x = x;
    }
    
    public void setY(final int y) {
        this.y = y;
    }
    
    public void setZ(final int z) {
        this.z = z;
    }
    
    public void setSectorType(final SectorType sectorType) {
        this.sectorType = sectorType;
    }
}
