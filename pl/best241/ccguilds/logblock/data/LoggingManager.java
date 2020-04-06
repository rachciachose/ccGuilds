// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.data;

import org.bukkit.craftbukkit.libs.com.google.gson.FieldAttributes;
import org.bukkit.craftbukkit.libs.com.google.gson.ExclusionStrategy;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.sql.DriverManager;
import pl.best241.ccguilds.manager.ConfigManager;
import java.util.Iterator;
import java.sql.ResultSet;
import java.util.Collection;
import pl.best241.ccguilds.logblock.data.actions.ActionType;
import java.util.UUID;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import pl.best241.ccguilds.logblock.data.actions.Action;
import java.util.Queue;

public class LoggingManager
{
    private static Queue<Action> actionsQueue;
    private static Gson gson;
    private static String host;
    private static int port;
    private static String user;
    private static String password;
    private static String database;
    private static String table;
    
    public static void main(final String... args) {
        final String splash = "Gowno";
        final byte r = -1;
        final byte g = -1;
        final byte b = -1;
        final String json = "{\"sleep\": 5000, \"text\": \"" + splash + "\", \"r\": " + r + ", \"g\": " + g + ", \"b\": " + b + "}";
        final Object fromJson = LoggingManager.gson.fromJson(json, (Class)SplashData.class);
        System.out.println(fromJson);
    }
    
    public static void addToQueue(final Action action) {
        LoggingManager.actionsQueue.add(action);
    }
    
    public static void runTimer() {
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                final Connection conn = LoggingManager.getMysqlConnection();
            Label_0004_Outer:
                while (true) {
                    while (true) {
                        try {
                            while (true) {
                                final Action action = LoggingManager.actionsQueue.poll();
                                if (action == null) {
                                    break;
                                }
                                final String parsed = LoggingManager.gson.toJson((Object)action);
                                final String query = "INSERT INTO " + LoggingManager.table + " (id, time, world, x, y, z, uuid, name, actionType, actionData) values (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                                final PreparedStatement stmt = conn.prepareStatement(query);
                                stmt.setLong(1, action.getTime());
                                stmt.setString(2, action.getSimpleLocation().getWorldName());
                                stmt.setInt(3, action.getSimpleLocation().getX());
                                stmt.setInt(4, action.getSimpleLocation().getY());
                                stmt.setInt(5, action.getSimpleLocation().getZ());
                                stmt.setString(6, action.getUUID().toString());
                                stmt.setString(7, action.getName());
                                stmt.setString(8, action.getType().toString());
                                stmt.setString(9, parsed);
                                stmt.executeUpdate();
                            }
                            break;
                        }
                        catch (SQLException ex) {
                            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
                            continue Label_0004_Outer;
                        }
                        continue;
                    }
                }
                try {
                    conn.close();
                }
                catch (SQLException ex) {
                    Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }, 300L, 300L);
    }
    
    public static ArrayList<Action> getActionsByLocation(final SimpleLocation loc) {
        final ArrayList<Action> actionsArray = new ArrayList<Action>();
        Connection conn = null;
        try {
            conn = getMysqlConnection();
            final String query = "SELECT * FROM " + LoggingManager.table + " WHERE  `x` = ? AND `y` = ? AND `z` = ? AND `world` = ? ORDER BY `time` DESC LIMIT 120";
            final PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, loc.getX());
            stmt.setInt(2, loc.getY());
            stmt.setInt(3, loc.getZ());
            stmt.setString(4, loc.getWorldName());
            System.out.println(stmt.toString());
            final ResultSet executedQuery = stmt.executeQuery();
            while (executedQuery.next()) {
                final long time = executedQuery.getLong("time");
                final String world = executedQuery.getString("world");
                final int x = executedQuery.getInt("x");
                final int y = executedQuery.getInt("y");
                final int z = executedQuery.getInt("z");
                final UUID uuid = UUID.fromString(executedQuery.getString("uuid"));
                final String nickName = executedQuery.getString("name");
                final ActionType actionType = ActionType.valueOf(executedQuery.getString("actionType"));
                final String actionJsonData = executedQuery.getString("actionData");
                final Action action = (Action)LoggingManager.gson.fromJson(actionJsonData, actionType.getClassType());
                if (action != null) {
                    action.setTime(time);
                    action.setName(nickName);
                    action.setSimpleLocation(loc);
                    action.setUUID(uuid);
                    action.setActionType(actionType);
                    actionsArray.add(action);
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
            try {
                conn.close();
            }
            catch (SQLException ex) {
                Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                conn.close();
            }
            catch (SQLException ex2) {
                Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex2);
            }
        }
        final ArrayList<Action> actionsToOrdered = new ArrayList<Action>();
        for (final Action action2 : LoggingManager.actionsQueue) {
            if (action2.getSimpleLocation().equals(loc)) {
                actionsToOrdered.add(action2);
            }
        }
        bubbleSort(actionsToOrdered);
        actionsArray.addAll(actionsToOrdered);
        return actionsArray;
    }
    
    private static void bubbleSort(final ArrayList<Action> actions) {
        final int n = actions.size();
        Action temp = null;
        for (int i = 0; i < n; ++i) {
            for (int j = 1; j < n; ++j) {
                if (actions.get(j - 1).getTime() > actions.get(j).getTime()) {
                    temp = actions.get(j - 1);
                    actions.set(j - 1, actions.get(j));
                    actions.set(j, temp);
                }
            }
        }
    }
    
    public static Connection getMysqlConnection() {
        if (LoggingManager.host == null) {
            LoggingManager.host = ConfigManager.getString("mysqlHost");
        }
        if (LoggingManager.port == 0) {
            LoggingManager.port = ConfigManager.getInt("mysqlPort");
        }
        if (LoggingManager.user == null) {
            LoggingManager.user = ConfigManager.getString("mysqlUsername");
        }
        if (LoggingManager.password == null) {
            LoggingManager.password = ConfigManager.getString("mysqlPassword");
        }
        if (LoggingManager.database == null) {
            LoggingManager.database = ConfigManager.getString("mysqlDatabase");
        }
        if (LoggingManager.table == null) {
            LoggingManager.table = ConfigManager.getString("mysqlTable");
        }
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            final String connectionUrl = "jdbc:mysql://" + LoggingManager.host + ":" + LoggingManager.port + "/" + LoggingManager.database;
            conn = DriverManager.getConnection(connectionUrl, LoggingManager.user, LoggingManager.password);
        }
        catch (InstantiationException ex) {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex2) {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (ClassNotFoundException ex3) {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex3);
        }
        catch (SQLException ex4) {
            Logger.getLogger(LoggingManager.class.getName()).log(Level.SEVERE, null, ex4);
        }
        return conn;
    }
    
    static {
        LoggingManager.actionsQueue = new ConcurrentLinkedQueue<Action>();
        LoggingManager.gson = new GsonBuilder().addSerializationExclusionStrategy((ExclusionStrategy)new ExclusionStrategy() {
            public boolean shouldSkipField(final FieldAttributes f) {
                return f.getDeclaringClass().equals(Action.class);
            }
            
            public boolean shouldSkipClass(final Class<?> type) {
                return false;
            }
        }).create();
        LoggingManager.host = null;
        LoggingManager.port = 0;
        LoggingManager.user = null;
        LoggingManager.password = null;
        LoggingManager.database = null;
        LoggingManager.table = null;
    }
    
    class SplashData
    {
        private byte r;
        private byte g;
        private byte b;
        private String text;
        private int sleep;
        
        public SplashData(final String text, final int sleep, final byte r, final byte g, final byte b) {
            this.text = text;
            this.sleep = sleep;
            this.r = r;
            this.g = g;
            this.b = b;
        }
        
        public byte getRed() {
            return this.r;
        }
        
        public byte getGreen() {
            return this.g;
        }
        
        public byte getBlue() {
            return this.b;
        }
        
        public int getRGB() {
            return this.getRed() << 16 | this.getGreen() << 8 | this.getBlue();
        }
        
        public String getText() {
            return this.text;
        }
        
        public int getSleep() {
            return this.sleep;
        }
        
        @Override
        public String toString() {
            return this.text;
        }
    }
}
