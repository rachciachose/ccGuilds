// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildCreateEvent extends EventData
{
    private UUID byWhom;
    
    public GuildCreateEvent(final long time, final String guildShortcut, final UUID byWhom) {
        super(time, EventType.GUILD_CREATE, guildShortcut);
        this.byWhom = byWhom;
    }
    
    public UUID getByWhom() {
        return this.byWhom;
    }
    
    public void setByWhom(final UUID byWhom) {
        this.byWhom = byWhom;
    }
}
