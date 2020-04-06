// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.data.GuildData;
import java.util.UUID;
import pl.best241.ccguilds.backend.Redis;
import java.util.Calendar;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.cctools.commands.FakePlayerPercent;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccpreetytab.manager.PreetyTabManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TabManager
{
    private static final int maxPlayers = 300;
    
    public static void refreshTab(final Player player) {
        PreetyTabManager.setTabField(ChatColor.BLUE + ChatColor.BOLD.toString() + "CRAFT" + ChatColor.GRAY + ChatColor.BOLD.toString() + "CORE" + ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + ".PL", 1, 1, player);
        PreetyTabManager.setTabField(ChatColor.BLUE + ChatColor.BOLD.toString() + "TOP GRACZE", 0, 4, player);
        PreetyTabManager.setTabField(ChatColor.BLUE + ChatColor.BOLD.toString() + "STATYSTYKI", 1, 4, player);
        PreetyTabManager.setTabField(ChatColor.BLUE + ChatColor.BOLD.toString() + "TOP GILDIE", 2, 4, player);
        refreshPlayerNumber(player);
        refreshPlayerStats(player);
        refreshTime(player);
        refeshPlayerSector(player);
        refreshPlayersTop(player);
        refreshGuildTop(player);
        refreshPlayerGuild(player);
    }
    
    public static void refreshPlayerNumber(final Player player) {
        final int fakedPlayers = FakePlayerPercent.getFakePlayersOnline(CcSectorsAPI.getOnlinePlayersNumber());
        PreetyTabManager.setTabField(ChatColor.DARK_GREEN.toString() + "Online: " + fakedPlayers, 1, 2, player);
    }
    
    public static void refeshPlayerSector(final Player player) {
        PreetyTabManager.setTabField(ChatColor.DARK_GREEN.toString() + ChatColor.GRAY + "Sektor: " + ChatColor.BLUE + CcSectorsAPI.getSectorName(), 1, 11, player);
    }
    
    public static void refreshPlayerStats(final Player player) {
        final PlayerData playerData = DataManager.getPlayerData(player.getUniqueId());
        PreetyTabManager.setTabField(ChatColor.GRAY + "Ranking: " + ChatColor.BLUE + playerData.getRank(), 1, 6, player);
        PreetyTabManager.setTabField(ChatColor.GRAY + "Zabojstwa: " + ChatColor.BLUE + playerData.getKills(), 1, 7, player);
        PreetyTabManager.setTabField(ChatColor.GRAY + "Smierci: " + ChatColor.BLUE + playerData.getDeaths(), 1, 8, player);
        PreetyTabManager.setTabField(ChatColor.GRAY + "K/D Ratio: " + ChatColor.BLUE + playerData.getKdRatio(), 1, 9, player);
    }
    
    public static void refreshTime(final Player player) {
        final String hour = (String.valueOf(Calendar.getInstance().get(11)).length() == 1) ? ("0" + Calendar.getInstance().get(11)) : ("" + Calendar.getInstance().get(11));
        final String minute = (String.valueOf(Calendar.getInstance().get(12)).length() == 1) ? ("0" + Calendar.getInstance().get(12)) : ("" + Calendar.getInstance().get(12));
        final String second = (String.valueOf(Calendar.getInstance().get(13)).length() == 1) ? ("0" + Calendar.getInstance().get(13)) : ("" + Calendar.getInstance().get(13));
        final String time = hour + ":" + minute + ":" + second;
        PreetyTabManager.setTabField(ChatColor.GRAY + "Godzina: " + ChatColor.BLUE + time, 1, 10, player);
    }
    
    public static void refreshPlayersTop(final Player player) {
        int playerId = 0;
        for (int i = 1; i <= 15; ++i) {
            final int field = 4 + i;
            String place = i + ". ";
            if (place.length() != 4) {
                place += " ";
            }
            if (DataManager.getLastBestPlayers() == null) {
                Redis.loadBest();
            }
            if (DataManager.getLastBestPlayers().size() > playerId) {
                final UUID bestUUID = DataManager.getLastBestPlayers().get(playerId);
                if (bestUUID != null) {
                    final String nick = DataManager.getPlayerName(bestUUID);
                    System.out.println(bestUUID + " equals null!");
                    if (nick == null) {
                        --i;
                        break;
                    }
                    PreetyTabManager.setTabField(ChatColor.GRAY + place + ChatColor.DARK_GREEN + nick, 0, field, player);
                }
                else {
                    PreetyTabManager.setTabField(ChatColor.GRAY + place, 0, field, player);
                }
            }
            else {
                PreetyTabManager.setTabField(ChatColor.GRAY + place, 0, field, player);
            }
            ++playerId;
        }
    }
    
    public static void refreshGuildTop(final Player player) {
        int guildId = 0;
        for (int i = 1; i <= 15; ++i) {
            final int field = 4 + i;
            String place = i + ". ";
            if (place.length() != 4) {
                place += " ";
            }
            if (DataManager.getLastBestGuilds().size() > guildId) {
                final String shortCut = DataManager.getLastBestGuilds().get(guildId);
                final GuildData guildData = DataManager.getGuildData(shortCut.toLowerCase());
                if (guildData != null) {
                    PreetyTabManager.setTabField(ChatColor.GRAY + place + ChatColor.DARK_GREEN + guildData.getShortCut() + ChatColor.DARK_GRAY + "   [" + ChatColor.GRAY + guildData.getRank() + ChatColor.DARK_GRAY + "]", 2, field, player);
                }
                else if (shortCut != null) {
                    --i;
                }
                else {
                    PreetyTabManager.setTabField(ChatColor.GRAY + place, 2, field, player);
                }
            }
            else {
                PreetyTabManager.setTabField(ChatColor.GRAY + place, 2, field, player);
            }
            ++guildId;
        }
    }
    
    public static void refreshPlayerGuild(final Player player) {
        final PlayerData playerData = DataManager.getPlayerData(player.getUniqueId());
        if (playerData.getGuildData() != null) {
            final GuildData data = playerData.getGuildData();
            String cuboid;
            if (data.getCuboid() == null) {
                cuboid = "Brak";
            }
            else {
                cuboid = playerData.getGuildData().getCuboid().getRadius() * 2 + "x" + playerData.getGuildData().getCuboid().getRadius() * 2;
            }
            final String deputy = (playerData.getGuildData().getDeputy() != null) ? DataManager.getPlayerName(playerData.getGuildData().getDeputy()) : "Brak";
            PreetyTabManager.setTabField(ChatColor.BLUE + ChatColor.BOLD.toString() + "TWOJA GILDIA", 1, 13, player);
            PreetyTabManager.setTabField("", 1, 14, player);
            PreetyTabManager.setTabField(ChatColor.GRAY + "Lider: " + ChatColor.BLUE + DataManager.getPlayerName(playerData.getGuildData().getLeader()), 1, 15, player);
            PreetyTabManager.setTabField(ChatColor.GRAY + "Zastepca: " + ChatColor.BLUE + deputy, 1, 16, player);
            PreetyTabManager.setTabField(ChatColor.GRAY + "Cuboid: " + ChatColor.BLUE + cuboid, 1, 17, player);
            PreetyTabManager.setTabField(ChatColor.GRAY + "Ranking: " + ChatColor.BLUE + data.getRank(), 1, 18, player);
            PreetyTabManager.setTabField(ChatColor.GRAY + "Punkty bazy:" + ChatColor.BLUE + " " + data.getBasePoints(), 1, 19, player);
        }
        else {
            PreetyTabManager.setTabField("", 1, 13, player);
            PreetyTabManager.setTabField("", 1, 14, player);
            PreetyTabManager.setTabField("", 1, 15, player);
            PreetyTabManager.setTabField("", 1, 16, player);
            PreetyTabManager.setTabField("", 1, 17, player);
            PreetyTabManager.setTabField("", 1, 18, player);
            PreetyTabManager.setTabField("", 1, 19, player);
        }
    }
    
    public static void timeAndPlayerRefreshTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    TabManager.refreshPlayerNumber(online);
                    TabManager.refreshTime(online);
                }
            }
        }, 20L, 20L);
    }
    
    public static void bestRefreshTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    TabManager.refreshPlayersTop(online);
                    TabManager.refreshGuildTop(online);
                }
            }
        }, 1200L, 1200L);
    }
}
