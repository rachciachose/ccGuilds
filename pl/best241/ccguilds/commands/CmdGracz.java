// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.text.DateFormat;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import pl.best241.ccsectors.api.CcSectorsAPI;
import org.bukkit.entity.Player;

public class CmdGracz
{
    public static void cmdGracz(final Player sender, final String playerName) {
        UUID uuid;
        try {
            uuid = CcSectorsAPI.getUUID(playerName);
        }
        catch (Exception ex) {
            sender.sendMessage(ChatColor.DARK_GRAY + " »" + ChatColor.RED + "Blad podczas pobierania UUID z bazy danych!");
            Logger.getLogger(CmdGracz.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (uuid == null) {
            sender.sendMessage(MessagesData.getMessage("gracz.playerNotInDatabase"));
        }
        else {
            final PlayerData data = DataManager.getPlayerData(uuid);
            if (data == null) {
                sender.sendMessage(MessagesData.getMessage("gracz.playerNotInDatabase"));
            }
            else {
                String guild = data.getGuildName();
                if (guild == null) {
                    guild = MessagesData.getMessage("gracz.noGuild");
                }
                else {
                    final DateFormat formatter = new SimpleDateFormat(MessagesData.getMessage("gracz.dateFormat"));
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(data.getGuildJoinTime());
                    guild += MessagesData.getMessage("gracz.timeFormat").replace("%time", formatter.format(calendar.getTime()));
                }
                final String nick = DataManager.getPlayerName(uuid);
                if (nick == null) {
                    DataManager.getPlayerNameFromDb(uuid);
                    DataManager.updatePlayerName(uuid, nick);
                    DataManager.updatePlayerUUID(nick, uuid);
                }
                sender.sendMessage(MessagesData.getMessage("gracz.description").replace("%nick", nick).replace("%guild", guild).replace("%rank", data.getRank() + "").replace("%kills", data.getKills() + "").replace("%deaths", data.getDeaths() + "").replace("%kd", getKdRatio(data)));
            }
        }
    }
    
    public static String getKdRatio(final PlayerData data) {
        final double kills = data.getKills();
        double deaths = data.getDeaths();
        if (kills == 0.0) {
            return "-";
        }
        if (deaths == 0.0) {
            deaths = 1.0;
        }
        double valueCalc = kills / deaths;
        valueCalc = Math.round(valueCalc * 100.0) / 100L;
        final String kdRatio = valueCalc + "";
        return kdRatio;
    }
}
