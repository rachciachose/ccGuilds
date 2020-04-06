// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccscoreboards.events.PlayerScoreEvent;
import org.bukkit.event.Listener;

public class PlayerScoreListener implements Listener
{
    @EventHandler
    public static void playerScoreListener(final PlayerScoreEvent event) {
        final Player player = event.getPlayer();
        final Player namedPlayer = event.getNamedPlayer();
        final UUID namedUUID = DataManager.getPlayerUUID(namedPlayer);
        final PlayerData namedData = DataManager.getPlayerData(namedUUID);
        event.setScore(namedData.getRank());
        event.setText(MessagesData.getMessage("playerScoreEvent.textStyle"));
    }
}
