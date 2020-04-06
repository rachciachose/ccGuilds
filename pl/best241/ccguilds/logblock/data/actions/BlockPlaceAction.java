// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data.actions;

import java.util.UUID;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.Material;

public class BlockPlaceAction extends Action
{
    private final Material blockPlaced;
    
    public BlockPlaceAction(final SimpleLocation loc, final UUID uuid, final String name, final Material blockPlaced) {
        super(ActionType.BLOCK_PLACE, loc, uuid, name, System.currentTimeMillis());
        this.blockPlaced = blockPlaced;
    }
    
    public Material getBlockPlaced() {
        return this.blockPlaced;
    }
}
