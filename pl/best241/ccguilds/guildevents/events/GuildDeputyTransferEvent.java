// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildDeputyTransferEvent extends EventData
{
    private UUID newDeputy;
    private UUID lastDeputy;
    
    public GuildDeputyTransferEvent(final long time, final String shortCut, final UUID newDeputy, final UUID lastDeputy) {
        super(time, EventType.GUILD_DEPUTY_TRANSFER, shortCut);
        this.newDeputy = newDeputy;
        this.lastDeputy = lastDeputy;
    }
}
