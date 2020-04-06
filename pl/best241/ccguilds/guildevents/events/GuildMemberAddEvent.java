// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildMemberAddEvent extends EventData
{
    private UUID whoAdded;
    private UUID byWhom;
    
    public GuildMemberAddEvent(final long time, final String shortCut, final UUID whoAdded, final UUID byWhom) {
        super(time, EventType.GUILD_MEMBER_ADD, shortCut);
        this.whoAdded = whoAdded;
        this.byWhom = byWhom;
    }
}
