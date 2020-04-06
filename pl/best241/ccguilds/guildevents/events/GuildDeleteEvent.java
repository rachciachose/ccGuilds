// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildDeleteEvent extends EventData
{
    private UUID byWhom;
    
    public GuildDeleteEvent(final long time, final String shortCut, final UUID byWhom) {
        super(time, EventType.GUILD_DELETE, shortCut);
        this.byWhom = byWhom;
    }
}
