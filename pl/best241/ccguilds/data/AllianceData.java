// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

public class AllianceData
{
    private String requestedGuild;
    private String acceptGuild;
    private long time;
    
    public AllianceData(final String requestedGuild, final String acceptGuild, final long time) {
        this.requestedGuild = requestedGuild;
        this.acceptGuild = acceptGuild;
        this.time = time;
    }
    
    public AllianceData(final String requestedGuild, final String acceptGuild) {
        this(requestedGuild, acceptGuild, System.currentTimeMillis());
    }
    
    public String getRequestorGuild() {
        return this.requestedGuild;
    }
    
    public String getAcceptedGuild() {
        return this.acceptGuild;
    }
    
    public long getAcceptTime() {
        return this.time;
    }
}
