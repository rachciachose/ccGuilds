// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import org.bukkit.entity.HumanEntity;
import pl.best241.ccguilds.effects.EffectManager;
import org.bukkit.Material;
import pl.best241.ccguilds.effects.EffectType;
import pl.best241.ccguilds.messages.MessagesData;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import pl.best241.ccguilds.manager.DataManager;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.commands.CmdEfekty;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;

public class InventoryClickListener implements Listener
{
    @EventHandler
    public static void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity whoClicked = event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getView().getTopInventory() == event.getClickedInventory() && event.getClickedInventory().getTitle().equals(CmdEfekty.effectNameInventory)) {
            final Player player = (Player)whoClicked;
            final PlayerData playerData = DataManager.getPlayerData(whoClicked.getUniqueId());
            final GuildData guildData = playerData.getGuildData();
            final ItemStack itemClicked = event.getCurrentItem();
            event.setCancelled(true);
            event.getView().setCursor((ItemStack)null);
            event.setResult(Event.Result.DENY);
            if (guildData == null) {
                player.sendMessage(MessagesData.getMessage("efekty.toRunYouHadToBeInGuild"));
                return;
            }
            final EffectType effectType = EffectType.getEffectType(itemClicked);
            if (effectType != null) {
                final ItemStack payment = new ItemStack(Material.GOLD_INGOT, effectType.getCost());
                if (whoClicked.getInventory().containsAtLeast(payment, effectType.getCost())) {
                    whoClicked.getInventory().removeItem(new ItemStack[] { payment });
                    EffectManager.broadcastEffect(guildData.getShortCut(), effectType);
                }
                else {
                    player.sendMessage(MessagesData.getMessage("efekty.youHaveToLittleGoldToBuyIt"));
                }
            }
        }
    }
}
