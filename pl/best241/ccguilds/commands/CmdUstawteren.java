// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.data.GuildHomeData;
import pl.best241.ccguilds.manager.GuildManager;
import pl.best241.ccguilds.data.CuboidData;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdUstawteren
{
    public static void ustawteren(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guild = DataManager.getGuildData(playerData.getGuildName());
        if (guild != null) {
            if (guild.getCuboid() == null) {
                if (CcSectorsAPI.getDistanceToNearestSector(player.getLocation()) <= Config.defaultMaxGuildRadiusIncreased + Config.defaultMinDistanceToSector) {
                    player.sendMessage(MessagesData.getMessage("ustawteren.youAreToCloseToSectorBorder"));
                    return;
                }
                final int height = player.getLocation().getBlockY();
                if (height < Config.defaultBaseBlockFrom || height > Config.defaultBaseBlockTo) {
                    player.sendMessage(MessagesData.getMessage("zaloz.baseCanBeOnlyAtYFromTo"));
                    return;
                }
                final long lastCuboidDestoryTimeDiff = System.currentTimeMillis() - guild.getLastCuboidDestoryTime();
                if (lastCuboidDestoryTimeDiff <= Config.defaultTimeBetweenBaseCreations) {
                    final int minutesLeft = (int)((Config.defaultTimeBetweenBaseCreations - lastCuboidDestoryTimeDiff) / 60L / 1000L);
                    final int secondsLeft = (int)((Config.defaultTimeBetweenBaseCreations - lastCuboidDestoryTimeDiff - minutesLeft * 60 * 1000) / 1000L);
                    String timeLeft = "1s";
                    if (minutesLeft != 0 && secondsLeft != 0) {
                        timeLeft = minutesLeft + "m " + secondsLeft + "s";
                    }
                    else if (minutesLeft == 0 && secondsLeft != 0) {
                        timeLeft = secondsLeft + "s";
                    }
                    else if (minutesLeft != 0 && secondsLeft == 0) {
                        timeLeft = minutesLeft + "m";
                    }
                    player.sendMessage(MessagesData.getMessage("ustawteren.youMustWaitToCreateTerrain").replace("%time", timeLeft));
                    return;
                }
                final TeleportLocation cuboidLocation;
                final TeleportLocation playerTeleportLocation = cuboidLocation = new TeleportLocation(player.getLocation());
                if (playerTeleportLocation.getX() < 0.0) {
                    cuboidLocation.setX(cuboidLocation.getX() - 1.0);
                }
                if (playerTeleportLocation.getZ() < 0.0) {
                    cuboidLocation.setZ(cuboidLocation.getZ() - 1.0);
                }
                final CuboidData cuboid = new CuboidData(cuboidLocation, Config.defaultGuildRadius, height - 1);
                if (!GuildManager.otherGuildsOnThisArea(cuboid, Config.defaultMaxGuildRadiusIncreased)) {
                    guild.setCuboid(cuboid);
                    final GuildHomeData guildHomeData = new GuildHomeData(playerTeleportLocation.getSectorType(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                    guild.setGuildHomeData(guildHomeData);
                    guild.rebuildBaseBlock();
                    guild.setBasePoints(Config.defaultBasePointsAfterCuboidRenew);
                    DataManager.updateGuildData(guild);
                    PubSub.broadcastMessage(MessagesData.getMessage("ustawteren.guildAgainClaimedTerrain").replace("%guildFullName", guild.getFullName()).replace("%guildShortCut", guild.getShortCut()));
                    player.sendMessage(MessagesData.getMessage("ustawteren.claimbDescription").replace("%height", height + ""));
                }
                else {
                    player.sendMessage(MessagesData.getMessage("ustawteren.youAreToCloseToAnotherGuild"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("ustawteren.yourGuildHasAlredyClimbedTerrain"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("ustawteren.youAreNotInAnyGuild"));
        }
    }
}
