// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import java.io.File;
import pl.best241.ccguilds.CcGuilds;

public class ConfigManager
{
    private static CcGuilds plugin;
    
    public static void load(final CcGuilds ccstones) {
        ConfigManager.plugin = ccstones;
        final File fileConfig = new File(ConfigManager.plugin.getDataFolder(), "config.yml");
        if (!fileConfig.exists()) {
            System.out.println("config.yml not found! Generating!");
            ConfigManager.plugin.getConfig().options().copyDefaults(true);
            ConfigManager.plugin.saveConfig();
        }
    }
    
    public static String getString(final String key) {
        return ConfigManager.plugin.getConfig().getString(key);
    }
    
    public static int getInt(final String path) {
        return ConfigManager.plugin.getConfig().getInt(path);
    }
}
