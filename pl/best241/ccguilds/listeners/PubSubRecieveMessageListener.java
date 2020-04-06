// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.effects.EffectManager;
import pl.best241.ccguilds.effects.EffectType;
import pl.best241.rdbplugin.pubsub.PubSub;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.CcGuilds;
import pl.best241.rdbplugin.events.PubSubRecieveMessageEvent;
import org.bukkit.event.Listener;

public class PubSubRecieveMessageListener implements Listener
{
    @EventHandler
    public static void pubSubRecieveMessageListener(final PubSubRecieveMessageEvent event) {
        if (event.getChannel().equals("reloadAllMessagesRequest")) {
            MessagesData.loadMessages((Plugin)CcGuilds.getPlugin());
            PubSub.broadcast("reloadAllMessagesResponse", CcGuilds.getPlugin().getName());
        }
        else if (event.getChannel().equals("ccGuilds.effectBroadcast")) {
            final String[] rawData = event.getMessage().split(";");
            final String shortCut = rawData[0];
            final String effectType = rawData[1];
            final EffectType type = EffectType.valueOf(effectType);
            EffectManager.recieveEffect(shortCut, type);
        }
    }
}
