// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import java.util.Random;
import java.util.List;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccguilds.data.WarData;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;

public class WarManager
{
    public static void startTicker() {
        Bukkit.getScheduler().runTaskTimer((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final GuildData guild : DataManager.getAllGuilds().values()) {
                    final List<WarData> wars = guild.getWars();
                    final Iterator warsIterator = wars.iterator();
                    synchronized (wars) {
                        while (warsIterator.hasNext()) {
                            final WarData guildWar = warsIterator.next();
                            if (guildWar.getStartTime() + Config.defaultTimeToWarExpire <= System.currentTimeMillis()) {
                                final GuildData otherWar = DataManager.getGuildData(guildWar.getChallangedGuild().equalsIgnoreCase(guild.getShortCut()) ? guildWar.getTargetGuild() : guildWar.getChallangedGuild());
                                if (guildWar == null) {
                                    continue;
                                }
                                Bukkit.getScheduler().runTask((Plugin)CcGuilds.getPlugin(), (Runnable)new Runnable() {
                                    @Override
                                    public void run() {
                                        guild.remWar(guildWar);
                                        if (otherWar == null) {
                                            DataManager.updateGuildData(guild);
                                            return;
                                        }
                                        otherWar.remWar(otherWar.getWar(guild.getShortCut()));
                                        DataManager.updateGuildData(guild);
                                        PubSub.broadcastMessage(MessagesData.getMessage("wojna.warExpired").replace("%guild1fullName", guild.getFullName()).replace("%guild1shortcut", guild.getShortCut()).replace("%guild2fullName", otherWar.getFullName()).replace("%guild2shortcut", otherWar.getShortCut()));
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }, (long)((new Random().nextInt(10) + 1) * 5 * 60 * 20), 6000L);
    }
}
