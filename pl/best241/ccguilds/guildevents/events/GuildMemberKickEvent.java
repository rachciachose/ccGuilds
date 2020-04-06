// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildMemberKickEvent extends EventData
{
    private UUID who;
    private UUID byWhom;
    
    public GuildMemberKickEvent(final long time, final String shortCut, final UUID who, final UUID byWhom) {
        super(time, EventType.GUILD_MEMBER_KICK, shortCut);
        this.byWhom = byWhom;
        this.who = who;
    }
}
