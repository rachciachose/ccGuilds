// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import pl.best241.ccguilds.data.CuboidData;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.ccguilds.guildevents.events.GuildCuboidEnlargeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdPowieksz
{
    public static void powieksz(final Player player) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        final GuildData guildData = playerData.getGuildData();
        if (guildData != null) {
            if (guildData.getLeader().toString().equals(uuid.toString())) {
                if (guildData.getCuboid() != null) {
                    final CuboidData cuboid = guildData.getCuboid();
                    if (cuboid.getRadius() >= Config.defaultMaxGuildRadiusIncreased) {
                        player.sendMessage(MessagesData.getMessage("powieksz.yourGuildReachedMaxCuboidSize"));
                        return;
                    }
                    final int step = (cuboid.getRadius() - Config.defaultGuildRadius + Config.defaultBlocksPerIncrease) / Config.defaultBlocksPerIncrease;
                    final Double cost = Math.pow(step * 1.2, 1.65) + 64.0;
                    final int diamonds = (int)(Object)cost;
                    final int emeralds = diamonds * 2 / 3;
                    if (player.hasPermission("ccGildie.noItemsForIncrease") || (player.getInventory().contains(Material.DIAMOND, diamonds) && player.getInventory().contains(Material.EMERALD, emeralds))) {
                        player.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.DIAMOND, diamonds) });
                        player.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.EMERALD, emeralds) });
                        cuboid.setRadius(cuboid.getRadius() + Config.defaultBlocksPerIncrease);
                        guildData.setCuboid(cuboid);
                        DataManager.updateGuildData(guildData.getShortCut().toLowerCase(), guildData);
                        player.sendMessage(MessagesData.getMessage("powieksz.terrainEnlarged"));
                        final GuildCuboidEnlargeEvent guildCuboidEnlargeEvent = new GuildCuboidEnlargeEvent(System.currentTimeMillis(), guildData.getShortCut(), cuboid.getRadius(), player.getUniqueId());
                        Redis.addGuildEvent(guildCuboidEnlargeEvent);
                        if (cuboid.getRadius() % 10 == 0) {
                            PubSub.broadcastMessage(MessagesData.getMessage("powieksz.guildEnlargedHisTerrainToRadius").replace("%guildFullName", guildData.getFullName()).replace("%guildShortCut", guildData.getShortCut()).replace("%radius", guildData.getCuboid().getRadius() * 2 + ""));
                        }
                        else {
                            PubSub.sendMessageToGuild(guildData.getShortCut(), MessagesData.getMessage("powieksz.yourGuildEnlargedHisTerrainToSize").replace("%radius", guildData.getCuboid().getRadius() * 2 + ""));
                        }
                    }
                    else {
                        player.sendMessage(MessagesData.getMessage("powieksz.toEnlargeYourTerrainYouNeed").replace("%diamonds", diamonds + "").replace("%emeralds", emeralds + "").replace("%diamondsMissing", diamonds - getAllNumerOfItemType(player, Material.DIAMOND) + "").replace("%emeraldsMissing", emeralds - getAllNumerOfItemType(player, Material.EMERALD) + ""));
                    }
                }
                else {
                    player.sendMessage(MessagesData.getMessage("powieksz.yourGuildDoesNotHaveHerTerrain"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("powieksz.youAreNotLeaderOfThisGuild"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("powieksz.youAreNotMemberInAnyGuild"));
        }
    }
    
    public static int getAllNumerOfItemType(final Player player, final Material material) {
        int items = 0;
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                if (item.getType() == material) {
                    items += item.getAmount();
                }
            }
        }
        return items;
    }
}
