// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

import java.util.Iterator;
import org.bukkit.Location;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccguilds.commands.CmdZaloz;
import pl.best241.ccguilds.backend.Config;
import java.util.Collections;
import java.util.ArrayList;
import pl.best241.ccguilds.visualeffects.VisualEffectType;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.HashSet;
import java.util.UUID;

public class GuildData
{
    private final String shortCut;
    private final String fullName;
    private String description;
    private int rank;
    private final long creationTime;
    private boolean friendlyFire;
    private boolean friendlyFireBow;
    private CuboidData cuboid;
    private UUID leader;
    private UUID deputy;
    private HashSet<UUID> members;
    private boolean needSave;
    private int deaths;
    private int kills;
    private long lastTntExplode;
    private int basePoints;
    private long lastCuboidDestoryTime;
    private List<WarData> wars;
    private List<AllianceData> alliances;
    private ConcurrentHashMap<VisualEffectType, Long> effects;
    private GuildHomeData guildHome;
    
    public GuildData(final String shortCut, final String fullName, final String description, final int rank, final int basePoints, final long creationTime, final boolean friendlyFire, final boolean friendlyFireBow, final CuboidData cub, final UUID leaderUUID, final UUID deputyUUID, final HashSet<UUID> members, List<WarData> wars, List<AllianceData> alliances, final long lastCuboidDestoryTime, final int kills, final int deaths, final ConcurrentHashMap<VisualEffectType, Long> effects, final GuildHomeData guildHome) {
        this.shortCut = shortCut;
        this.fullName = fullName;
        this.description = description;
        this.rank = rank;
        this.creationTime = creationTime;
        this.friendlyFire = friendlyFire;
        this.friendlyFireBow = friendlyFireBow;
        this.cuboid = cub;
        this.leader = leaderUUID;
        this.deputy = deputyUUID;
        this.members = members;
        this.basePoints = basePoints;
        if (wars == null) {
            wars = new ArrayList<WarData>();
        }
        if (alliances == null) {
            alliances = new ArrayList<AllianceData>();
        }
        this.wars = Collections.synchronizedList(wars);
        this.alliances = Collections.synchronizedList(alliances);
        this.lastCuboidDestoryTime = lastCuboidDestoryTime;
        this.kills = kills;
        this.deaths = deaths;
        this.effects = effects;
        this.guildHome = guildHome;
    }
    
    public int getGuildKills() {
        return this.kills;
    }
    
    public void rebuildBaseBlock() {
        try {
            this.cuboid.getBaseBlock().getBlock().setType(Config.baseBlock);
        }
        catch (Exception ex) {
            Logger.getLogger(CmdZaloz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ConcurrentHashMap<VisualEffectType, Long> getEffects() {
        return this.effects;
    }
    
    public void setGuildKills(final int kills) {
        this.kills = kills;
    }
    
    public int getGuildDeaths() {
        return this.deaths;
    }
    
    public void setGuildDeaths(final int deaths) {
        this.deaths = deaths;
    }
    
    public long getLastCuboidDestoryTime() {
        return this.lastCuboidDestoryTime;
    }
    
    public void setLastCuboidDestoryTime(final long newTime) {
        this.lastCuboidDestoryTime = newTime;
    }
    
    public int getBasePoints() {
        return this.basePoints;
    }
    
    public void setBasePoints(final int points) {
        this.basePoints = points;
    }
    
    public String getShortCut() {
        return this.shortCut;
    }
    
    public String getFullName() {
        return this.fullName;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public long getCreationTime() {
        return this.creationTime;
    }
    
    public boolean getFriendlyFire() {
        return this.friendlyFire;
    }
    
    public void setFriendlyFire(final boolean ff) {
        this.friendlyFire = ff;
    }
    
    public Location getSpawnLocation() {
        throw new UnsupportedOperationException("Not implemented");
    }
    
    public UUID getLeader() {
        return this.leader;
    }
    
    public void setLeader(final UUID uuid) {
        this.leader = uuid;
    }
    
    public UUID getDeputy() {
        return this.deputy;
    }
    
    public void setDeputy(final UUID uuid) {
        this.deputy = uuid;
    }
    
    public HashSet<UUID> getMembers() {
        return this.members;
    }
    
    public void setMembers(final HashSet<UUID> members) {
        this.members = members;
    }
    
    public CuboidData getCuboid() {
        return this.cuboid;
    }
    
    public void setCuboid(final CuboidData cuboid) {
        this.cuboid = cuboid;
    }
    
    public boolean isNeedingSave() {
        return this.needSave;
    }
    
    public void setNeedSave(final boolean needSave) {
        this.needSave = needSave;
    }
    
    public boolean getFriendlyFireBow() {
        return this.friendlyFireBow;
    }
    
    public void setFriendlyFireBow(final boolean value) {
        this.friendlyFireBow = value;
    }
    
    public List<WarData> getWars() {
        if (this.wars == null) {
            return new ArrayList<WarData>();
        }
        return this.wars;
    }
    
    public void setWars(final List<WarData> wars) {
        this.wars = Collections.synchronizedList(wars);
    }
    
    public List<AllianceData> getAlliances() {
        if (this.alliances == null) {
            return new ArrayList<AllianceData>();
        }
        return this.alliances;
    }
    
    public void setAlliances(final List<AllianceData> alliances) {
        this.alliances = Collections.synchronizedList(alliances);
    }
    
    public void addWar(final WarData war) {
        this.remWar(war);
        final List<WarData> warList = this.getWars();
        warList.add(war);
        this.setWars(warList);
    }
    
    public void remWar(final WarData war) {
        final List<WarData> warList = this.getWars();
        warList.remove(war);
        this.setWars(warList);
    }
    
    public void addAlly(final AllianceData ally) {
        final List<AllianceData> allys = this.getAlliances();
        allys.add(ally);
        this.setAlliances(allys);
    }
    
    public void remAlly(final AllianceData ally) {
        final List<AllianceData> allys = this.getAlliances();
        allys.remove(ally);
        this.setAlliances(allys);
    }
    
    public boolean isLeader(final UUID uuid) {
        return this.leader.toString().equalsIgnoreCase(uuid.toString());
    }
    
    public boolean isDeputy(final UUID uuid) {
        return this.deputy != null && this.deputy.toString().equalsIgnoreCase(uuid.toString());
    }
    
    public void setTntExplosionTime(final long time) {
        this.lastTntExplode = time;
    }
    
    public long getLastTntExplosionTime() {
        return this.lastTntExplode;
    }
    
    public boolean equals(final GuildData other) {
        return other != null && other.getShortCut().equalsIgnoreCase(this.shortCut);
    }
    
    public boolean isInAlliance(final GuildData other) {
        for (final AllianceData ally : this.alliances) {
            String allyGuild = null;
            if (ally.getAcceptedGuild().equalsIgnoreCase(this.shortCut)) {
                allyGuild = ally.getRequestorGuild();
            }
            else if (ally.getRequestorGuild().equalsIgnoreCase(this.shortCut)) {
                allyGuild = ally.getAcceptedGuild();
            }
            if (allyGuild == null) {
                continue;
            }
            if (allyGuild.equalsIgnoreCase(other.getShortCut())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInWar(final GuildData other) {
        if (other == null) {
            return false;
        }
        for (final WarData war : this.wars) {
            long time = 0L;
            String warGuild = null;
            if (war.getTargetGuild().equalsIgnoreCase(this.shortCut)) {
                warGuild = war.getChallangedGuild();
                time = war.getChallangedTime();
            }
            else if (war.getChallangedGuild().equalsIgnoreCase(this.shortCut)) {
                warGuild = war.getTargetGuild();
            }
            if (warGuild == null) {
                continue;
            }
            if (warGuild.equalsIgnoreCase(other.getShortCut()) && time + Config.defaultWarStartDelay <= System.currentTimeMillis()) {
                return true;
            }
        }
        return false;
    }
    
    public void removeWar(final String shortCut) {
        WarData guildWar = null;
        for (final WarData war : this.wars) {
            String warGuild = null;
            if (war.getTargetGuild().equalsIgnoreCase(this.shortCut)) {
                warGuild = war.getChallangedGuild();
            }
            else if (war.getChallangedGuild().equalsIgnoreCase(this.shortCut)) {
                warGuild = war.getTargetGuild();
            }
            if (warGuild == null) {
                continue;
            }
            if (!warGuild.equalsIgnoreCase(shortCut)) {
                continue;
            }
            guildWar = war;
        }
        if (guildWar != null) {
            this.remWar(guildWar);
        }
    }
    
    public void removeAlliance(final String shortCut) {
        AllianceData guildAlly = null;
        for (final AllianceData ally : this.alliances) {
            String allyGuild = null;
            if (ally.getAcceptedGuild().equalsIgnoreCase(this.shortCut)) {
                allyGuild = ally.getRequestorGuild();
            }
            else if (ally.getRequestorGuild().equalsIgnoreCase(this.shortCut)) {
                allyGuild = ally.getAcceptedGuild();
            }
            if (allyGuild == null) {
                continue;
            }
            if (!allyGuild.equalsIgnoreCase(shortCut)) {
                continue;
            }
            guildAlly = ally;
        }
        if (guildAlly != null) {
            this.remAlly(guildAlly);
        }
    }
    
    public WarData getWar(final String shortCut) {
        for (final WarData war : this.wars) {
            String warGuild = null;
            if (war.getTargetGuild().equalsIgnoreCase(this.shortCut)) {
                warGuild = war.getChallangedGuild();
            }
            else if (war.getChallangedGuild().equalsIgnoreCase(this.shortCut)) {
                warGuild = war.getTargetGuild();
            }
            if (warGuild == null) {
                continue;
            }
            if (warGuild.equalsIgnoreCase(shortCut)) {
                return war;
            }
        }
        return null;
    }
    
    public GuildHomeData getGuildHomeData() {
        return this.guildHome;
    }
    
    public void setGuildHomeData(final GuildHomeData data) {
        this.guildHome = data;
    }
    
    public boolean isMember(final UUID uuid) {
        for (final UUID memberUUID : this.getMembers()) {
            if (uuid.equals(memberUUID)) {
                return true;
            }
        }
        return false;
    }
}
