// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data.actions;

public enum ActionType
{
    BLOCK_BREAK((Class)BlockBreakAction.class), 
    BLOCK_PLACE((Class)BlockPlaceAction.class), 
    CHEST_ACTION((Class)ChestAction.class);
    
    private final Class classType;
    
    private ActionType(final Class classType) {
        this.classType = classType;
    }
    
    public Class getClassType() {
        return this.classType;
    }
}
