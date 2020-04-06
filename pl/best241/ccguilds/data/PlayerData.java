// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

import pl.best241.ccguilds.manager.DataManager;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerData
{
    private final UUID uuid;
    private String guildName;
    private long guildJoinTime;
    private long guildLeaveTime;
    private int rank;
    private int kills;
    private int deaths;
    private boolean needSave;
    private final ArrayList<UUID> playerKills;
    private final ArrayList<UUID> playerDeaths;
    
    public PlayerData(final UUID uuid, final String guildName, final long guildJoinTime, final long guildLeaveTime, final int rank, final int kills, final int deaths, final ArrayList<UUID> playerKills, final ArrayList<UUID> playerDeaths) {
        this.uuid = uuid;
        this.guildName = guildName;
        this.guildJoinTime = guildJoinTime;
        this.guildLeaveTime = guildLeaveTime;
        this.rank = rank;
        this.kills = kills;
        this.deaths = deaths;
        this.playerKills = playerKills;
        this.playerDeaths = playerDeaths;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getGuildName() {
        return this.guildName;
    }
    
    public void setGuildName(final String guildName) {
        this.guildName = guildName;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setKills(final int kills) {
        this.kills = kills;
    }
    
    public int getDeaths() {
        return this.deaths;
    }
    
    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }
    
    public boolean isNeedingSave() {
        return this.needSave;
    }
    
    public void setNeedSave(final boolean b) {
        this.needSave = b;
    }
    
    public long getGuildJoinTime() {
        return this.guildJoinTime;
    }
    
    public void setGuildJoinTime(final long guildJoinTime) {
        this.guildJoinTime = guildJoinTime;
    }
    
    public long getGuildLeaveTime() {
        return this.guildLeaveTime;
    }
    
    public void setGuildLeaveTime(final long guildLeaveTime) {
        this.guildLeaveTime = guildLeaveTime;
    }
    
    public GuildData getGuildData() {
        return DataManager.getGuildData(this.guildName);
    }
    
    public String getKdRatio() {
        if (this.deaths == 0) {
            return "-";
        }
        if (this.kills == 0) {
            return "-";
        }
        double kdRatio = this.kills / this.deaths;
        kdRatio = Math.round(kdRatio * 100.0);
        kdRatio /= 100.0;
        return kdRatio + "";
    }
    
    public ArrayList<UUID> getPlayerKills() {
        return this.playerKills;
    }
    
    public void addPlayerKill(final UUID uuid) {
        this.playerKills.add(0, uuid);
    }
    
    public ArrayList<UUID> getPlayerVictims() {
        return this.playerDeaths;
    }
    
    public void addPlayerVictim(final UUID uuid) {
        this.playerDeaths.add(0, uuid);
    }
}
