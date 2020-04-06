// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildCuboidEnlargeEvent extends EventData
{
    private UUID byWhom;
    private int newRadius;
    
    public GuildCuboidEnlargeEvent(final long time, final String shortCut, final int newRadius, final UUID byWhom) {
        super(time, EventType.GUILD_CUBOID_ENLARGE, shortCut);
        this.newRadius = newRadius;
        this.byWhom = byWhom;
    }
}
