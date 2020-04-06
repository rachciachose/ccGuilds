// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import org.bukkit.inventory.PlayerInventory;
import java.util.Iterator;
import pl.best241.ccguilds.data.PolishItemNames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;
import pl.best241.ccguilds.data.PlayerData;
import pl.best241.ccguilds.guildevents.EventData;
import pl.best241.ccguilds.guildevents.events.GuildCreateEvent;
import pl.best241.ccguilds.pubsub.PubSub;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.backend.Redis;
import pl.best241.rdbplugin.JedisFactory;
import java.util.List;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.visualeffects.VisualEffectType;
import java.util.concurrent.ConcurrentHashMap;
import pl.best241.ccguilds.data.AllianceData;
import pl.best241.ccguilds.data.WarData;
import java.util.ArrayList;
import pl.best241.ccguilds.data.GuildHomeData;
import java.util.UUID;
import java.util.HashSet;
import pl.best241.ccguilds.manager.GuildManager;
import pl.best241.ccguilds.data.CuboidData;
import pl.best241.ccsectors.api.TeleportLocation;
import pl.best241.ccguilds.backend.Config;
import pl.best241.ccsectors.api.CcSectorsAPI;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;

public class CmdZaloz
{
    public static void zaloz(final Player player, final String shortcut, final String name) {
        final UUID uuid = DataManager.getPlayerUUID(player);
        final PlayerData playerData = DataManager.getPlayerData(uuid);
        if (playerData.getGuildName() == null) {
            if (shortcut.length() > 5 || shortcut.length() < 2) {
                player.sendMessage(MessagesData.getMessage("zaloz.shortcutLenghtMightBeBetween").replace("%charsMin", "2").replace("%charsMax", "5"));
                return;
            }
            if (name.length() > 30 || name.length() < 8) {
                player.sendMessage(MessagesData.getMessage("zaloz.fullNameLenghtMightBeBetween").replace("%charsMin", "8").replace("%charsMax", "30"));
                return;
            }
            if (CcSectorsAPI.getDistanceToNearestSector(player.getLocation()) <= Config.defaultMaxGuildRadiusIncreased + Config.defaultMinDistanceToSector) {
                player.sendMessage(MessagesData.getMessage("zaloz.youAreToCloseToOtherSector"));
                return;
            }
            if (CcSectorsAPI.getSectorName().toLowerCase().contains("spawn")) {
                player.sendMessage("Nie mozesz zakladac gildii na spawnie!");
                return;
            }
            final int height = player.getLocation().getBlockY();
            if (height < Config.defaultBaseBlockFrom || height > Config.defaultBaseBlockTo) {
                player.sendMessage(MessagesData.getMessage("zaloz.baseCanBeOnlyAtYFromTo"));
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
            if (DataManager.getGuildData(shortcut.toLowerCase()) != null) {
                player.sendMessage(MessagesData.getMessage("zaloz.guildWithThisShortcutAlredyExist"));
                return;
            }
            if (GuildManager.getGuildDataByFullName(name) != null) {
                player.sendMessage(MessagesData.getMessage("zaloz.guildWithThisFullnameAlredyExist"));
                return;
            }
            if (!GuildManager.otherGuildsOnThisArea(cuboid, Config.defaultMaxGuildRadiusIncreased)) {
                final boolean paid = player.hasPermission("ccGildie.noItemsCreation") || getFormPlayerItemsForGuild(player);
                if (paid) {
                    final HashSet<UUID> members = new HashSet<UUID>();
                    members.add(uuid);
                    final GuildHomeData guildHomeData = new GuildHomeData(playerTeleportLocation.getSectorType(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
                    final GuildData guild = new GuildData(shortcut, name, null, Config.defaultGuildRank, Config.defaultBasePoints, System.currentTimeMillis(), false, false, cuboid, uuid, null, members, new ArrayList<WarData>(), new ArrayList<AllianceData>(), 0L, 0, 0, new ConcurrentHashMap<VisualEffectType, Long>(), guildHomeData);
                    playerData.setGuildName(guild.getShortCut());
                    playerData.setGuildJoinTime(System.currentTimeMillis());
                    final Jedis jedis = JedisFactory.getInstance().getJedis();
                    Redis.addGuildToList(shortcut.toLowerCase(), jedis);
                    JedisFactory.getInstance().returnJedis(jedis);
                    DataManager.updateGuildData(shortcut.toLowerCase(), guild);
                    DataManager.updatePlayerData(uuid, playerData);
                    CcScoreboards.refreshPlayer(player);
                    CcScoreboards.refreshPlayersFor(player);
                    TabManager.refreshPlayerGuild(player);
                    PubSub.broadcastMessage(MessagesData.getMessage("zaloz.guildWasCreatedBy").replace("%fullName", name).replace("%shortCut", guild.getShortCut()).replace("%player", player.getName()));
                    player.sendMessage(MessagesData.getMessage("zaloz.claimbDescription").replace("%height", height + ""));
                    guild.rebuildBaseBlock();
                    Redis.addGuildEvent(new GuildCreateEvent(System.currentTimeMillis(), guild.getShortCut(), uuid));
                }
                else {
                    player.sendMessage(MessagesData.getMessage("zaloz.toCreateYourGuildYouNeedLotOfItems"));
                }
            }
            else {
                player.sendMessage(MessagesData.getMessage("zaloz.youAreTooCloseOtherGuild"));
            }
        }
        else {
            player.sendMessage(MessagesData.getMessage("zaloz.youAlredyHaveGuild"));
        }
    }
    
    public static ArrayList<ItemStack> getItemsForGuild() {
        final ArrayList<ItemStack> itemsForGuild = new ArrayList<ItemStack>();
        final ItemStack rawFish = new ItemStack(Material.RAW_FISH, 64);
        itemsForGuild.add(rawFish);
        final ItemStack goldBlock = new ItemStack(Material.GOLDEN_APPLE, 64);
        itemsForGuild.add(goldBlock);
        final ItemStack tnt = new ItemStack(Material.TNT, 64);
        itemsForGuild.add(tnt);
        final ItemStack greenDye = new ItemStack(Material.INK_SACK, 64, (short)10);
        itemsForGuild.add(greenDye);
        final ItemStack diamondBlock = new ItemStack(Material.DIAMOND, 64);
        itemsForGuild.add(diamondBlock);
        final ItemStack pinkGlass = new ItemStack(Material.STAINED_GLASS, 32, (short)6);
        itemsForGuild.add(pinkGlass);
        final ItemStack wheat = new ItemStack(Material.BOOKSHELF, 16);
        itemsForGuild.add(wheat);
        final ItemStack redstoneBlock = new ItemStack(Material.REDSTONE_BLOCK, 64);
        itemsForGuild.add(redstoneBlock);
        final ItemStack emeraldBlock = new ItemStack(Material.EMERALD_BLOCK, 64);
        itemsForGuild.add(emeraldBlock);
        return itemsForGuild;
    }
    
    private static boolean getFormPlayerItemsForGuild(final Player player) {
        final boolean isVip = player.hasPermission("ccGuilds.lessItemsForGuild");
        boolean toLess = false;
        for (final ItemStack item : getItemsForGuild()) {
            final ItemStack itemToCheck = item.clone();
            int amount = itemToCheck.getAmount();
            if (isVip) {
                amount /= 2;
            }
            itemToCheck.setAmount(amount);
            if (!hasItem(player.getInventory(), itemToCheck, amount)) {
                if (Config.displayPolishItemsWhenNotComplete) {
                    player.sendMessage(MessagesData.getMessage("zaloz.youAreMissingItems").replace("%count", amount + "").replace("%name", PolishItemNames.getPolishName(itemToCheck.getType(), itemToCheck.getDurability())));
                }
                toLess = true;
            }
        }
        if (toLess) {
            return false;
        }
        for (final ItemStack item : getItemsForGuild()) {
            final ItemStack itemToCheck = item.clone();
            int amount = itemToCheck.getAmount();
            if (isVip) {
                amount /= 2;
            }
            itemToCheck.setAmount(amount);
            player.getInventory().removeItem(new ItemStack[] { itemToCheck });
        }
        return true;
    }
    
    public static boolean hasItem(final PlayerInventory inv, final ItemStack item, final int requiredAmount) {
        int itemAmount = 0;
        for (final ItemStack playerItem : inv.getContents()) {
            if (playerItem != null) {
                if (playerItem.getType() == item.getType() && playerItem.getDurability() == item.getDurability()) {
                    itemAmount += playerItem.getAmount();
                }
            }
        }
        return itemAmount >= requiredAmount;
    }
}
