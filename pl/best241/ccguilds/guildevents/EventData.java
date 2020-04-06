// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents;

public class EventData
{
    private final long time;
    private final EventType type;
    private final String shortCut;
    
    public EventData(final long time, final EventType type, final String shortCut) {
        this.time = time;
        this.type = type;
        this.shortCut = shortCut;
    }
    
    public long getTime() {
        return this.time;
    }
    
    public EventType getType() {
        return this.type;
    }
    
    public String getShortCut() {
        return this.shortCut;
    }
}
