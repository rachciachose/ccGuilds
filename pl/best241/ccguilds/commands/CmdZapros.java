// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.Bukkit;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;

public class CmdZapros
{
    public static HashMap<UUID, ArrayList<String>> invitations;
    public static HashMap<UUID, UUID> whoInvented;
    
    public static void zapros(final Player player, final String target) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final PlayerData data = DataManager.getPlayerData(playerUUID);
        final String guildName = data.getGuildName();
        if (guildName != null) {
            final GuildData guildData = DataManager.getGuildData(guildName.toLowerCase());
            if (guildData.getLeader().toString().equals(playerUUID.toString()) || (guildData.getDeputy() != null && guildData.getDeputy().toString().equals(playerUUID.toString()))) {
                if (player.getName().equalsIgnoreCase(target)) {
                    player.sendMessage(MessagesData.getMessage("zapros.youCannotInviteYourself"));
                    return;
                }
                Player targetPlayer = null;
                for (final Player online : Bukkit.getOnlinePlayers()) {
                    if (online.getName().equalsIgnoreCase(target)) {
                        targetPlayer = online;
                        break;
                    }
                }
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    final UUID targetUUID = DataManager.getPlayerUUID(targetPlayer);
                    final PlayerData targetData = DataManager.getPlayerData(targetUUID);
                    if (targetData.getGuildName() == null) {
                        ArrayList<String> invs;
                        if (!CmdZapros.invitations.containsKey(targetUUID)) {
                            invs = new ArrayList<String>();
                        }
                        else {
                            invs = CmdZapros.invitations.get(targetUUID);
                        }
                        invs.add(guildData.getShortCut().toLowerCase());
                        CmdZapros.invitations.put(targetUUID, invs);
                        CmdZapros.whoInvented.put(targetUUID, player.getUniqueId());
                        final int gold = guildData.getMembers().size() * 6;
                        player.sendMessage(MessagesData.getMessage("zapros.youInvitedPlayerToGuild").replace("%player", targetPlayer.getName()));
                        targetPlayer.sendMessage(MessagesData.getMessage("zapros.youWereInvitedToGuild").replace("%fullName", guildData.getFullName()).replace("%shortCut", guildData.getShortCut()).replace("%cost", Integer.toString(gold)));
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("zapros.thisPlayerIsAlredyInGuild"));
                    }
                }
                else {
                    player.sendMessage(MessagesData.getMessage("zapros.playerIsOfflineOrOnOtherSector"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("zapros.toDoThatYouMustBeLeaderOrDeputy"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("zapros.youAreNotInAnyGuild"));
        }
    }
    
    static {
        CmdZapros.invitations = new HashMap<UUID, ArrayList<String>>();
        CmdZapros.whoInvented = new HashMap<UUID, UUID>();
    }
}
