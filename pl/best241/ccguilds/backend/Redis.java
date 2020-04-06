// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.backend;

import pl.best241.ccguilds.guildevents.GuildEventManager;
import pl.best241.ccguilds.guildevents.EventData;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import java.util.concurrent.ConcurrentHashMap;
import pl.best241.ccguilds.visualeffects.VisualEffectType;
import pl.best241.ccguilds.data.GuildHomeData;
import java.util.Arrays;
import pl.best241.ccguilds.data.AllianceData;
import pl.best241.ccguilds.data.WarData;
import java.util.List;
import java.util.HashSet;
import org.bukkit.World;
import pl.best241.ccsectors.data.SectorType;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.data.CuboidData;
import java.util.Iterator;
import java.util.Collection;
import redis.clients.jedis.Tuple;
import java.util.Map;
import java.util.Set;
import pl.best241.rdbplugin.JedisFactory;
import java.util.HashMap;
import redis.clients.jedis.Jedis;
import java.util.UUID;
import java.util.ArrayList;

public class Redis
{
    public static ArrayList<UUID> bestPlayers;
    public static ArrayList<String> bestGuilds;
    public static ArrayList<UUID> bestPeopleKillers;
    
    public static HashMap<String, String> getAllPlayerNameUUIDs(final Jedis jedis) {
        return (HashMap<String, String>)jedis.hgetAll("nick2uuid");
    }
    
    public static HashMap<String, String> getAllPlayerUuidNames(final Jedis jedis) {
        return (HashMap<String, String>)jedis.hgetAll("uuid2nick");
    }
    
    public static UUID getLastPlayerUUID(final String name, final Jedis jedis) {
        final String value = jedis.hget("nick2uuid", name);
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }
    
    public static String getLastPlayerName(final UUID uuid, final Jedis jedis) {
        return jedis.hget("uuid2nick", uuid.toString());
    }
    
    public static ArrayList<UUID> getAllPlayers() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final ArrayList<UUID> uuids = new ArrayList<UUID>();
        final Set<String> members = (Set<String>)jedis.smembers("ccGuilds.players");
        members.stream().forEach(member -> uuids.add(UUID.fromString(member)));
        JedisFactory.getInstance().returnJedis(jedis);
        return uuids;
    }
    
    public static void addPlayer(final UUID uuid, final Jedis jedis) {
        jedis.sadd("ccGuilds.players", new String[] { uuid.toString() });
    }
    
    public static int getPlayerRank(final UUID uuid, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.playerRank", uuid.toString());
        if (value == null) {
            return 1000;
        }
        return (int)(Object)value;
    }
    
    public static Map<String, Double> getAllPlayersRank() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final ArrayList<Tuple> list = new ArrayList<Tuple>(jedis.zrangeWithScores("ccGuilds.playerRank", 0L, -1L));
        JedisFactory.getInstance().returnJedis(jedis);
        final HashMap<String, Double> playersRank = new HashMap<String, Double>();
        for (final Tuple tuple : list) {
            playersRank.put(tuple.getElement(), tuple.getScore());
        }
        return playersRank;
    }
    
    public static void savePlayerRank(final UUID uuid, final int rank, final Jedis jedis) {
        jedis.zadd("ccGuilds.playerRank", (double)rank, uuid.toString());
    }
    
    public static void savePlayerKills(final UUID uuid, final int kills, final Jedis jedis) {
        jedis.zadd("ccGuilds.playerKills", (double)kills, uuid.toString());
    }
    
    public static int getPlayerKills(final UUID uuid, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.playerKills", uuid.toString());
        if (value == null) {
            return 0;
        }
        return (int)(Object)value;
    }
    
    public static HashMap<UUID, Integer> getAllPlayersKills() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Set<Tuple> playerKills = (Set<Tuple>)jedis.zrangeWithScores("ccGuilds.playerKills", 0L, -1L);
        JedisFactory.getInstance().returnJedis(jedis);
        final Iterator<Tuple> playerKillsIterator = playerKills.iterator();
        final HashMap<UUID, Integer> hashPlayerKills = new HashMap<UUID, Integer>();
        while (playerKillsIterator.hasNext()) {
            final Tuple next = playerKillsIterator.next();
            hashPlayerKills.put(UUID.fromString(next.getElement()), (int)next.getScore());
        }
        return hashPlayerKills;
    }
    
    public static HashMap<UUID, Integer> getAllPlayersDeaths() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Set<Tuple> playerDeaths = (Set<Tuple>)jedis.zrangeWithScores("ccGuilds.playerDeaths", 0L, -1L);
        JedisFactory.getInstance().returnJedis(jedis);
        final Iterator<Tuple> playerDeathsIterator = playerDeaths.iterator();
        final HashMap<UUID, Integer> hashPlayerDeaths = new HashMap<UUID, Integer>();
        while (playerDeathsIterator.hasNext()) {
            final Tuple next = playerDeathsIterator.next();
            hashPlayerDeaths.put(UUID.fromString(next.getElement()), (int)next.getScore());
        }
        return hashPlayerDeaths;
    }
    
    public static void savePlayerDeaths(final UUID uuid, final int kills, final Jedis jedis) {
        jedis.zadd("ccGuilds.playerDeaths", (double)kills, uuid.toString());
    }
    
    public static int getPlayerDeaths(final UUID uuid, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.playerDeaths", uuid.toString());
        if (value == null) {
            return 0;
        }
        return (int)(Object)value;
    }
    
    public static String getPlayerGuildName(final UUID uuid, final Jedis jedis) {
        return jedis.hget("ccGuilds.playerGuild", uuid.toString());
    }
    
    public static Map<String, String> getAllPlayersGuildNames() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Map<String, String> maps = (Map<String, String>)jedis.hgetAll("ccGuilds.playerGuild");
        JedisFactory.getInstance().returnJedis(jedis);
        return maps;
    }
    
    public static void savePlayerGuildName(final UUID uuid, final String guildName, final Jedis jedis) {
        if (guildName == null) {
            jedis.hdel("ccGuilds.playerGuild", new String[] { uuid.toString() });
            return;
        }
        jedis.hset("ccGuilds.playerGuild", uuid.toString(), guildName);
    }
    
    public static long getPlayerGuildJoinTime(final UUID uuid, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.playerGuildJoinTime", uuid.toString());
        if (value == null) {
            return -1L;
        }
        return Long.valueOf(value);
    }
    
    public static Map<String, String> getAllPlayersGuildJoinTime() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Map<String, String> data = (Map<String, String>)jedis.hgetAll("ccGuilds.playerGuildJoinTime");
        JedisFactory.getInstance().returnJedis(jedis);
        return data;
    }
    
    public static void setPlayerGuildJoinTime(final UUID uuid, final long time, final Jedis jedis) {
        jedis.hset("ccGuilds.playerGuildJoinTime", uuid.toString(), Long.toString(time));
    }
    
    public static long getPlayerGuildLeaveTime(final UUID uuid, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.playerGuildLeaveTime", uuid.toString());
        if (value == null) {
            return -1L;
        }
        return Long.valueOf(value);
    }
    
    public static Map<String, String> getAllPlayersGuildLeaveTime() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Map<String, String> data = (Map<String, String>)jedis.hgetAll("ccGuilds.playerGuildLeaveTime");
        JedisFactory.getInstance().returnJedis(jedis);
        return data;
    }
    
    public static void setPlayerGuildLeaveTime(final UUID uuid, final long time, final Jedis jedis) {
        jedis.hset("ccGuilds.playerGuildLeaveTime", uuid.toString(), Long.toString(time));
    }
    
    public static void addGuildToList(final String guildShortcut, final Jedis jedis) {
        jedis.sadd("ccGuilds.guildList", new String[] { guildShortcut.toLowerCase() });
    }
    
    public static void removeGuildFromList(final String guildShortcut, final Jedis jedis) {
        jedis.srem("ccGuilds.guildList", new String[] { guildShortcut.toLowerCase() });
    }
    
    public static void saveGuildDeleteTime(final String guildShortcut, final long time, final Jedis jedis) {
        jedis.zadd("ccGuilds.guildDeleteTime", (double)time, guildShortcut);
    }
    
    public static Long getGuildDeleteTime(final String guildShortcut, final Jedis jedis) {
        final Double zscore = jedis.zscore("ccGuilds.guildDeleteTime", guildShortcut);
        if (zscore == null) {
            return null;
        }
        return (long)(Object)zscore;
    }
    
    public static boolean containsGuildInList(final String guildShortcut, final Jedis jedis) {
        return jedis.sismember("ccGuilds.guildList", guildShortcut.toLowerCase());
    }
    
    public static ArrayList<String> getAllGuildsInList(final Jedis jedis) {
        final Set<String> smembers = (Set<String>)jedis.smembers("ccGuilds.guildList");
        return new ArrayList<String>(smembers);
    }
    
    public static void setGuildShortCut(final String guildShortcut, final Jedis jedis) {
        jedis.hset("ccGuilds.guildShortNames", guildShortcut.toLowerCase(), guildShortcut);
    }
    
    public static void deleteGuildShortCut(final String guildShortcut, final Jedis jedis) {
        jedis.hdel("ccGuilds.guildShortNames", new String[] { guildShortcut.toLowerCase() });
    }
    
    public static String getGuildShortCut(final String idShortcut, final Jedis jedis) {
        return jedis.hget("ccGuilds.guildShortNames", idShortcut.toLowerCase());
    }
    
    public static void setGuildFullName(final String shortCut, final String fullName, final Jedis jedis) {
        jedis.hset("ccGuilds.guildFullName", shortCut.toLowerCase(), fullName);
    }
    
    public static void deleteGuildFullName(final String shortCut, final Jedis jedis) {
        jedis.hdel("ccGuilds.guildFullName", new String[] { shortCut.toLowerCase() });
    }
    
    public static String getGuildFullName(final String shortCut, final Jedis jedis) {
        return jedis.hget("ccGuilds.guildFullName", shortCut.toLowerCase());
    }
    
    public static void setGuildDescription(final String shortCut, final String description, final Jedis jedis) {
        if (description == null) {
            return;
        }
        jedis.hset("ccGuilds.guildDescription", shortCut.toLowerCase(), description);
    }
    
    public static void deleteGuildDescription(final String shortCut, final Jedis jedis) {
        jedis.hdel("ccGuilds.guildDescription", new String[] { shortCut.toLowerCase() });
    }
    
    public static String getGuildDescription(final String shortCut, final Jedis jedis) {
        return jedis.hget("ccGuilds.guildDescription", shortCut.toLowerCase());
    }
    
    public static void setGuildRank(final String shortCut, final int rank, final Jedis jedis) {
        jedis.zadd("ccGuilds.guildRank", (double)rank, shortCut.toLowerCase());
    }
    
    public static void deleteGuildRank(final String shortCut, final Jedis jedis) {
        jedis.zrem("ccGuilds.guildRank", new String[] { shortCut.toLowerCase() });
    }
    
    public static int getGuildRank(final String shortCut, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.guildRank", shortCut.toLowerCase());
        if (value == null) {
            return 1000;
        }
        return (int)(Object)value;
    }
    
    public static void setGuildFriendlyFire(final String shortCut, final boolean friendlyFire, final Jedis jedis) {
        jedis.hset("ccGuilds.guildFriendlyFire", shortCut.toLowerCase(), friendlyFire + "");
    }
    
    public static boolean getGuildFriendlyFire(final String shortCut, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.guildFriendlyFire", shortCut.toLowerCase());
        return value != null && Boolean.valueOf(value);
    }
    
    public static void setGuildFriendlyFireBow(final String shortCut, final boolean friendlyFire, final Jedis jedis) {
        jedis.hset("ccGuilds.guildFriendlyFireBow", shortCut.toLowerCase(), friendlyFire + "");
    }
    
    public static boolean getGuildFriendlyFireBow(final String shortCut, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.guildFriendlyFireBow", shortCut.toLowerCase());
        return value != null && Boolean.valueOf(value);
    }
    
    public static void setGuildCuboid(final String shortCut, final CuboidData cuboid, final Jedis jedis) {
        if (cuboid == null) {
            jedis.hdel("ccGuilds.guildCuboid", new String[] { shortCut.toLowerCase() });
            return;
        }
        final String parsed = cuboid.getSectorType() + ";" + cuboid.getRadius() + ";" + cuboid.getX() + ";" + cuboid.getZ() + ";" + cuboid.getBaseHeigth();
        jedis.hset("ccGuilds.guildCuboid", shortCut.toLowerCase(), parsed);
    }
    
    public static CuboidData getGuildCuboid(final String shortCut, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.guildCuboid", shortCut.toLowerCase());
        if (value == null) {
            return null;
        }
        final String[] args = value.split(";");
        final String worldName = args[0];
        final int radius = Integer.parseInt(args[1]);
        final int x = Integer.parseInt(args[2]);
        final int z = Integer.parseInt(args[3]);
        final int heigth = Integer.parseInt(args[4]);
        final World world = Bukkit.getWorld(worldName);
        return new CuboidData(SectorType.valueOf(worldName), radius, x, z, heigth);
    }
    
    public static void setGuildLeader(final String shortCut, final UUID leader, final Jedis jedis) {
        jedis.hset("ccGuilds.guildLeader", shortCut.toLowerCase(), leader.toString());
    }
    
    public static void setGuildDeputy(final String shortCut, final UUID deputy, final Jedis jedis) {
        if (deputy == null) {
            return;
        }
        jedis.hset("ccGuilds.guildDeputy", shortCut.toLowerCase(), deputy.toString());
    }
    
    public static void setGuildMembers(final String shortCut, final HashSet<UUID> members, final Jedis jedis) {
        String parsed = "";
        for (final UUID uuid : members) {
            parsed = ";" + uuid.toString() + parsed;
        }
        parsed = parsed.substring(1);
        jedis.hset("ccGuilds.guildMembers", shortCut.toLowerCase(), parsed);
    }
    
    public static void setGuildCreationTime(final String shortCut, final long creationTime, final Jedis jedis) {
        jedis.zadd("ccGuilds.guildCreationTime", (double)creationTime, shortCut.toLowerCase());
    }
    
    public static long getGuildCreationTime(final String shortCut, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.guildCreationTime", shortCut.toLowerCase());
        if (value == null) {
            return 0L;
        }
        return (long)(Object)value;
    }
    
    public static void setGuildBasePoints(final String shortCut, final int points, final Jedis jedis) {
        jedis.zadd("ccGuilds.guildBasePoints", (double)points, shortCut.toLowerCase());
    }
    
    public static void deleteGuildBasePoints(final String shortCut, final Jedis jedis) {
    }
    
    public static int getGuildBasePoints(final String shortCut, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.guildBasePoints", shortCut.toLowerCase());
        if (value == null) {
            return Config.defaultBasePoints;
        }
        return (int)(Object)value;
    }
    
    public static UUID getGuildLeader(final String shortCut, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.guildLeader", shortCut.toLowerCase());
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }
    
    public static UUID getGuildDeputy(final String shortCut, final Jedis jedis) {
        final String value = jedis.hget("ccGuilds.guildDeputy", shortCut.toLowerCase());
        if (value == null) {
            return null;
        }
        return UUID.fromString(value);
    }
    
    public static long getGuildLastCuboidDestoryTime(final String shortcut, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.lastCuboidDestoryTime", shortcut.toLowerCase());
        if (value == null) {
            return 0L;
        }
        return (long)(Object)value;
    }
    
    public static void setGuildLastCuboidDestoryTime(final String shortCut, final long time, final Jedis jedis) {
        jedis.zadd("ccGuilds.lastCuboidDestoryTime", (double)time, shortCut.toLowerCase());
    }
    
    public static HashSet<UUID> getGuildMembers(final String shortCut, final Jedis jedis) {
        final String parsed = jedis.hget("ccGuilds.guildMembers", shortCut.toLowerCase());
        if (parsed == null) {
            return new HashSet<UUID>();
        }
        final HashSet<UUID> members = new HashSet<UUID>();
        final String[] split;
        final String[] args = split = parsed.split(";");
        for (final String arg : split) {
            members.add(UUID.fromString(arg));
        }
        return members;
    }
    
    public static void setGuildWars(final String shortCut, final List<WarData> wars, final Jedis jedis) {
        if (wars == null) {
            return;
        }
        String parsedWars = "";
        for (final WarData war : wars) {
            final String parsedWar = war.getChallangedGuild() + ";" + war.getTargetGuild() + ";" + war.getChallangedTime() + ";" + war.getStartTime() + ";" + war.getChallengingKills() + ";" + war.getTargetKills();
            parsedWars = ":" + parsedWar + parsedWars;
        }
        if (!parsedWars.isEmpty()) {
            parsedWars = parsedWars.substring(1);
        }
        jedis.hset("ccGuilds.guildWars", shortCut.toLowerCase(), parsedWars);
    }
    
    public static ArrayList<WarData> getGuildWars(final String shortCut, final Jedis jedis) {
        final String parsedWars = jedis.hget("ccGuilds.guildWars", shortCut.toLowerCase());
        if (parsedWars == null) {
            return new ArrayList<WarData>();
        }
        final ArrayList<WarData> wars = new ArrayList<WarData>();
        for (final String war : parsedWars.split(":")) {
            final String[] splittedWar = war.split(";");
            if (splittedWar.length == 6) {
                final String challanged = splittedWar[0];
                final String target = splittedWar[1];
                final Long challangedTime = Long.parseLong(splittedWar[2]);
                final Long startTime = Long.parseLong(splittedWar[3]);
                final Integer challengingKills = Integer.parseInt(splittedWar[4]);
                final Integer targetKills = Integer.parseInt(splittedWar[5]);
                final WarData warData = new WarData(challanged, target, challangedTime, challengingKills, targetKills);
                wars.add(warData);
            }
        }
        return wars;
    }
    
    public static void setGuildAlliances(final String shortCut, List<AllianceData> alliances, final Jedis jedis) {
        if (alliances == null) {
            alliances = new ArrayList<AllianceData>();
        }
        String parsedAlliances = "";
        for (final AllianceData ally : alliances) {
            final String parsedAlliance = ally.getRequestorGuild() + ";" + ally.getAcceptedGuild() + ";" + ally.getAcceptTime();
            parsedAlliances = ":" + parsedAlliance + parsedAlliances;
        }
        if (!parsedAlliances.isEmpty()) {
            parsedAlliances = parsedAlliances.substring(1);
        }
        jedis.hset("ccGuilds.guildAlliances", shortCut.toLowerCase(), parsedAlliances);
    }
    
    public static ArrayList<AllianceData> getGuildAlliances(final String shortCut, final Jedis jedis) {
        final String parsedAlliances = jedis.hget("ccGuilds.guildAlliances", shortCut.toLowerCase());
        if (parsedAlliances == null) {
            return new ArrayList<AllianceData>();
        }
        System.out.println("alliances: " + parsedAlliances);
        final ArrayList<AllianceData> alliances = new ArrayList<AllianceData>();
        for (final String alliance : parsedAlliances.split(":")) {
            final String[] splittedAlly = alliance.split(";");
            if (splittedAlly.length == 3) {
                final String requestor = splittedAlly[0];
                final String accepted = splittedAlly[1];
                final Long acceptTime = Long.parseLong(splittedAlly[2]);
                final AllianceData allianceData = new AllianceData(requestor, accepted, acceptTime);
                alliances.add(allianceData);
            }
        }
        return alliances;
    }
    
    public static void addGuildAlliancesRequest(final String shortCut, final String requestedGuild, final Jedis jedis) {
        String guilds = jedis.hget("ccGuilds.guildAlliancesRequest", shortCut.toLowerCase());
        guilds = ((guilds == null) ? "" : guilds);
        guilds = guilds + ";" + requestedGuild;
        jedis.hset("ccGuilds.guildAlliancesRequest", shortCut.toLowerCase(), guilds);
    }
    
    public static void setGuildAlliancesRequest(final String shortCut, final ArrayList<String> guilds, final Jedis jedis) {
        String parsed = "";
        for (final String guild : guilds) {
            parsed = ";" + guild + parsed;
        }
        if (!parsed.isEmpty()) {
            parsed = parsed.substring(1);
        }
        jedis.hset("ccGuilds.guildAlliancesRequest", shortCut.toLowerCase(), parsed);
    }
    
    public static ArrayList<String> getGuildAlliancesRequest(final String shortCut, final Jedis jedis) {
        final String requests = jedis.hget("ccGuilds.guildAlliancesRequest", shortCut.toLowerCase());
        final ArrayList<String> guilds = new ArrayList<String>();
        if (requests == null) {
            return new ArrayList<String>();
        }
        guilds.addAll(Arrays.asList(requests.split(";")));
        return guilds;
    }
    
    public static void removeGuildAllianceRequest(final String shortCut, final String requestedGuild, final Jedis jedis) {
        final ArrayList<String> guilds = getGuildAlliancesRequest(shortCut, jedis);
        guilds.remove(requestedGuild);
        setGuildAlliancesRequest(shortCut, guilds, jedis);
    }
    
    public static int getGuildKills(final String shortCut, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.guildKills", shortCut.toLowerCase());
        if (value == null) {
            return 0;
        }
        return (int)(Object)value;
    }
    
    public static void setGuildKills(final String shortCut, final int kills, final Jedis jedis) {
        jedis.zadd("ccGuilds.guildKills", (double)kills, shortCut.toLowerCase());
    }
    
    public static int getGuildDeaths(final String shortCut, final Jedis jedis) {
        final Double value = jedis.zscore("ccGuilds.guildDeaths", shortCut.toLowerCase());
        if (value == null) {
            return 0;
        }
        return (int)(Object)value;
    }
    
    public static GuildHomeData getGuildHomeData(final String shortCut, final Jedis jedis) {
        final String hget = jedis.hget("ccGuilds.guildHomeData", shortCut.toLowerCase());
        if (hget == null) {
            return null;
        }
        final String[] parts = hget.split(";");
        final String world = parts[0];
        final int x = Integer.parseInt(parts[1]);
        final int y = Integer.parseInt(parts[2]);
        final int z = Integer.parseInt(parts[3]);
        final GuildHomeData guildHomeData = new GuildHomeData(SectorType.valueOf(world), x, y, z);
        return guildHomeData;
    }
    
    public static GuildHomeData getGuildHomeData(final String shortCut) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final GuildHomeData guildHomeData = getGuildHomeData(shortCut, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
        return guildHomeData;
    }
    
    public static void setGuildHomeData(final String shortCut, final GuildHomeData guildHomeData, final Jedis jedis) {
        if (guildHomeData == null) {
            jedis.hdel("ccGuilds.guildHomeData", new String[] { shortCut.toLowerCase() });
            return;
        }
        final String data = guildHomeData.getSectorType() + ";" + guildHomeData.getX() + ";" + guildHomeData.getY() + ";" + guildHomeData.getZ();
        jedis.hset("ccGuilds.guildHomeData", shortCut.toLowerCase(), data);
    }
    
    public static void setGuildHomeData(final String shortCut, final GuildHomeData guildHomeData) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        setGuildHomeData(shortCut, guildHomeData, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void setGuildDeaths(final String shortCut, final int deaths, final Jedis jedis) {
        jedis.zadd("ccGuilds.guildDeaths", (double)deaths, shortCut.toLowerCase());
    }
    
    public static void addGuildEffect(final String shortCut, final VisualEffectType effect, final long time, final Jedis jedis) {
        jedis.hset("ccGuilds.guildEffect." + shortCut.toLowerCase(), effect.toString(), time + "");
    }
    
    public static void setGuildEffects(final String shortcut, final ConcurrentHashMap<VisualEffectType, Long> effects, final Jedis jedis) {
        for (final VisualEffectType effect : effects.keySet()) {
            final long time = effects.get(effect);
            if (time >= System.currentTimeMillis()) {
                jedis.hset("ccGuilds.guildEffect." + shortcut.toLowerCase(), effect.toString(), time + "");
            }
        }
    }
    
    public static ConcurrentHashMap<VisualEffectType, Long> getGuildEffects(final String shortCut, final Jedis jedis) {
        final Map<String, String> effects = (Map<String, String>)jedis.hgetAll("ccGuilds.guildEffect." + shortCut.toLowerCase());
        final ConcurrentHashMap<VisualEffectType, Long> parsedEffects = new ConcurrentHashMap<VisualEffectType, Long>();
        for (final String rawEffect : effects.keySet()) {
            final VisualEffectType effect = VisualEffectType.valueOf(rawEffect);
            final long time = Long.parseLong(effects.get(rawEffect));
            parsedEffects.put(effect, time);
        }
        return parsedEffects;
    }
    
    public static ArrayList<UUID> getBestPlayers() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Set<String> zrevrange = (Set<String>)jedis.zrevrange("ccGuilds.playerRank", 0L, -1L);
        JedisFactory.getInstance().returnJedis(jedis);
        final ArrayList<UUID> uuids = new ArrayList<UUID>();
        for (final String rawUUID : zrevrange) {
            uuids.add(UUID.fromString(rawUUID));
        }
        return uuids;
    }
    
    public static ArrayList<String> getBestGuilds() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Set<String> zrevrange = (Set<String>)jedis.zrevrange("ccGuilds.guildRank", 0L, -1L);
        JedisFactory.getInstance().returnJedis(jedis);
        return new ArrayList<String>(zrevrange);
    }
    
    public static ArrayList<UUID> getBestPeopleKiller() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Set<String> zrevrange = (Set<String>)jedis.zrevrange("ccGuilds.playerKills", 0L, -1L);
        JedisFactory.getInstance().returnJedis(jedis);
        final ArrayList<UUID> uuids = new ArrayList<UUID>();
        for (final String rawUUID : zrevrange) {
            uuids.add(UUID.fromString(rawUUID));
        }
        return uuids;
    }
    
    public static void runBestTickers() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                Redis.bestPlayers = Redis.getBestPlayers();
                Redis.bestGuilds = Redis.getBestGuilds();
                Redis.bestPeopleKillers = Redis.getBestPeopleKiller();
            }
        }, 6000L, 6000L);
    }
    
    public static void loadBest() {
        Redis.bestPlayers = getBestPlayers();
        Redis.bestGuilds = getBestGuilds();
        Redis.bestPeopleKillers = getBestPeopleKiller();
    }
    
    public static void setLastPlayerKillers(final UUID playerUUID, final ArrayList<UUID> killers) {
        String key = "";
        for (final UUID uuid : killers) {
            key = key + uuid.toString() + ";";
        }
        if (!key.isEmpty()) {
            key = key.substring(0, key.length() - 1);
        }
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hset("ccGuilds.playerKillers", playerUUID.toString(), key);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void addLastPlayerKiller(final UUID playerUUID, final UUID killerUUID) {
        final ArrayList<UUID> killers = getLastPlayerKillers(playerUUID);
        killers.add(0, killerUUID);
        setLastPlayerKillers(playerUUID, killers);
    }
    
    public static ArrayList<UUID> getLastPlayerKillers(final UUID playerUUID) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final ArrayList<UUID> lastPlayerKillers = getLastPlayerKillers(playerUUID, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
        return lastPlayerKillers;
    }
    
    public static ArrayList<UUID> getLastPlayerKillers(final UUID playerUUID, final Jedis jedis) {
        final String hget = jedis.hget("ccGuilds.playerKillers", playerUUID.toString());
        if (hget == null) {
            return new ArrayList<UUID>();
        }
        final ArrayList<UUID> killers = new ArrayList<UUID>();
        for (final String rawUUID : hget.split(";")) {
            if (!rawUUID.isEmpty()) {
                killers.add(UUID.fromString(rawUUID));
            }
        }
        return killers;
    }
    
    public static HashMap<UUID, ArrayList<UUID>> getAllLastPlayersKillers() {
        final HashMap<UUID, ArrayList<UUID>> allLastPlayersKillers = new HashMap<UUID, ArrayList<UUID>>();
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Map<String, String> hgetAll = (Map<String, String>)jedis.hgetAll("ccGuilds.playerKillers");
        JedisFactory.getInstance().returnJedis(jedis);
        for (final String rawUuid : hgetAll.keySet()) {
            final UUID parsedUUID = UUID.fromString(rawUuid);
            final String value = hgetAll.get(rawUuid);
            final ArrayList<UUID> killers = new ArrayList<UUID>();
            for (final String rawKillerUUID : value.split(";")) {
                if (!rawKillerUUID.isEmpty()) {
                    killers.add(UUID.fromString(rawKillerUUID));
                }
            }
            allLastPlayersKillers.put(parsedUUID, killers);
        }
        return allLastPlayersKillers;
    }
    
    public static HashMap<UUID, ArrayList<UUID>> getAllLastPlayersVictims() {
        final HashMap<UUID, ArrayList<UUID>> allLastPlayersKillers = new HashMap<UUID, ArrayList<UUID>>();
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final Map<String, String> hgetAll = (Map<String, String>)jedis.hgetAll("ccGuilds.playerVictims");
        JedisFactory.getInstance().returnJedis(jedis);
        for (final String rawUuid : hgetAll.keySet()) {
            final UUID parsedUUID = UUID.fromString(rawUuid);
            final String value = hgetAll.get(rawUuid);
            final ArrayList<UUID> killers = new ArrayList<UUID>();
            for (final String rawKillerUUID : value.split(";")) {
                if (!rawKillerUUID.isEmpty()) {
                    killers.add(UUID.fromString(rawKillerUUID));
                }
            }
            allLastPlayersKillers.put(parsedUUID, killers);
        }
        return allLastPlayersKillers;
    }
    
    public static void setLastPlayerVictims(final UUID playerUUID, final ArrayList<UUID> killers) {
        String key = "";
        for (final UUID uuid : killers) {
            key = key + uuid.toString() + ";";
        }
        if (!key.isEmpty()) {
            key = key.substring(0, key.length() - 1);
        }
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.hset("ccGuilds.playerVictims", playerUUID.toString(), key);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void addLastPlayerVictim(final UUID playerUUID, final UUID killerUUID) {
        final ArrayList<UUID> killers = getLastPlayerKillers(playerUUID);
        killers.add(0, killerUUID);
        setLastPlayerKillers(playerUUID, killers);
    }
    
    public static ArrayList<UUID> getLastPlayerVictims(final UUID playerUUID) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final ArrayList<UUID> killers = getLastPlayerVictims(playerUUID, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
        return killers;
    }
    
    public static ArrayList<UUID> getLastPlayerVictims(final UUID playerUUID, final Jedis jedis) {
        final String hget = jedis.hget("ccGuilds.playerVictims", playerUUID.toString());
        if (hget == null) {
            return new ArrayList<UUID>();
        }
        final ArrayList<UUID> killers = new ArrayList<UUID>();
        for (final String rawUUID : hget.split(";")) {
            if (!rawUUID.isEmpty()) {
                killers.add(UUID.fromString(rawUUID));
            }
        }
        return killers;
    }
    
    public static void addGuildEvent(final EventData data) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        jedis.sadd("ccGuilds.guildEvents." + data.getShortCut().toLowerCase(), new String[] { GuildEventManager.parseToString(data) });
        JedisFactory.getInstance().returnJedis(jedis);
    }
}
