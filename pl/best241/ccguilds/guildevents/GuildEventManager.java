// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.guildevents;

import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import pl.best241.ccguilds.guildevents.events.GuildCreateEvent;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

public class GuildEventManager
{
    private static final Gson gson;
    
    public static String parseToString(final EventData eventData) {
        return GuildEventManager.gson.toJson((Object)eventData);
    }
    
    public static EventData parseToObject(final String rawData) {
        final EventData notExtendedData = (EventData)GuildEventManager.gson.fromJson(rawData, (Class)EventData.class);
        switch (notExtendedData.getType()) {
            case GUILD_CREATE: {
                return (EventData)GuildEventManager.gson.fromJson(rawData, (Class)GuildCreateEvent.class);
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        gson = new GsonBuilder().create();
    }
}
