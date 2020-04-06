// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import java.util.Iterator;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.CcGuilds;
import pl.best241.ccguilds.data.WarData;
import pl.best241.ccguilds.data.AllianceData;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdGildia
{
    public static void gildia(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData data = DataManager.getPlayerData(uuid);
        if (data.getGuildName() != null) {
            gildia(player, data.getGuildName());
        }
        else {
            player.sendMessage(MessagesData.getMessage("gildia.youAreNotInAnyGuild"));
        }
    }
    
    public static void gildia(final Player player, final String shortCut) {
        final GuildData guild = DataManager.getGuildData(shortCut);
        if (guild != null) {
            String cuboid;
            if (guild.getCuboid() == null) {
                cuboid = MessagesData.getMessage("gildia.noCuboidState");
            }
            else {
                cuboid = MessagesData.getMessage("gildia.cuboidString").replace("%radius", 2 * guild.getCuboid().getRadius() + "");
            }
            String members = "";
            for (final UUID member : guild.getMembers()) {
                String lastName = DataManager.getPlayerName(member);
                if (lastName == null) {
                    lastName = DataManager.getPlayerNameFromDb(member);
                    DataManager.updatePlayerName(member, lastName);
                    DataManager.updatePlayerUUID(lastName, member);
                }
                if (CcSectorsAPI.isPlayerOnline(member)) {
                    if (!members.isEmpty()) {
                        members = members + MessagesData.getMessage("gildia.memberList.separator") + MessagesData.getMessage("gildia.memberList.onlinePlayer").replace("%player", lastName);
                    }
                    else {
                        members = MessagesData.getMessage("gildia.memberList.onlinePlayer").replace("%player", lastName);
                    }
                }
                else if (!members.isEmpty()) {
                    members = members + MessagesData.getMessage("gildia.memberList.separator") + MessagesData.getMessage("gildia.memberList.offlinePlayer").replace("%player", lastName);
                }
                else {
                    members = MessagesData.getMessage("gildia.memberList.offlinePlayer").replace("%player", lastName);
                }
            }
            String alliances = "";
            for (final AllianceData ally : guild.getAlliances()) {
                GuildData allyData;
                if (ally.getAcceptedGuild().equalsIgnoreCase(guild.getShortCut())) {
                    allyData = DataManager.getGuildData(ally.getRequestorGuild());
                }
                else {
                    allyData = DataManager.getGuildData(ally.getAcceptedGuild());
                }
                if (!alliances.isEmpty()) {
                    alliances = alliances + MessagesData.getMessage("gildia.allianceList.separator") + MessagesData.getMessage("gildia.allianceList.allyStyle").replace("%ally", allyData.getShortCut());
                }
                else {
                    alliances = MessagesData.getMessage("gildia.allianceList.allyStyle").replace("%ally", allyData.getShortCut());
                }
            }
            if (alliances.isEmpty()) {
                alliances = MessagesData.getMessage("gildia.allianceList.emptyList");
            }
            String wars = "";
            for (final WarData war : guild.getWars()) {
                String guildName = "";
                if (!war.getChallangedGuild().equalsIgnoreCase(guild.getShortCut())) {
                    final GuildData warGuild = DataManager.getGuildData(war.getChallangedGuild());
                    if (warGuild == null) {
                        continue;
                    }
                    if (war.getStartTime() < System.currentTimeMillis()) {
                        final WarData guildWar = warGuild.getWar(guild.getShortCut());
                        if (guildWar == null) {
                            continue;
                        }
                        if (guildWar.isAgressor(guild.getShortCut())) {
                            final int agressorKills = guildWar.getTargetKills();
                            final int guildKills = guildWar.getChallengingKills();
                        }
                        else {
                            final int guildKills = guildWar.getTargetKills();
                            final int agressorKills = guildWar.getChallengingKills();
                        }
                        guildName = MessagesData.getMessage("gildia.warList.warStartedStyle").replace("%warShortcut", warGuild.getShortCut());
                    }
                    else {
                        guildName = MessagesData.getMessage("gildia.warList.warWaitingForStartStyle").replace("%warShortcut", warGuild.getShortCut()).replace("%timeTo", CcGuilds.getTimeTo(war.getStartTime()));
                    }
                }
                else {
                    final GuildData warGuild = DataManager.getGuildData(war.getTargetGuild());
                    if (warGuild == null) {
                        continue;
                    }
                    if (war.getStartTime() < System.currentTimeMillis()) {
                        final WarData guildWar = warGuild.getWar(guild.getShortCut());
                        if (guildWar == null) {
                            continue;
                        }
                        if (guildWar.isAgressor(guild.getShortCut())) {
                            final int agressorKills = guildWar.getTargetKills();
                            final int guildKills = guildWar.getChallengingKills();
                        }
                        else {
                            final int guildKills = guildWar.getTargetKills();
                            final int agressorKills = guildWar.getChallengingKills();
                        }
                        guildName = MessagesData.getMessage("gildia.warList.warStartedStyle").replace("%warShortcut", warGuild.getShortCut());
                    }
                    else {
                        guildName = MessagesData.getMessage("gildia.warList.warWaitingForStartStyle").replace("%warShortcut", warGuild.getShortCut()).replace("%timeTo", CcGuilds.getTimeTo(war.getStartTime()));
                    }
                }
                if (wars.isEmpty()) {
                    wars = guildName;
                }
                else {
                    wars = wars + MessagesData.getMessage("gildia.warList.separator") + guildName;
                }
            }
            if (wars.isEmpty()) {
                wars = MessagesData.getMessage("gildia.warList.emptyList");
            }
            String description;
            if (guild.getDescription() != null) {
                description = guild.getDescription();
            }
            else {
                description = MessagesData.getMessage("gildia.noDescriptionState");
            }
            String deputy;
            if (guild.getDeputy() != null) {
                deputy = DataManager.getPlayerName(guild.getDeputy());
            }
            else {
                deputy = MessagesData.getMessage("gildia.noDescriptionState");
            }
            player.sendMessage(MessagesData.getMessage("gildia.description").replace("%shortcut", guild.getShortCut()).replace("%name", guild.getFullName()).replace("%leader", DataManager.getPlayerName(guild.getLeader())).replace("%deputy", deputy).replace("%state", guild.getFriendlyFire() ? MessagesData.getMessage("gildia.friendlyFireStateOn") : MessagesData.getMessage("gildia.friendlyFireStateOff")).replace("%rank", guild.getRank() + "").replace("%kills", guild.getGuildKills() + "").replace("%deaths", guild.getGuildDeaths() + "").replace("%cubSize", cuboid).replace("%basePoints", guild.getBasePoints() + "").replace("%description", description).replace("%memberList", members).replace("%allyList", alliances).replace("%warList", wars));
        }
        else {
            player.sendMessage(MessagesData.getMessage("gildia.thisGuildDoesNotExist"));
        }
    }
}
