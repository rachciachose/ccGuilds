// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildWarStartEvent extends EventData
{
    private UUID uuid;
    
    public GuildWarStartEvent(final long time, final String shortCut) {
        super(time, EventType.GUILD_WAR_START, shortCut);
    }
}
