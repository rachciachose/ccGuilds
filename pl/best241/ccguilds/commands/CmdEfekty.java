// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.commands;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import pl.best241.ccguilds.effects.EffectType;
import pl.best241.ccguilds.messages.MessagesData;
import java.util.ArrayList;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdEfekty
{
    public static final String effectNameInventory;
    
    public static void efekty(final Player player) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)player, 9, CmdEfekty.effectNameInventory);
        inv.setItem(0, getRegenPotion());
        inv.setItem(3, getSpeedPotion());
        inv.setItem(4, getFireResistancePotion());
        inv.setItem(6, getHastePotion());
        player.openInventory(inv);
    }
    
    public static ItemStack getRegenPotion() {
        final ItemStack potionItem = new Potion(PotionType.REGEN).toItemStack(1);
        final ItemMeta meta = potionItem.getItemMeta();
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(MessagesData.getMessage("efekty.durationLoreName").replace("%time", EffectType.REGEN.getDuration() + "s"));
        lore.add(MessagesData.getMessage("efekty.costLoreName").replace("%cost", EffectType.REGEN.getCost() + ""));
        meta.setLore((List)lore);
        potionItem.setItemMeta(meta);
        return potionItem;
    }
    
    public static ItemStack getHastePotion() {
        final ItemStack item = new ItemStack(Material.GOLD_PICKAXE);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Haste II");
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(MessagesData.getMessage("efekty.durationLoreName").replace("%time", EffectType.HASTE.getDuration() + "s"));
        lore.add(MessagesData.getMessage("efekty.costLoreName").replace("%cost", EffectType.HASTE.getCost() + ""));
        meta.setLore((List)lore);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack getSpeedPotion() {
        final ItemStack potionItem = new Potion(PotionType.SPEED).toItemStack(1);
        final ItemMeta meta = potionItem.getItemMeta();
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(MessagesData.getMessage("efekty.durationLoreName").replace("%time", EffectType.SPEED.getDuration() + "s"));
        lore.add(MessagesData.getMessage("efekty.costLoreName").replace("%cost", EffectType.SPEED.getCost() + ""));
        meta.setLore((List)lore);
        potionItem.setItemMeta(meta);
        return potionItem;
    }
    
    public static ItemStack getInstantHealthPotion() {
        final Potion potion = new Potion(PotionType.INSTANT_HEAL);
        potion.setSplash(true);
        final ItemStack potionItem = potion.toItemStack(1);
        final ItemMeta meta = potionItem.getItemMeta();
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(MessagesData.getMessage("efekty.costLoreName").replace("%cost", EffectType.INSTANT_HEAL.getCost() + ""));
        meta.setLore((List)lore);
        potionItem.setItemMeta(meta);
        return potionItem;
    }
    
    public static ItemStack getFireResistancePotion() {
        final ItemStack potionItem = new Potion(PotionType.FIRE_RESISTANCE).toItemStack(1);
        final ItemMeta meta = potionItem.getItemMeta();
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(MessagesData.getMessage("efekty.durationLoreName").replace("%time", EffectType.FIRE_RESISTANCE.getDuration() + "s"));
        lore.add(MessagesData.getMessage("efekty.costLoreName").replace("%cost", EffectType.FIRE_RESISTANCE.getCost() + ""));
        meta.setLore((List)lore);
        potionItem.setItemMeta(meta);
        return potionItem;
    }
    
    public static ItemStack getInstantDamagePotion() {
        final ItemStack potionItem = new Potion(PotionType.INSTANT_DAMAGE).toItemStack(1);
        final ItemMeta meta = potionItem.getItemMeta();
        final ArrayList<String> lore = new ArrayList<String>();
        lore.add(MessagesData.getMessage("efekty.costLoreName").replace("%cost", EffectType.INSTANT_DAMAGE.getCost() + ""));
        meta.setLore((List)lore);
        potionItem.setItemMeta(meta);
        return potionItem;
    }
    
    static {
        effectNameInventory = MessagesData.getMessage("efekty.inventoryName");
    }
}
