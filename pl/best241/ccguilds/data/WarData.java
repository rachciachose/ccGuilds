// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

import pl.best241.ccguilds.backend.Config;

public class WarData
{
    private final String challengingGuild;
    private final String targetGuild;
    private final long challangedTime;
    private int challengingKills;
    private int targetKills;
    
    public WarData(final String challangingGuild, final String targetGuild, final long challangedTime, final int challengingKills, final int targetKills) {
        this.challengingGuild = challangingGuild;
        this.targetGuild = targetGuild;
        this.challangedTime = challangedTime;
        this.challengingKills = challengingKills;
        this.targetKills = targetKills;
    }
    
    public boolean isAgressor(final String guild) {
        return this.challengingGuild.equalsIgnoreCase(guild);
    }
    
    public String getChallangedGuild() {
        return this.challengingGuild;
    }
    
    public String getTargetGuild() {
        return this.targetGuild;
    }
    
    public long getChallangedTime() {
        return this.challangedTime;
    }
    
    public long getStartTime() {
        return this.getChallangedTime() + Config.defaultWarStartDelay;
    }
    
    public boolean hasStarted() {
        return System.currentTimeMillis() >= this.getStartTime();
    }
    
    public int getTargetKills() {
        return this.targetKills;
    }
    
    public void setTargetKills(final int kills) {
        this.targetKills = kills;
    }
    
    public int getChallengingKills() {
        return this.challengingKills;
    }
    
    public void setChallengingKills(final int kills) {
        this.challengingKills = kills;
    }
}
