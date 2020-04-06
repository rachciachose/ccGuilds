// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildMemberAddEvent;
import java.util.UUID;
import pl.best241.ccguilds.pubsub.PubSub;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.ArrayList;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdDolacz
{
    public static void dolacz(final Player player, final String guild) {
        final UUID playerUUID = player.getUniqueId();
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        if (playerData.getGuildName() == null) {
            final ArrayList<String> invs = CmdZapros.invitations.get(playerUUID);
            if (invs == null) {
                player.sendMessage(MessagesData.dolaczYouWereNotInvited);
                return;
            }
            if (invs.contains(guild.toLowerCase())) {
                invs.remove(guild.toLowerCase());
                CmdZapros.invitations.put(playerUUID, invs);
                final GuildData guildData = DataManager.getGuildData(guild.toLowerCase());
                final int gold = guildData.getMembers().size() * 6;
                final ItemStack item = new ItemStack(Material.GOLD_INGOT, gold);
                if (!player.getInventory().contains(Material.GOLD_INGOT, gold)) {
                    player.sendMessage(MessagesData.getMessage("dolacz.youNeedMoreGold").replace("%cost", Integer.toString(gold)));
                    return;
                }
                player.getInventory().removeItem(new ItemStack[] { item });
                playerData.setGuildName(guildData.getShortCut());
                playerData.setGuildJoinTime(System.currentTimeMillis());
                guildData.getMembers().add(playerUUID);
                DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                DataManager.updatePlayerData(playerUUID, playerData);
                PubSub.broadcastMessage(MessagesData.dolaczPlayerJoindGuild.replace("%player", player.getName()).replace("%fullname", guildData.getFullName()).replace("%shortcut", guildData.getShortCut()));
                final UUID whoInvented = CmdZapros.whoInvented.get(playerUUID);
                CmdZapros.whoInvented.remove(playerUUID);
                final GuildMemberAddEvent guildMemberAddEvent = new GuildMemberAddEvent(System.currentTimeMillis(), guild, player.getUniqueId(), whoInvented);
                Redis.addGuildEvent(guildMemberAddEvent);
                CcScoreboards.refreshPlayer(player);
                CcScoreboards.refreshPlayersFor(player);
                TabManager.refreshPlayerGuild(player);
            }
            else {
                player.sendMessage(MessagesData.dolaczYouWereNotInvitedToThisGuild);
            }
        }
        else {
            player.sendMessage(MessagesData.dolaczYouAreAlredyInAGuild);
        }
    }
}
