// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import pl.best241.ccguilds.data.GuildHomeData;
import pl.best241.ccguilds.visualeffects.VisualEffectType;
import java.util.HashSet;
import pl.best241.ccguilds.data.CuboidData;
import pl.best241.ccguilds.data.AllianceData;
import pl.best241.ccguilds.data.WarData;
import java.util.List;
import pl.best241.ccguilds.pubsub.PubSub;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.rdbplugin.JedisFactory;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

public class DataManager
{
    private static final Vector<UUID> dgChannels;
    private static final HashMap<String, UUID> nameUUID;
    private static final HashMap<UUID, String> uuidName;
    private static final ConcurrentHashMap<UUID, PlayerData> playerDatas;
    private static final ConcurrentHashMap<String, GuildData> guildDatas;
    
    public static void loadAllPlayerUUIDs() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final HashMap<String, String> rawNameUUID = Redis.getAllPlayerNameUUIDs(jedis);
        rawNameUUID.keySet().stream().forEach(key -> DataManager.nameUUID.put(key, UUID.fromString(rawNameUUID.get(key))));
        for (final Player online : Bukkit.getOnlinePlayers()) {
            updatePlayerUUID(online.getName(), getPlayerUUID(online));
        }
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static UUID getPlayerUUID(final Player player) {
        return player.getUniqueId();
    }
    
    public static void addToDgChannel(final UUID uuid) {
        DataManager.dgChannels.add(uuid);
    }
    
    public static boolean isInDgChannel(final UUID uuid) {
        return DataManager.dgChannels.contains(uuid);
    }
    
    public static void removeFromDgChannel(final UUID uuid) {
        DataManager.dgChannels.remove(uuid);
    }
    
    public static void updatePlayerUUID(final String name, final UUID uuid) {
        DataManager.nameUUID.put(name, uuid);
    }
    
    public static void loadAllPlayerNames() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final HashMap<String, String> rawUuidName = Redis.getAllPlayerUuidNames(jedis);
        rawUuidName.keySet().stream().forEach(key -> DataManager.uuidName.put(UUID.fromString(key), rawUuidName.get(key)));
        for (final Player online : Bukkit.getOnlinePlayers()) {
            updatePlayerName(getPlayerUUID(online), online.getName());
        }
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static String getPlayerName(final UUID uuid) {
        return DataManager.uuidName.get(uuid);
    }
    
    public static String getPlayerNameFromDb(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String hget = jedis.hget("uuid2nick", uuid.toString());
        JedisFactory.getInstance().returnJedis(jedis);
        return hget;
    }
    
    public static void updatePlayerName(final UUID uuid, final String name) {
        DataManager.uuidName.put(uuid, name);
    }
    
    public static void loadAllPlayerData() {
        final ArrayList<UUID> uuids = Redis.getAllPlayers();
        final Map<String, String> playerGuildNames = Redis.getAllPlayersGuildNames();
        final Map<String, String> playerGuildsJoinTime = Redis.getAllPlayersGuildJoinTime();
        final Map<String, String> playersGuildsLeaveTime = Redis.getAllPlayersGuildLeaveTime();
        final Map<String, Double> playerRanks = Redis.getAllPlayersRank();
        final HashMap<UUID, Integer> allPlayerKills = Redis.getAllPlayersKills();
        final HashMap<UUID, Integer> allPlayerDeaths = Redis.getAllPlayersDeaths();
        final HashMap<UUID, ArrayList<UUID>> allLastPlayersKillers = Redis.getAllLastPlayersKillers();
        final HashMap<UUID, ArrayList<UUID>> allLastPlayersVictims = Redis.getAllLastPlayersVictims();
        int i = 0;
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        for (final UUID uuid : uuids) {
            final String guildJoinTimeRaw = playerGuildsJoinTime.get(uuid.toString());
            final String playerGuildLeaveTime = playersGuildsLeaveTime.get(uuid.toString());
            final String guildName = playerGuildNames.get(uuid.toString());
            final long guildJoinTime = (guildJoinTimeRaw == null) ? -1L : Long.parseLong(guildJoinTimeRaw);
            final long guildLeaveTime = (playerGuildLeaveTime == null) ? -1L : Long.parseLong(playerGuildLeaveTime);
            final int rank = (playerRanks.get(uuid.toString()) != null) ? ((int)(Object)playerRanks.get(uuid.toString())) : 1000;
            Integer kills = allPlayerKills.get(uuid);
            Integer deaths = allPlayerDeaths.get(uuid);
            ArrayList<UUID> playerKills = allLastPlayersKillers.get(uuid);
            ArrayList<UUID> playerVictims = allLastPlayersVictims.get(uuid);
            if (kills == null) {
                kills = 0;
            }
            if (deaths == null) {
                deaths = 0;
            }
            if (playerKills == null) {
                playerKills = new ArrayList<UUID>();
            }
            if (playerVictims == null) {
                playerVictims = new ArrayList<UUID>();
            }
            final PlayerData data = new PlayerData(uuid, guildName, guildJoinTime, guildLeaveTime, rank, kills, deaths, playerKills, playerVictims);
            DataManager.playerDatas.put(uuid, data);
            ++i;
        }
        JedisFactory.getInstance().returnJedis(jedis);
        System.out.println("Loading " + i + " player data...");
    }
    
    public static void saveAllPlayerDataNeedingSave(final Jedis jedis) {
        final PlayerData data;
        DataManager.playerDatas.keySet().stream().forEach(uuid -> {
            data = DataManager.playerDatas.get(uuid);
            if (data.isNeedingSave()) {
                savePlayerData(data, jedis);
                DataManager.playerDatas.get(uuid).setNeedSave(false);
            }
        });
    }
    
    public static void saveAllPlayerDataNeedingSaveSync() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        saveAllPlayerDataNeedingSave(jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void saveAllPlayerDataNeedingSaveAsync() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        saveAllPlayerDataNeedingSave(jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void savePlayerData(final PlayerData data, final Jedis jedis) {
        Redis.savePlayerGuildName(data.getUUID(), data.getGuildName(), jedis);
        Redis.setPlayerGuildJoinTime(data.getUUID(), data.getGuildJoinTime(), jedis);
        Redis.setPlayerGuildLeaveTime(data.getUUID(), data.getGuildLeaveTime(), jedis);
        Redis.savePlayerRank(data.getUUID(), data.getRank(), jedis);
        Redis.savePlayerKills(data.getUUID(), data.getKills(), jedis);
        Redis.savePlayerDeaths(data.getUUID(), data.getDeaths(), jedis);
        Redis.setLastPlayerKillers(data.getUUID(), data.getPlayerKills());
        Redis.setLastPlayerVictims(data.getUUID(), data.getPlayerVictims());
        data.setNeedSave(false);
    }
    
    public static void saver() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                DataManager.saveAllPlayerDataNeedingSaveAsync();
            }
        }, 6000L, 6000L);
    }
    
    public static PlayerData getPlayerData(final UUID uuid) {
        return DataManager.playerDatas.get(uuid);
    }
    
    public static void updatePlayerData(final UUID uuid, final PlayerData data) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        savePlayerData(data, jedis);
        if (data.isNeedingSave()) {
            data.setNeedSave(false);
        }
        DataManager.playerDatas.put(uuid, data);
        PubSub.reloadPlayerData(uuid, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void reloadPlayerData(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String guildName = Redis.getPlayerGuildName(uuid, jedis);
        final long guildJoinTime = Redis.getPlayerGuildJoinTime(uuid, jedis);
        final long guildLeaveTime = Redis.getPlayerGuildLeaveTime(uuid, jedis);
        final int rank = Redis.getPlayerRank(uuid, jedis);
        final int kills = Redis.getPlayerKills(uuid, jedis);
        final int deaths = Redis.getPlayerDeaths(uuid, jedis);
        final ArrayList<UUID> playerKills = Redis.getLastPlayerKillers(uuid, jedis);
        final ArrayList<UUID> playerVictims = Redis.getLastPlayerVictims(uuid, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
        final PlayerData data = new PlayerData(uuid, guildName, guildJoinTime, guildLeaveTime, rank, kills, deaths, playerKills, playerVictims);
        DataManager.playerDatas.put(uuid, data);
    }
    
    public static void reloadPlayerDataAsync(final UUID uuid) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String guildName = Redis.getPlayerGuildName(uuid, jedis);
        final long guildJoinTime = Redis.getPlayerGuildJoinTime(uuid, jedis);
        final long guildLeaveTime = Redis.getPlayerGuildLeaveTime(uuid, jedis);
        final int rank = Redis.getPlayerRank(uuid, jedis);
        final int kills = Redis.getPlayerKills(uuid, jedis);
        final int deaths = Redis.getPlayerDeaths(uuid, jedis);
        final ArrayList<UUID> playerKills = Redis.getLastPlayerKillers(uuid, jedis);
        final ArrayList<UUID> playerVictims = Redis.getLastPlayerVictims(uuid, jedis);
        final PlayerData data = new PlayerData(uuid, guildName, guildJoinTime, guildLeaveTime, rank, kills, deaths, playerKills, playerVictims);
        JedisFactory.getInstance().returnJedis(jedis);
        DataManager.playerDatas.put(uuid, data);
    }
    
    public static void updatePlayerDataAsync(final UUID uuid, final PlayerData data) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        if (data.isNeedingSave()) {
            savePlayerData(data, jedis);
        }
        DataManager.playerDatas.put(uuid, data);
        PubSub.reloadPlayerData(uuid, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void loadAllGuildDatas() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final ArrayList<String> allGuildsInList = Redis.getAllGuildsInList(jedis);
        for (final String shortId : allGuildsInList) {
            final String shortCut = Redis.getGuildShortCut(shortId, jedis);
            final String fullName = Redis.getGuildFullName(shortId, jedis);
            final String description = Redis.getGuildDescription(shortId, jedis);
            final int rank = Redis.getGuildRank(shortId, jedis);
            final int basePoints = Redis.getGuildBasePoints(shortId, jedis);
            final long creationTime = Redis.getGuildCreationTime(shortId, jedis);
            final boolean friendlyFire = Redis.getGuildFriendlyFire(shortId, jedis);
            final boolean friendlyFireBow = Redis.getGuildFriendlyFireBow(shortId, jedis);
            final CuboidData cuboid = Redis.getGuildCuboid(shortId, jedis);
            final UUID leader = Redis.getGuildLeader(shortId, jedis);
            final UUID deputy = Redis.getGuildDeputy(shortId, jedis);
            final HashSet<UUID> members = Redis.getGuildMembers(shortId, jedis);
            final ArrayList<WarData> wars = Redis.getGuildWars(shortId, jedis);
            final ArrayList<AllianceData> alliances = Redis.getGuildAlliances(shortId, jedis);
            final long lastCuboidDestory = Redis.getGuildLastCuboidDestoryTime(shortId, jedis);
            final int kills = Redis.getGuildKills(shortId, jedis);
            final int deaths = Redis.getGuildDeaths(shortId, jedis);
            final ConcurrentHashMap<VisualEffectType, Long> effects = Redis.getGuildEffects(shortId, jedis);
            final GuildHomeData guildHomeData = Redis.getGuildHomeData(shortCut, jedis);
            final GuildData guild = new GuildData(shortCut, fullName, description, rank, basePoints, creationTime, friendlyFire, friendlyFireBow, cuboid, leader, deputy, members, wars, alliances, lastCuboidDestory, kills, deaths, effects, guildHomeData);
            DataManager.guildDatas.put(shortId, guild);
        }
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void saveAllGuildDataNeedingSave(final Jedis jedis) {
        final GuildData data;
        DataManager.guildDatas.keySet().stream().forEach(shortCut -> {
            data = DataManager.guildDatas.get(shortCut);
            if (data.isNeedingSave()) {
                saveGuildData(data, jedis);
                DataManager.guildDatas.get(shortCut).setNeedSave(false);
            }
        });
    }
    
    public static void saveAllGuildDataNeedingSaveSync() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        saveAllGuildDataNeedingSave(jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void saveAllGuildDataNeedingSaveAsync() {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        saveAllGuildDataNeedingSave(jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void saveGuildData(final GuildData data, final Jedis jedis) {
        Redis.addGuildToList(data.getShortCut(), jedis);
        Redis.setGuildShortCut(data.getShortCut(), jedis);
        Redis.setGuildFullName(data.getShortCut(), data.getFullName(), jedis);
        Redis.setGuildDescription(data.getShortCut(), data.getDescription(), jedis);
        Redis.setGuildRank(data.getShortCut(), data.getRank(), jedis);
        Redis.setGuildBasePoints(data.getShortCut(), data.getBasePoints(), jedis);
        Redis.setGuildCreationTime(data.getShortCut(), data.getCreationTime(), jedis);
        Redis.setGuildFriendlyFire(data.getShortCut(), data.getFriendlyFire(), jedis);
        Redis.setGuildFriendlyFireBow(data.getShortCut(), data.getFriendlyFireBow(), jedis);
        Redis.setGuildCuboid(data.getShortCut(), data.getCuboid(), jedis);
        Redis.setGuildLeader(data.getShortCut(), data.getLeader(), jedis);
        Redis.setGuildDeputy(data.getShortCut(), data.getDeputy(), jedis);
        Redis.setGuildMembers(data.getShortCut(), data.getMembers(), jedis);
        Redis.setGuildWars(data.getShortCut(), data.getWars(), jedis);
        Redis.setGuildAlliances(data.getShortCut(), data.getAlliances(), jedis);
        Redis.setGuildLastCuboidDestoryTime(data.getShortCut(), data.getLastCuboidDestoryTime(), jedis);
        Redis.setGuildKills(data.getShortCut(), data.getGuildKills(), jedis);
        Redis.setGuildDeaths(data.getShortCut(), data.getGuildDeaths(), jedis);
        Redis.setGuildEffects(data.getShortCut(), data.getEffects(), jedis);
        Redis.setGuildHomeData(data.getShortCut(), data.getGuildHomeData(), jedis);
    }
    
    public static void deleteGuildData(final String shortCut, final Jedis jedis) {
        Redis.removeGuildFromList(shortCut, jedis);
        Redis.saveGuildDeleteTime(shortCut, System.currentTimeMillis(), jedis);
        final GuildData data = DataManager.guildDatas.get(shortCut.toLowerCase());
        if (data == null || data.getAlliances() == null) {
            return;
        }
        for (final AllianceData ally : data.getAlliances()) {
            GuildData allyGuild;
            if (ally.getAcceptedGuild().equalsIgnoreCase(data.getShortCut())) {
                allyGuild = getGuildData(ally.getRequestorGuild());
            }
            else {
                allyGuild = getGuildData(ally.getAcceptedGuild());
            }
            if (allyGuild == null) {
                continue;
            }
            allyGuild.remAlly(getAllianceData(allyGuild, shortCut));
            updateGuildData(allyGuild.getShortCut().toLowerCase(), allyGuild);
        }
        for (final WarData war : data.getWars()) {
            GuildData warGuild;
            if (war.getChallangedGuild().equalsIgnoreCase(data.getShortCut())) {
                warGuild = getGuildData(war.getTargetGuild());
            }
            else {
                warGuild = getGuildData(war.getChallangedGuild());
            }
            if (warGuild == null) {
                continue;
            }
            warGuild.remWar(getWarData(warGuild, shortCut));
        }
        DataManager.guildDatas.remove(shortCut.toLowerCase());
    }
    
    public static AllianceData getAllianceData(final GuildData data, final String guild) {
        for (final AllianceData ally : data.getAlliances()) {
            if (ally.getAcceptedGuild().equalsIgnoreCase(guild) || ally.getRequestorGuild().equalsIgnoreCase(guild)) {
                return ally;
            }
        }
        return null;
    }
    
    public static WarData getWarData(final GuildData data, final String guild) {
        for (final WarData war : data.getWars()) {
            if (war.getChallangedGuild().equalsIgnoreCase(guild) || war.getTargetGuild().equalsIgnoreCase(guild)) {
                return war;
            }
        }
        return null;
    }
    
    public static void deleteGuildData(final String shortCut) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        deleteGuildData(shortCut, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void deleteGuildDataAsync(final String shortCut) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        deleteGuildData(shortCut, jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void guildSaver() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                DataManager.saveAllGuildDataNeedingSaveAsync();
            }
        }, 6000L, 6000L);
    }
    
    public static GuildData getGuildData(final String shortCut) {
        if (shortCut == null) {
            return null;
        }
        return DataManager.guildDatas.get(shortCut.toLowerCase());
    }
    
    public static void updateGuildData(final String shortCut, final GuildData data) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        saveGuildData(data, jedis);
        if (data.isNeedingSave()) {
            data.setNeedSave(false);
        }
        DataManager.guildDatas.put(shortCut.toLowerCase(), data);
        PubSub.reloadGuildData(shortCut.toLowerCase(), jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void updateGuildData(final GuildData data) {
        updateGuildData(data.getShortCut(), data);
    }
    
    public static void updateGuildDataWithoutSyncing(final String shortCut, final GuildData data) {
        DataManager.guildDatas.put(shortCut.toLowerCase(), data);
    }
    
    public static void updateGuildDataWithoutSyncing(final GuildData data) {
        updateGuildDataWithoutSyncing(data.getShortCut(), data);
    }
    
    public static void reloadGuildData(final String shortId) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        if (Redis.containsGuildInList(shortId, jedis)) {
            final String shortCut = Redis.getGuildShortCut(shortId, jedis);
            final String fullName = Redis.getGuildFullName(shortId, jedis);
            final String description = Redis.getGuildDescription(shortId, jedis);
            final int rank = Redis.getGuildRank(shortId, jedis);
            final int basePoints = Redis.getGuildBasePoints(shortId, jedis);
            final long creationTime = Redis.getGuildCreationTime(shortId, jedis);
            final boolean friendlyFire = Redis.getGuildFriendlyFire(shortId, jedis);
            final boolean friendlyFireBow = Redis.getGuildFriendlyFireBow(shortId, jedis);
            final CuboidData cuboid = Redis.getGuildCuboid(shortId, jedis);
            final UUID leader = Redis.getGuildLeader(shortId, jedis);
            final UUID deputy = Redis.getGuildDeputy(shortId, jedis);
            final HashSet<UUID> members = Redis.getGuildMembers(shortId, jedis);
            final ArrayList<WarData> wars = Redis.getGuildWars(shortId, jedis);
            final ArrayList<AllianceData> alliances = Redis.getGuildAlliances(shortId, jedis);
            final long lastCuboidDestoryTime = Redis.getGuildLastCuboidDestoryTime(shortId, jedis);
            final int kills = Redis.getGuildKills(shortId, jedis);
            final int deaths = Redis.getGuildDeaths(shortId, jedis);
            final ConcurrentHashMap<VisualEffectType, Long> effects = Redis.getGuildEffects(shortId, jedis);
            final GuildHomeData guildHomeData = Redis.getGuildHomeData(shortCut, jedis);
            final GuildData guild = new GuildData(shortCut, fullName, description, rank, basePoints, creationTime, friendlyFire, friendlyFireBow, cuboid, leader, deputy, members, wars, alliances, lastCuboidDestoryTime, kills, deaths, effects, guildHomeData);
            DataManager.guildDatas.put(shortId, guild);
        }
        else {
            DataManager.guildDatas.remove(shortId);
        }
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static void reloadGuildDataAsync(final String shortId) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        final String shortCut = Redis.getGuildShortCut(shortId, jedis);
        final String fullName = Redis.getGuildFullName(shortId, jedis);
        final String description = Redis.getGuildDescription(shortId, jedis);
        final int rank = Redis.getGuildRank(shortId, jedis);
        final int basePoints = Redis.getGuildBasePoints(shortId, jedis);
        final long creationTime = Redis.getGuildCreationTime(shortId, jedis);
        final boolean friendlyFire = Redis.getGuildFriendlyFire(shortId, jedis);
        final boolean friendlyFireBow = Redis.getGuildFriendlyFireBow(shortId, jedis);
        final CuboidData cuboid = Redis.getGuildCuboid(shortId, jedis);
        final UUID leader = Redis.getGuildLeader(shortId, jedis);
        final UUID deputy = Redis.getGuildDeputy(shortId, jedis);
        final HashSet<UUID> members = Redis.getGuildMembers(shortId, jedis);
        final ArrayList<WarData> wars = Redis.getGuildWars(shortId, jedis);
        final ArrayList<AllianceData> alliances = Redis.getGuildAlliances(shortId, jedis);
        final long lastCuboidDestoryTime = Redis.getGuildLastCuboidDestoryTime(shortId, jedis);
        final int kills = Redis.getGuildKills(shortId, jedis);
        final int deaths = Redis.getGuildDeaths(shortId, jedis);
        final ConcurrentHashMap<VisualEffectType, Long> effects = Redis.getGuildEffects(shortId, jedis);
        final GuildHomeData guildHomeData = Redis.getGuildHomeData(shortCut, jedis);
        final GuildData guild = new GuildData(shortCut, fullName, description, rank, basePoints, creationTime, friendlyFire, friendlyFireBow, cuboid, leader, deputy, members, wars, alliances, lastCuboidDestoryTime, kills, deaths, effects, guildHomeData);
        JedisFactory.getInstance().returnJedis(jedis);
        DataManager.guildDatas.put(shortId.toLowerCase(), guild);
    }
    
    public static void updateGuildDataAsync(final String shortId, final GuildData data) {
        final Jedis jedis = JedisFactory.getInstance().getJedis();
        if (data.isNeedingSave()) {
            saveGuildData(data, jedis);
        }
        DataManager.guildDatas.put(shortId.toLowerCase(), data);
        PubSub.reloadGuildData(shortId.toLowerCase(), jedis);
        JedisFactory.getInstance().returnJedis(jedis);
    }
    
    public static Map<String, GuildData> getAllGuilds() {
        return DataManager.guildDatas;
    }
    
    public static ArrayList<UUID> getLastBestPlayers() {
        return Redis.bestPlayers;
    }
    
    public static ArrayList<String> getLastBestGuilds() {
        return Redis.bestGuilds;
    }
    
    public static ArrayList<UUID> getLastBestPeopleKillers() {
        return Redis.bestPeopleKillers;
    }
    
    static {
        dgChannels = new Vector<UUID>();
        nameUUID = new HashMap<String, UUID>();
        uuidName = new HashMap<UUID, String>();
        playerDatas = new ConcurrentHashMap<UUID, PlayerData>();
        guildDatas = new ConcurrentHashMap<String, GuildData>();
    }
}
