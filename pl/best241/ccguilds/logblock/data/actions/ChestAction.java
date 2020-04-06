// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data.actions;

import java.util.UUID;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.Material;

public class ChestAction extends Action
{
    private final Material material;
    private final short data;
    private final int amount;
    
    public ChestAction(final SimpleLocation loc, final UUID uuid, final String name, final Material material, final short data, final int amount) {
        super(ActionType.CHEST_ACTION, loc, uuid, name, System.currentTimeMillis());
        this.material = material;
        this.data = data;
        this.amount = amount;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public short getData() {
        return this.data;
    }
    
    public int getAmount() {
        return this.amount;
    }
}
