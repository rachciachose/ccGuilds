// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data.actions;

import java.util.UUID;
import pl.best241.ccguilds.logblock.data.SimpleLocation;

public class Action
{
    private ActionType type;
    private Long time;
    private SimpleLocation loc;
    private UUID uuid;
    private String name;
    
    public Action(final ActionType type, final SimpleLocation loc, final UUID uuid, final String name, final long time) {
        this.type = type;
        this.loc = loc;
        this.uuid = uuid;
        this.name = name;
        this.time = time;
    }
    
    public ActionType getType() {
        return this.type;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public SimpleLocation getSimpleLocation() {
        return this.loc;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setTime(final long time) {
        this.time = time;
    }
    
    public void setSimpleLocation(final SimpleLocation loc) {
        this.loc = loc;
    }
    
    public void setUUID(final UUID uuid) {
        this.uuid = uuid;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setActionType(final ActionType type) {
        this.type = type;
    }
}
