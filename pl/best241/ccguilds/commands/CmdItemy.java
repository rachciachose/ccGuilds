// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.ArrayList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CmdItemy implements Listener
{
    public static String itemsForGuildsCalculatorName;
    
    public static void itemy(final Player player) {
        double multiplier = 1.0;
        if (player.hasPermission("ccGuilds.lessItemsForGuild")) {
            multiplier = 0.5;
        }
        final int fields = (int)Math.ceil(CmdZaloz.getItemsForGuild().size() / 9.0) * 9;
        final Inventory inv = Bukkit.createInventory((InventoryHolder)player, fields, CmdItemy.itemsForGuildsCalculatorName);
        for (int i = 0; i < CmdZaloz.getItemsForGuild().size(); ++i) {
            final ItemStack item = CmdZaloz.getItemsForGuild().get(i).clone();
            final int numberNeeded = (int)(multiplier * item.getAmount());
            final int numberInEq = calculateItems(item, (Inventory)player.getInventory());
            final double percentInEq = Math.round(numberInEq / numberNeeded * 100.0 * 100.0) / 100L;
            final ArrayList<String> lore = new ArrayList<String>();
            lore.add(MessagesData.getMessage("itemy.youNeed").replace("%needed", numberNeeded + ""));
            lore.add(MessagesData.getMessage("itemy.youHave").replace("%number", numberInEq + ""));
            if (numberNeeded - numberInEq > 0) {
                lore.add(MessagesData.getMessage("itemy.youAreMissingItems").replace("%items", numberNeeded - numberInEq + ""));
            }
            else {
                lore.add(MessagesData.getMessage("itemy.youCompletedItems"));
            }
            lore.add(MessagesData.getMessage("itemy.itemsState").replace("%percent", percentInEq + ""));
            if (player.hasPermission("ccGuilds.lessItemsForGuild")) {
                lore.add(MessagesData.getMessage("itemy.youAreVipYouNeedLessItems"));
            }
            else {
                lore.add(MessagesData.getMessage("itemy.vipNeedLessItems"));
            }
            final ItemMeta meta = item.getItemMeta();
            meta.setLore((List)lore);
            item.setItemMeta(meta);
            item.setAmount(numberInEq);
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }
    
    public static int calculateItems(final ItemStack item, final Inventory inv) {
        int number = 0;
        for (final ItemStack itemInInv : inv.getContents()) {
            if (itemInInv != null && itemInInv.isSimilar(item)) {
                number += itemInInv.getAmount();
            }
        }
        return number;
    }
    
    @EventHandler
    public static void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getView().getTopInventory() == event.getClickedInventory()) {
            if (event.getClickedInventory().getTitle() == null) {
                if (CmdItemy.itemsForGuildsCalculatorName != null) {
                    return;
                }
            }
            else if (!event.getClickedInventory().getTitle().equals(CmdItemy.itemsForGuildsCalculatorName)) {
                return;
            }
            event.setCancelled(true);
            event.getView().setCursor((ItemStack)null);
            event.setResult(Event.Result.DENY);
        }
    }
    
    static {
        CmdItemy.itemsForGuildsCalculatorName = MessagesData.getMessage("itemy.inventoryName");
    }
}
