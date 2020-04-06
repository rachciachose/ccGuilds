// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.messages;

import java.util.Iterator;
import org.bukkit.ChatColor;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.HashMap;

public class MessagesData
{
    public static int bazaTpAntilogoutTime;
    public static String bazaYouAreAlredyTeleporting;
    public static String bazaStartTeleporting;
    public static String bazaYourGuildHaveNoTerrain;
    public static String bazaYouAreNotMemberOfThisGuild;
    public static String bazaTeleportedSuccessfully;
    public static String bazaTeleportationCanceled;
    public static String dgYouAreNotInAnyGuild;
    public static String dgDgMessage;
    public static String dolaczYouWereNotInvited;
    public static String dolaczYouWereNotInvitedToThisGuild;
    public static String dolaczYouAreAlredyInAGuild;
    public static String dolaczPlayerJoindGuild;
    public static String dsMessageToGuilds;
    public static String dsYouAreNotInAnyGuild;
    private static MessagesConfig config;
    private static final HashMap<String, String> pathMessage;
    private static final HashMap<String, ArrayList<String>> pathMessageList;
    
    public static void loadMessages(final Plugin plugin) {
        (MessagesData.config = new MessagesConfig(plugin, "messages.yml")).saveDefaultConfig();
        MessagesData.config.reloadCustomConfig();
        final Set<String> keys = (Set<String>)MessagesData.config.getCustomConfig().getKeys(true);
        System.out.println(keys);
        MessagesData.pathMessage.clear();
        MessagesData.pathMessageList.clear();
        MessagesData.bazaTpAntilogoutTime = MessagesData.config.getInt("baza.bazaTpAntilogoutTime");
        MessagesData.bazaYouAreAlredyTeleporting = MessagesData.config.getString("baza.youAreDuringFightCantTeleport");
        MessagesData.bazaYouAreAlredyTeleporting = MessagesData.config.getString("baza.youAreAlredyTeleporting");
        MessagesData.bazaYourGuildHaveNoTerrain = MessagesData.config.getString("baza.yourGuildHaveNoTerrain");
        MessagesData.bazaStartTeleporting = MessagesData.config.getString("baza.startTeleporting");
        MessagesData.bazaYouAreNotMemberOfThisGuild = MessagesData.config.getString("baza.youAreNotMemberOfThisGuild");
        MessagesData.bazaTeleportedSuccessfully = MessagesData.config.getString("baza.teleportedSuccessfully");
        MessagesData.bazaTeleportationCanceled = MessagesData.config.getString("baza.teleportationCanceled");
        MessagesData.dgYouAreNotInAnyGuild = MessagesData.config.getString("dg.youAreNotInAnyGuild");
        MessagesData.dgDgMessage = MessagesData.config.getString("dg.dgMessage");
        MessagesData.dolaczYouWereNotInvited = MessagesData.config.getString("dolacz.youWereNotInvited");
        MessagesData.dolaczYouWereNotInvitedToThisGuild = MessagesData.config.getString("dolacz.youWereNotInvitedToThisGuild");
        MessagesData.dolaczYouAreAlredyInAGuild = MessagesData.config.getString("dolacz.youAreAlredyInAGuild");
        MessagesData.dolaczPlayerJoindGuild = MessagesData.config.getString("dolacz.playerJoindGuild");
        MessagesData.dsMessageToGuilds = MessagesData.config.getString("ds.messageToGuilds");
        MessagesData.dsYouAreNotInAnyGuild = MessagesData.config.getString("ds.youAreNotInAnyGuild");
    }
    
    public static String getMessage(final String path) {
        if (MessagesData.pathMessage.containsKey(path)) {
            return MessagesData.pathMessage.get(path);
        }
        final String message = MessagesData.config.getString(path);
        MessagesData.pathMessage.put(path, message);
        return message;
    }
    
    public static ArrayList<String> getMessageList(final String path) {
        if (MessagesData.pathMessageList.containsKey(path)) {
            return MessagesData.pathMessageList.get(path);
        }
        final ArrayList<String> list = (ArrayList<String>)MessagesData.config.getCustomConfig().getList(path);
        final ArrayList<String> coloredList = new ArrayList<String>();
        for (final String message : list) {
            coloredList.add(ChatColor.translateAlternateColorCodes('&', message));
        }
        MessagesData.pathMessageList.put(path, coloredList);
        return coloredList;
    }
    
    static {
        pathMessage = new HashMap<String, String>();
        pathMessageList = new HashMap<String, ArrayList<String>>();
    }
}
