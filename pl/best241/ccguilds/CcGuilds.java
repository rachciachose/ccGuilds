// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds;

import java.util.ArrayList;
import java.util.List;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.logblock.listeners.LogBlockCheckListener;
import pl.best241.ccguilds.commands.CmdCzlonek;
import pl.best241.ccguilds.commands.CmdUstawteren;
import pl.best241.ccguilds.commands.CmdUstawdom;
import pl.best241.ccguilds.commands.CmdEfekty;
import pl.best241.ccguilds.commands.CmdOpis;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccsectors.api.CcSectorsAPI;
import org.apache.commons.lang.StringUtils;
import pl.best241.ccguilds.commands.CmdDs;
import pl.best241.ccguilds.commands.CmdDg;
import pl.best241.ccguilds.commands.CmdGildia;
import pl.best241.ccguilds.commands.CmdGracz;
import pl.best241.ccguilds.commands.CmdFriendlyFireBow;
import pl.best241.ccguilds.commands.CmdFriendlyFire;
import pl.best241.ccguilds.commands.CmdPokoj;
import pl.best241.ccguilds.commands.CmdWojna;
import pl.best241.ccguilds.commands.CmdSojusz;
import pl.best241.ccguilds.commands.CmdPowieksz;
import pl.best241.ccguilds.commands.CmdDom;
import pl.best241.ccguilds.commands.CmdZastepca;
import pl.best241.ccguilds.commands.CmdLider;
import pl.best241.ccguilds.commands.CmdOpusc;
import pl.best241.ccguilds.commands.CmdWyrzuc;
import pl.best241.ccguilds.commands.CmdDolacz;
import pl.best241.ccguilds.commands.CmdZapros;
import pl.best241.ccguilds.commands.CmdRozwiaz;
import pl.best241.ccguilds.commands.CmdZaloz;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Iterator;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.logblock.LogBlock;
import pl.best241.ccguilds.manager.ConfigManager;
import pl.best241.ccguilds.effects.EffectManager;
import pl.best241.ccguilds.timers.AntiLogoutTimer;
import pl.best241.ccguilds.visualeffects.VisualEffectTicker;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccguilds.manager.WarManager;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.pubsub.PubSub;
import java.util.Random;
import pl.best241.ccguilds.manager.GuildShowManager;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.listeners.PistonMoveListener;
import pl.best241.ccguilds.listeners.InventoryClickListener;
import pl.best241.ccguilds.listeners.PubSubRecieveMessageListener;
import pl.best241.ccguilds.commands.CmdItemy;
import pl.best241.ccguilds.listeners.PlayerBucketEmptyListener;
import pl.best241.ccguilds.listeners.PlayerBucketFillListener;
import pl.best241.ccguilds.listeners.PlayerScoreListener;
import pl.best241.ccguilds.listeners.PlayerQuitListener;
import pl.best241.ccguilds.listeners.PlayerPrefixSuffixListener;
import pl.best241.ccguilds.listeners.PlayerKickListener;
import pl.best241.ccguilds.listeners.PlayerJoinListener;
import pl.best241.ccguilds.listeners.PlayerDeathListener;
import pl.best241.ccguilds.listeners.EntityExplodeListener;
import pl.best241.ccguilds.listeners.EntityDamageByEntityListener;
import pl.best241.ccguilds.listeners.BlockPlaceListener;
import org.bukkit.event.Listener;
import pl.best241.ccguilds.listeners.BlockBreakListener;
import pl.best241.ccguilds.messages.MessagesData;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.messages.MessagesManager;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CcGuilds extends JavaPlugin
{
    private static CcGuilds plugin;
    private static int key;
    
    public void onEnable() {
        if (!this.getServer().getPluginManager().isPluginEnabled("rdbPlugin")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "rdbPlugin not enabled! ccGuilds not started!");
            this.setEnabled(false);
        }
        MessagesManager.loadMessages((Plugin)(CcGuilds.plugin = this));
        MessagesData.loadMessages((Plugin)CcGuilds.plugin);
        this.getServer().getPluginManager().registerEvents((Listener)new BlockBreakListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new BlockPlaceListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new EntityDamageByEntityListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new EntityExplodeListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerDeathListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerJoinListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerKickListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerPrefixSuffixListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerQuitListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerScoreListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerBucketFillListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PlayerBucketEmptyListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CmdItemy(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PubSubRecieveMessageListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new InventoryClickListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PistonMoveListener(), (Plugin)this);
        DataManager.loadAllPlayerData();
        DataManager.loadAllPlayerNames();
        DataManager.loadAllPlayerUUIDs();
        DataManager.loadAllGuildDatas();
        GuildShowManager.startTicker();
        CcGuilds.key = new Random().nextInt();
        PubSub.listen();
        PubSub.keepAliveTicker();
        Redis.loadBest();
        Redis.runBestTickers();
        WarManager.startTicker();
        TabManager.bestRefreshTimer();
        TabManager.timeAndPlayerRefreshTimer();
        VisualEffectTicker.startTicker();
        AntiLogoutTimer.antiLogoutTimerRun();
        EffectManager.runEffectTicker();
        ConfigManager.load(this);
        LogBlock.enableAll();
        for (final GuildData guildData : DataManager.getAllGuilds().values()) {
            if (guildData.getCuboid() != null) {
                try {
                    if (guildData.getCuboid().getBaseBlock().getBlock().getType() == Config.baseBlock) {
                        continue;
                    }
                    guildData.getCuboid().getBaseBlock().getBlock().setType(Config.baseBlock);
                    System.out.println("Guild " + guildData.getShortCut() + " don't have its base block! Setting!");
                }
                catch (Exception ex) {}
            }
        }
    }
    
    public void onDisable() {
        DataManager.saveAllPlayerDataNeedingSaveSync();
        DataManager.saveAllGuildDataNeedingSaveSync();
        PubSub.stopListen();
    }
    
    public static int getKey() {
        return CcGuilds.key;
    }
    
    public boolean onCommand(final CommandSender sender, final Command cmd, final String lable, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("zaloz")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length < 2) {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /zaloz SKROT Pelna nazwa gildii");
                    return false;
                }
                final String shortCut = args[0];
                String fullName = "";
                for (int i = 1; i < args.length; ++i) {
                    fullName = fullName + " " + args[i];
                }
                fullName = fullName.substring(1);
                CmdZaloz.zaloz(player, shortCut, fullName);
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("rozwiaz")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdRozwiaz.rozwiaz(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /rozwiaz");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("zapros")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length != 1) {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /zapros nick");
                }
                else {
                    final String nick = args[0];
                    CmdZapros.zapros(player, nick);
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("dolacz")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length != 1) {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /dolacz gildia");
                }
                else {
                    final String guild = args[0];
                    CmdDolacz.dolacz(player, guild);
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("wyrzuc")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length != 1) {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /wyrzuc gildia");
                }
                else {
                    final String nick = args[0];
                    CmdWyrzuc.wyrzuc(player, nick);
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("opusc")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdOpusc.opusc(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /opusc");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("lider")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 1) {
                    final String newLeader = args[0];
                    CmdLider.lider(player, newLeader);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /lider nick");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("zastepca")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 1) {
                    final String newDeputy = args[0];
                    CmdZastepca.zastepca(player, newDeputy);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /zastepca nick");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("dom")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdDom.baza(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /dom");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("powieksz")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdPowieksz.powieksz(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /powieksz");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("sojusz")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 1) {
                    final String requestGuild = args[0];
                    CmdSojusz.sojusz(player, requestGuild);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /sojusz gildia");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("wojna")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 1) {
                    CmdWojna.wojna(player, args[0]);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /wojna gildia");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("pokoj")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 1) {
                    CmdPokoj.pokoj(player, args[0]);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /pokoj gildia");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("friendlyfire")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
                return false;
            }
            final Player player = (Player)sender;
            if (args.length == 0) {
                CmdFriendlyFire.friendlyfire(player);
            }
            else {
                player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /friendlyfire");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("friendlyfirebow")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
                return false;
            }
            final Player player = (Player)sender;
            if (args.length == 0) {
                CmdFriendlyFireBow.friendlyfirebow(player);
            }
            else {
                player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /friendlyfirebow");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("gracz")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
                return false;
            }
            if (args.length == 0 || args.length == 1) {
                String player2;
                if (args.length == 0) {
                    player2 = sender.getName();
                }
                else {
                    player2 = args[0];
                }
                CmdGracz.cmdGracz((Player)sender, player2);
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /gracz nick");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("gildia")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
                return false;
            }
            final Player player = (Player)sender;
            if (args.length == 0) {
                CmdGildia.gildia(player);
            }
            else if (args.length == 1) {
                CmdGildia.gildia(player, args[0]);
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /gildia skrot");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("dg")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length >= 1) {
                    String message = "";
                    for (final String arg : args) {
                        if (message.isEmpty()) {
                            message = arg;
                        }
                        else {
                            message = message + " " + arg;
                        }
                    }
                    CmdDg.dg(player, message);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /dg wiadomosc");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("ds")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length >= 1) {
                    String message = "";
                    for (final String arg : args) {
                        if (message.isEmpty()) {
                            message = arg;
                        }
                        message = message + " " + arg;
                    }
                    CmdDs.ds(player, message);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /ds wiadomosc");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("setrank")) {
            if (sender.isOp() || sender.hasPermission("ccGuilds.setRank")) {
                if (args.length == 2) {
                    final String nick2 = args[0];
                    final String amount = args[1];
                    if (!StringUtils.isNumeric(amount)) {
                        sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /setrank nick ilosc!");
                        return false;
                    }
                    final int number = Integer.parseInt(amount);
                    UUID uuid;
                    try {
                        uuid = CcSectorsAPI.getUUID(nick2);
                    }
                    catch (Exception ex) {
                        sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Blad podczas pobierania UUID z bazy danych!");
                        Logger.getLogger(CcGuilds.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                    }
                    if (uuid == null) {
                        sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Gracza nie ma w bazie!");
                        return false;
                    }
                    final PlayerData data = DataManager.getPlayerData(uuid);
                    data.setRank(number);
                    DataManager.updatePlayerData(uuid, data);
                    sender.sendMessage(ChatColor.GREEN + "Ustawiono nowy ranking!");
                }
                else {
                    sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /setrank nick ilosc!");
                }
            }
        }
        else if (cmd.getName().equalsIgnoreCase("opis")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length >= 1) {
                    String description = "";
                    for (final String arg : args) {
                        if (description.isEmpty()) {
                            description = arg;
                        }
                        else {
                            description = description + " " + arg;
                        }
                    }
                    CmdOpis.opis(player, description);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /opis tekst");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("itemy")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdItemy.itemy(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /itemy");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("efekty")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdEfekty.efekty(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /efekty");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("ustawdom")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdUstawdom.ustawdom(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /ustawdom");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("ustawteren")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 0) {
                    CmdUstawteren.ustawteren(player);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /ustawteren");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("czlonek")) {
            if (sender instanceof Player) {
                final Player player = (Player)sender;
                if (args.length == 1) {
                    final String nick = args[0];
                    CmdCzlonek.czlonek(player, nick);
                }
                else {
                    player.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Poprawne uzycie: /czlonek nick");
                }
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Komenda tylko dla gracza!");
            }
        }
        else if (cmd.getName().equalsIgnoreCase("killmeps")) {
            if (!(sender instanceof Player)) {
                PubSub.stopListen();
            }
        }
        else if (cmd.getName().equalsIgnoreCase("logblockupload") && sender instanceof Player) {
            final Player player = (Player)sender;
            LogBlockCheckListener.upload.add(player.getUniqueId());
        }
        return false;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String lable, final String[] args) {
        return new ArrayList<String>();
    }
    
    public static CcGuilds getPlugin() {
        return CcGuilds.plugin;
    }
    
    public static String getTimeTo(final long to) {
        final long now = System.currentTimeMillis();
        long diff = to - now;
        long seconds = diff / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        final long days = hours / 24L;
        diff -= seconds * 1000L;
        seconds -= minutes * 60L;
        minutes -= hours * 60L;
        hours -= days * 24L;
        String time = "";
        if (days != 0L) {
            time = time + days + "d ";
        }
        if (hours != 0L) {
            time = time + hours + "h ";
        }
        if (minutes != 0L) {
            time = time + minutes + "m ";
        }
        if (seconds != 0L) {
            time = time + seconds + "s ";
        }
        return time.substring(0, time.length() - 1);
    }
    
    public static PlayerData getPlayerData(final UUID uuid) {
        return DataManager.getPlayerData(uuid);
    }
    
    public static GuildData getGuildData(final String shortCut) {
        if (shortCut == null) {
            return null;
        }
        return DataManager.getGuildData(shortCut);
    }
}
