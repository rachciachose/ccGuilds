// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents.events;

import pl.best241.ccguilds.guildevents.EventType;
import java.util.UUID;
import pl.best241.ccguilds.guildevents.EventData;

public class GuildLeaderTransferEvent extends EventData
{
    private UUID newLeader;
    private UUID lastLeader;
    
    public GuildLeaderTransferEvent(final long time, final String shortCut, final UUID newLeader, final UUID lastLeader) {
        super(time, EventType.GUILD_LEADER_TRANSFER, shortCut);
        this.newLeader = newLeader;
        this.lastLeader = lastLeader;
    }
}
