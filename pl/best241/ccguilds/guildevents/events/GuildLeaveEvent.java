// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildLeaveEvent extends EventData
{
    private UUID who;
    
    public GuildLeaveEvent(final long time, final String shortCut, final UUID who) {
        super(time, EventType.GUILD_MEMBER_LEAVE, shortCut);
        this.who = who;
    }
}
