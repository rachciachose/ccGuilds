// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import org.bukkit.plugin.Plugin;
import pl.best241.ccguilds.CcGuilds;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import pl.best241.ccguilds.logblock.data.actions.Action;
import pl.best241.ccguilds.logblock.data.LoggingManager;
import pl.best241.ccguilds.logblock.data.actions.ChestAction;
import pl.best241.ccguilds.logblock.data.SimpleLocation;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.BlockState;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.HumanEntity;
import java.util.Map;
import org.bukkit.event.Listener;

public class ChestLogger implements Listener
{
    private static final Map<HumanEntity, ItemStack[]> containers;
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof BlockState || holder instanceof DoubleChest) {
            final HumanEntity player = event.getPlayer();
            final ItemStack[] before = ChestLogger.containers.get(player);
            if (before != null) {
                final ItemStack[] after = compressInventory(event.getInventory().getContents());
                final ItemStack[] diff = compareInventories(before, after);
                final Location loc = getInventoryHolderLocation(holder);
                for (final ItemStack item : diff) {
                    final ChestAction chestAction = new ChestAction(new SimpleLocation(loc), player.getUniqueId(), player.getName(), item.getType(), rawData(item), item.getAmount());
                    LoggingManager.addToQueue(chestAction);
                }
                ChestLogger.containers.remove(player);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory() != null) {
            final InventoryHolder holder = event.getInventory().getHolder();
            if ((holder instanceof BlockState || holder instanceof DoubleChest) && getInventoryHolderType(holder) != 58) {
                ChestLogger.containers.put(event.getPlayer(), compressInventory(event.getInventory().getContents()));
            }
        }
    }
    
    public static void enableLogger() {
        Bukkit.getPluginManager().registerEvents((Listener)new ChestLogger(), (Plugin)CcGuilds.getPlugin());
    }
    
    private static int getInventoryHolderType(final InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return ((DoubleChest)holder).getLocation().getBlock().getTypeId();
        }
        if (holder instanceof BlockState) {
            return ((BlockState)holder).getTypeId();
        }
        return -1;
    }
    
    private static ItemStack[] compressInventory(final ItemStack[] items) {
        final ArrayList<ItemStack> compressed = new ArrayList<ItemStack>();
        for (final ItemStack item : items) {
            if (item != null) {
                final int type = item.getTypeId();
                final short data = rawData(item);
                boolean found = false;
                for (final ItemStack item2 : compressed) {
                    if (type == item2.getTypeId() && data == rawData(item2)) {
                        item2.setAmount(item2.getAmount() + item.getAmount());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    compressed.add(new ItemStack(type, item.getAmount(), data));
                }
            }
        }
        Collections.sort(compressed, new ItemStackComparator());
        return compressed.toArray(new ItemStack[compressed.size()]);
    }
    
    private static short rawData(final ItemStack item) {
        return (short)((item.getType() != null) ? ((item.getData() != null) ? item.getDurability() : 0) : 0);
    }
    
    private static ItemStack[] compareInventories(final ItemStack[] items1, final ItemStack[] items2) {
        final ItemStackComparator comperator = new ItemStackComparator();
        final ArrayList<ItemStack> diff = new ArrayList<ItemStack>();
        final int l1 = items1.length;
        final int l2 = items2.length;
        int c1 = 0;
        int c2 = 0;
        while (c1 < l1 || c2 < l2) {
            if (c1 >= l1) {
                diff.add(items2[c2]);
                ++c2;
            }
            else if (c2 >= l2) {
                items1[c1].setAmount(items1[c1].getAmount() * -1);
                diff.add(items1[c1]);
                ++c1;
            }
            else {
                final int comp = comperator.compare(items1[c1], items2[c2]);
                if (comp < 0) {
                    items1[c1].setAmount(items1[c1].getAmount() * -1);
                    diff.add(items1[c1]);
                    ++c1;
                }
                else if (comp > 0) {
                    diff.add(items2[c2]);
                    ++c2;
                }
                else {
                    final int amount = items2[c2].getAmount() - items1[c1].getAmount();
                    if (amount != 0) {
                        items1[c1].setAmount(amount);
                        diff.add(items1[c1]);
                    }
                    ++c1;
                    ++c2;
                }
            }
        }
        return diff.toArray(new ItemStack[diff.size()]);
    }
    
    private static Location getInventoryHolderLocation(final InventoryHolder holder) {
        if (holder == null) {
            return null;
        }
        if (holder instanceof DoubleChest) {
            return ((DoubleChest)holder).getLocation();
        }
        if (holder instanceof BlockState) {
            return ((BlockState)holder).getLocation();
        }
        return null;
    }
    
    static {
        containers = new HashMap<HumanEntity, ItemStack[]>();
    }
    
    private static class ItemStackComparator implements Comparator<ItemStack>
    {
        @Override
        public int compare(final ItemStack a, final ItemStack b) {
            final int aType = a.getTypeId();
            final int bType = b.getTypeId();
            if (aType < bType) {
                return -1;
            }
            if (aType > bType) {
                return 1;
            }
            final short aData = rawData(a);
            final short bData = rawData(b);
            if (aData < bData) {
                return -1;
            }
            if (aData > bData) {
                return 1;
            }
            return 0;
        }
    }
}
