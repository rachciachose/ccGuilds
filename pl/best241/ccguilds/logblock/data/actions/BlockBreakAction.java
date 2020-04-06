// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data.actions;

import java.util.UUID;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.Material;

public class BlockBreakAction extends Action
{
    private Material blockDigged;
    private Material toolDigging;
    
    public BlockBreakAction(final SimpleLocation loc, final UUID uuid, final String name, final Material blockDigged, final Material toolDigging) {
        super(ActionType.BLOCK_BREAK, loc, uuid, name, System.currentTimeMillis());
        this.blockDigged = blockDigged;
        this.toolDigging = toolDigging;
    }
    
    public Material getBlockDigged() {
        return this.blockDigged;
    }
    
    public Material getToolDiggingBy() {
        return this.toolDigging;
    }
    
    public void setBlockDigged(final Material material) {
        this.blockDigged = material;
    }
}
