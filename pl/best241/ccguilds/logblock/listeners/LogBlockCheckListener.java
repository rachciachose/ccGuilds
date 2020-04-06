// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.listeners;

import java.util.Random;
import java.math.BigInteger;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Iterator;
import com.mongodb.MongoClient;
import pl.best241.ccitemdumper.config.ConfigManager;
import org.bson.Document;
import pl.best241.ccguilds.logblock.data.actions.ChestAction;
import pl.best241.ccguilds.logblock.data.actions.BlockPlaceAction;
import pl.best241.ccguilds.logblock.data.actions.BlockBreakAction;
import java.util.Date;
import java.text.SimpleDateFormat;
import pl.best241.ccguilds.logblock.data.LoggingManager;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.messages.MessagesData;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class LogBlockCheckListener implements Listener
{
    private static HashMap<UUID, Long> logBlockTime;
    public static ArrayList<UUID> upload;
    private static final long timeBetweenLogBlockCheck = 1500L;
    private static SecureRandom random;
    
    @EventHandler
    public static void logBlockCheckListener(final PlayerInteractEvent event) {
        final ItemStack itemInHand = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && itemInHand != null && itemInHand.getType() == Material.WOOD_PICKAXE) {
            final Player player = event.getPlayer();
            final Block block = event.getClickedBlock();
            if (LogBlockCheckListener.logBlockTime.containsKey(player.getUniqueId()) && LogBlockCheckListener.logBlockTime.get(player.getUniqueId()) + 1500L >= System.currentTimeMillis()) {
                player.sendMessage(MessagesData.getMessage("logblock.youCantUseLogblockSoFast"));
                System.out.println("Cannot open, try witch a bigger delay");
                return;
            }
            LogBlockCheckListener.logBlockTime.put(player.getUniqueId(), System.currentTimeMillis());
            Bukkit.getScheduler().runTaskAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                @Override
                public void run() {
                    boolean shouldUpload = false;
                    if (LogBlockCheckListener.upload.contains(player.getUniqueId())) {
                        shouldUpload = true;
                        LogBlockCheckListener.upload.remove(player.getUniqueId());
                    }
                    final ArrayList<pl.best241.ccguilds.logblock.data.actions.Action> actionsByLocation = LoggingManager.getActionsByLocation(new SimpleLocation(block.getLocation()));
                    player.sendMessage(MessagesData.getMessage("logblock.foundResults").replace("%number", Integer.toString(actionsByLocation.size())));
                    String fullDataMessage = "";
                    for (final pl.best241.ccguilds.logblock.data.actions.Action action : actionsByLocation) {
                        final String dataString = new SimpleDateFormat(MessagesData.getMessage("logblock.dateFormat")).format(new Date(action.getTime()));
                        final String name = action.getName();
                        String actionMessage = "";
                        if (action instanceof BlockBreakAction) {
                            final BlockBreakAction blockBreakAction = (BlockBreakAction)action;
                            actionMessage = MessagesData.getMessage("logblock.blockBreakAction").replace("%date", dataString).replace("%blockDestroyed", blockBreakAction.getBlockDigged().toString()).replace("%player", name);
                        }
                        else if (action instanceof BlockPlaceAction) {
                            final BlockPlaceAction blockPlaceAction = (BlockPlaceAction)action;
                            actionMessage = MessagesData.getMessage("logblock.blockPlaceAction").replace("%date", dataString).replace("%blockPlaced", blockPlaceAction.getBlockPlaced().toString()).replace("%player", name);
                        }
                        else if (action instanceof ChestAction) {
                            final ChestAction chestAction = (ChestAction)action;
                            final String number = Integer.toString(Math.abs(chestAction.getAmount()));
                            if (chestAction.getAmount() > 0) {
                                actionMessage = MessagesData.getMessage("logblock.chestItemGiven").replace("%date", dataString).replace("%number", number).replace("%type", chestAction.getMaterial().toString()).replace("%player", name);
                            }
                            else {
                                actionMessage = MessagesData.getMessage("logblock.chestItemTaken").replace("%date", dataString).replace("%number", number).replace("%type", chestAction.getMaterial().toString()).replace("%player", name);
                            }
                        }
                        if (shouldUpload) {
                            fullDataMessage = fullDataMessage + actionMessage + "\n";
                        }
                        else {
                            player.sendMessage(actionMessage);
                        }
                    }
                    if (shouldUpload) {
                        final String link = LogBlockCheckListener.nextSessionId();
                        final Document doc = new Document("uuid", (Object)player.getUniqueId().toString()).append("link", (Object)link).append("data", (Object)fullDataMessage);
                        MongoClient mongoClient = null;
                        try {
                            mongoClient = new MongoClient(ConfigManager.host, 27017);
                            final MongoDatabase database = mongoClient.getDatabase("craftcore");
                            final MongoCollection<Document> collection = (MongoCollection<Document>)database.getCollection("logblock_links");
                            collection.insertOne((Object)doc);
                        }
                        catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
                        finally {
                            if (mongoClient != null) {
                                mongoClient.close();
                            }
                        }
                        player.sendMessage("Aby zobaczyc wszystkie akcje wejdz tutaj: http://craftcore.pl/logblock/?link=" + link);
                    }
                }
            });
        }
    }
    
    public static String nextSessionId() {
        return new BigInteger(130, LogBlockCheckListener.random).toString(32);
    }
    
    public static void enableLogBlockCheckListener() {
        Bukkit.getServer().getPluginManager().registerEvents((Listener)new LogBlockCheckListener(), (Plugin)CcGuilds.getPlugin());
    }
    
    static {
        LogBlockCheckListener.logBlockTime = new HashMap<UUID, Long>();
        LogBlockCheckListener.upload = new ArrayList<UUID>();
        LogBlockCheckListener.random = new SecureRandom();
    }
}
