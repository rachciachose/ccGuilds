// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import pl.best241.ccguilds.data.WarData;
import org.bukkit.inventory.ItemStack;
import pl.best241.ccguilds.data.GuildData;
import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.manager.MessageManager;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.best241.ccguilds.data.DamageData;
import pl.best241.ccguilds.manager.TabManager;
import pl.best241.ccguilds.manager.GuildManager;
import pl.best241.ccguilds.data.PolishItemNames;
import pl.best241.ccguilds.pubsub.PubSub;
import org.bukkit.Material;
import pl.best241.ccguilds.messages.MessagesData;
import pl.best241.ccscoreboards.CcScoreboards;
import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.manager.RankManager;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.Listener;

public class PlayerDeathListener implements Listener
{
    @EventHandler
    public static void playerDeathListener(final PlayerDeathEvent event) {
        event.setDeathMessage("");
        final Player player = event.getEntity();
        System.out.println(player.getName() + " died!");
        final EntityDamageEvent entityDamageEvent = player.getLastDamageCause();
        if (entityDamageEvent instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent damageByEntity = (EntityDamageByEntityEvent)entityDamageEvent;
            if (damageByEntity.getDamager() instanceof Player || damageByEntity.getDamager() instanceof Arrow) {
                Player damager = null;
                if (damageByEntity.getDamager() instanceof Player) {
                    damager = (Player)damageByEntity.getDamager();
                }
                else if (damageByEntity.getDamager() instanceof Arrow) {
                    final Arrow arrow = (Arrow)damageByEntity.getDamager();
                    if (arrow.getShooter() instanceof Player) {
                        damager = (Player)arrow.getShooter();
                    }
                }
                if (damager != null) {
                    final int calculatedRank = RankManager.calculateRank(player, damager);
                    String points;
                    if (calculatedRank > 0) {
                        points = "+" + calculatedRank;
                    }
                    else {
                        points = calculatedRank + "";
                    }
                    final UUID playerUUID = player.getUniqueId();
                    final UUID damagerUUID = damager.getUniqueId();
                    final PlayerData playerData = DataManager.getPlayerData(playerUUID);
                    final PlayerData damagerData = DataManager.getPlayerData(damagerUUID);
                    CcScoreboards.refreshPlayer(damager);
                    CcScoreboards.refreshPlayer(player);
                    final GuildData playerGuild = playerData.getGuildData();
                    final GuildData damagerGuild = damagerData.getGuildData();
                    String victimGuildFormat = "";
                    if (playerGuild != null) {
                        victimGuildFormat = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", playerGuild.getShortCut());
                    }
                    String attackerGuildFormat = "";
                    if (damagerGuild != null) {
                        attackerGuildFormat = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", damagerGuild.getShortCut());
                    }
                    final ItemStack weapon = damager.getItemInHand();
                    if (weapon == null || weapon.getType() == Material.AIR) {
                        PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager.getName()).replace("%points", points).replace("%victimGuildFormat", victimGuildFormat).replace("%damagerGuildFormat", attackerGuildFormat));
                    }
                    else if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName()) {
                        final String name = PolishItemNames.getPolishName(weapon.getType());
                        if (name != null) {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayerUsing").replace("%player", player.getName()).replace("%target", damager.getName()).replace("%points", points).replace("%itemName", name).replace("%victimGuildFormat", victimGuildFormat).replace("%damagerGuildFormat", attackerGuildFormat));
                        }
                        else {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager.getName()).replace("%points", points).replace("%victimGuildFormat", victimGuildFormat).replace("%damagerGuildFormat", attackerGuildFormat));
                        }
                    }
                    else {
                        final String name = PolishItemNames.getPolishName(weapon.getType());
                        if (name != null) {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayerUsing").replace("%player", player.getName()).replace("%target", damager.getName()).replace("%points", points).replace("%itemName", name).replace("%victimGuildFormat", victimGuildFormat).replace("%damagerGuildFormat", attackerGuildFormat));
                        }
                        else {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager.getName()).replace("%points", points).replace("%victimGuildFormat", victimGuildFormat).replace("%damagerGuildFormat", attackerGuildFormat));
                        }
                    }
                    CcScoreboards.refreshPlayer(damager);
                    CcScoreboards.refreshPlayer(player);
                    CcScoreboards.refreshPlayerScore(player);
                    CcScoreboards.refreshPlayerScore(damager);
                    if (damagerGuild != null && playerGuild != null && damagerGuild.isInWar(playerGuild)) {
                        final WarData damagerWar = damagerGuild.getWar(playerGuild.getShortCut());
                        if (damagerWar.isAgressor(playerGuild.getShortCut())) {
                            damagerWar.setTargetKills(damagerWar.getTargetKills() + 1);
                        }
                        else {
                            damagerWar.setChallengingKills(damagerWar.getChallengingKills() + 1);
                        }
                        damagerGuild.addWar(damagerWar);
                        final WarData playerWar = playerGuild.getWar(damagerGuild.getShortCut());
                        if (playerWar.isAgressor(damagerGuild.getShortCut())) {
                            playerWar.setTargetKills(playerWar.getTargetKills() + 1);
                        }
                        else {
                            playerWar.setChallengingKills(playerWar.getChallengingKills() + 1);
                        }
                    }
                    GuildManager.calculateGuildRank(playerGuild, damagerGuild);
                    TabManager.refreshPlayerStats(player);
                    TabManager.refreshPlayerStats(damager);
                }
            }
            else if (EntityDamageByEntityListener.antiRelog.containsKey(player.getUniqueId()) && EntityDamageByEntityListener.antiRelog.get(player.getUniqueId()).time + 6000L >= System.currentTimeMillis()) {
                final DamageData damage = EntityDamageByEntityListener.antiRelog.get(player.getUniqueId());
                final Entity damagerEntity = damage.damager;
                if (damagerEntity instanceof Player) {
                    final Player damager2 = (Player)damagerEntity;
                    if (damager2 != null) {
                        final int calculatedRank2 = RankManager.calculateRank(player, damager2);
                        String points2;
                        if (calculatedRank2 > 0) {
                            points2 = "+" + calculatedRank2;
                        }
                        else {
                            points2 = calculatedRank2 + "";
                        }
                        final UUID playerUUID2 = player.getUniqueId();
                        final UUID damagerUUID2 = damager2.getUniqueId();
                        final PlayerData playerData2 = DataManager.getPlayerData(playerUUID2);
                        final PlayerData damagerData2 = DataManager.getPlayerData(damagerUUID2);
                        CcScoreboards.refreshPlayer(damager2);
                        CcScoreboards.refreshPlayer(player);
                        final GuildData playerGuild2 = playerData2.getGuildData();
                        final GuildData damagerGuild2 = damagerData2.getGuildData();
                        String victimGuildFormat2 = "";
                        if (playerGuild2 != null) {
                            victimGuildFormat2 = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", playerGuild2.getShortCut());
                        }
                        String attackerGuildFormat2 = "";
                        if (damagerGuild2 != null) {
                            attackerGuildFormat2 = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", damagerGuild2.getShortCut());
                        }
                        final ItemStack weapon2 = damager2.getItemInHand();
                        if (weapon2 == null || weapon2.getType() == Material.AIR) {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager2.getName()).replace("%points", points2).replace("%victimGuildFormat", victimGuildFormat2).replace("%damagerGuildFormat", attackerGuildFormat2));
                        }
                        else if (weapon2.hasItemMeta() && weapon2.getItemMeta().hasDisplayName()) {
                            final String name2 = PolishItemNames.getPolishName(weapon2.getType());
                            if (name2 != null) {
                                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayerUsing").replace("%player", player.getName()).replace("%target", damager2.getName()).replace("%points", points2).replace("%itemName", name2).replace("%victimGuildFormat", victimGuildFormat2).replace("%damagerGuildFormat", attackerGuildFormat2));
                            }
                            else {
                                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager2.getName()).replace("%points", points2).replace("%victimGuildFormat", victimGuildFormat2).replace("%damagerGuildFormat", attackerGuildFormat2));
                            }
                        }
                        else {
                            final String name2 = PolishItemNames.getPolishName(weapon2.getType());
                            if (name2 != null) {
                                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayerUsing").replace("%player", player.getName()).replace("%target", damager2.getName()).replace("%points", points2).replace("%itemName", name2).replace("%victimGuildFormat", victimGuildFormat2).replace("%damagerGuildFormat", attackerGuildFormat2));
                            }
                            else {
                                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager2.getName()).replace("%points", points2).replace("%victimGuildFormat", victimGuildFormat2).replace("%damagerGuildFormat", attackerGuildFormat2));
                            }
                        }
                        CcScoreboards.refreshPlayer(damager2);
                        CcScoreboards.refreshPlayer(player);
                        CcScoreboards.refreshPlayerScore(player);
                        CcScoreboards.refreshPlayerScore(damager2);
                        if (playerGuild2 != null && damagerGuild2 != null && !playerGuild2.equals(damagerGuild2)) {
                            if (playerGuild2.isInWar(damagerGuild2)) {
                                final WarData playerToDamagerWar = playerGuild2.getWar(damagerGuild2.getShortCut());
                                final WarData damagerToPlayerWar = damagerGuild2.getWar(playerGuild2.getShortCut());
                                playerToDamagerWar.setTargetKills(playerToDamagerWar.getTargetKills() + 1);
                                damagerToPlayerWar.setChallengingKills(damagerToPlayerWar.getChallengingKills() + 1);
                                playerGuild2.remWar(damagerGuild2.getWar(playerGuild2.getShortCut()));
                                playerGuild2.addWar(playerToDamagerWar);
                                damagerGuild2.remWar(damagerGuild2.getWar(playerGuild2.getShortCut()));
                                damagerGuild2.addWar(damagerToPlayerWar);
                            }
                            GuildManager.calculateGuildRank(playerGuild2, damagerGuild2);
                        }
                        TabManager.refreshPlayerStats(player);
                        TabManager.refreshPlayerStats(damager2);
                    }
                }
            }
            else {
                String mob = damageByEntity.getDamager().getType().toString();
                final String mobTranslated = MessagesData.getMessage("playerDeathEvent.mobTranslated." + mob);
                if (mobTranslated != null) {
                    mob = mobTranslated;
                }
                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByMob").replace("%player", player.getName()).replace("%mob", mob));
            }
        }
        else if (EntityDamageByEntityListener.antiRelog.containsKey(player.getUniqueId()) && EntityDamageByEntityListener.antiRelog.get(player.getUniqueId()).time + 6000L >= System.currentTimeMillis()) {
            final DamageData damage2 = EntityDamageByEntityListener.antiRelog.get(player.getUniqueId());
            final Entity damagerEntity2 = damage2.damager;
            if (damagerEntity2 instanceof Player) {
                final Player damager3 = (Player)damagerEntity2;
                if (damager3 != null) {
                    final UUID playerUUID3 = DataManager.getPlayerUUID(player);
                    final UUID damagerUUID3 = DataManager.getPlayerUUID(damager3);
                    final PlayerData playerData3 = DataManager.getPlayerData(playerUUID3);
                    final PlayerData damagerData3 = DataManager.getPlayerData(damagerUUID3);
                    final GuildData playerGuild3 = playerData3.getGuildData();
                    final GuildData damagerGuild3 = damagerData3.getGuildData();
                    String victimGuildFormat3 = "";
                    if (playerGuild3 != null) {
                        victimGuildFormat3 = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", playerGuild3.getShortCut());
                    }
                    String attackerGuildFormat3 = "";
                    if (damagerGuild3 != null) {
                        attackerGuildFormat3 = MessagesData.getMessage("playerDeathEvent.guildFormat").replace("%guild", damagerGuild3.getShortCut());
                    }
                    final int calculatedRank3 = RankManager.calculateRank(player, damager3);
                    String points3;
                    if (calculatedRank3 > 0) {
                        points3 = "+" + calculatedRank3;
                    }
                    else {
                        points3 = calculatedRank3 + "";
                    }
                    final ItemStack weapon3 = damager3.getItemInHand();
                    if (weapon3 == null || weapon3.getType() == Material.AIR) {
                        PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager3.getName()).replace("%points", points3).replace("%victimGuildFormat", victimGuildFormat3).replace("%damagerGuildFormat", attackerGuildFormat3));
                    }
                    else if (weapon3.hasItemMeta() && weapon3.getItemMeta().hasDisplayName()) {
                        final String name3 = PolishItemNames.getPolishName(weapon3.getType());
                        if (name3 != null) {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayerUsing").replace("%player", player.getName()).replace("%target", damager3.getName()).replace("%points", points3).replace("%itemName", name3).replace("%victimGuildFormat", victimGuildFormat3).replace("%damagerGuildFormat", attackerGuildFormat3));
                        }
                        else {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager3.getName()).replace("%points", points3).replace("%victimGuildFormat", victimGuildFormat3).replace("%damagerGuildFormat", attackerGuildFormat3));
                        }
                    }
                    else {
                        final String name3 = PolishItemNames.getPolishName(weapon3.getType());
                        if (name3 != null) {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayerUsing").replace("%player", player.getName()).replace("%target", damager3.getName()).replace("%points", points3).replace("%itemName", name3).replace("%victimGuildFormat", victimGuildFormat3).replace("%damagerGuildFormat", attackerGuildFormat3));
                        }
                        else {
                            PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByPlayer").replace("%player", player.getName()).replace("%target", damager3.getName()).replace("%points", points3).replace("%victimGuildFormat", victimGuildFormat3).replace("%damagerGuildFormat", attackerGuildFormat3));
                        }
                    }
                    CcScoreboards.refreshPlayer(damager3);
                    CcScoreboards.refreshPlayer(player);
                    CcScoreboards.refreshPlayerScore(player);
                    CcScoreboards.refreshPlayerScore(damager3);
                    if (playerGuild3 != null && damagerGuild3 != null && !playerGuild3.equals(damagerGuild3)) {
                        if (playerGuild3.isInWar(damagerGuild3)) {
                            final WarData playerToDamagerWar2 = playerGuild3.getWar(damagerGuild3.getShortCut());
                            final WarData damagerToPlayerWar2 = damagerGuild3.getWar(playerGuild3.getShortCut());
                            playerToDamagerWar2.setTargetKills(playerToDamagerWar2.getTargetKills() + 1);
                            damagerToPlayerWar2.setChallengingKills(damagerToPlayerWar2.getChallengingKills() + 1);
                        }
                        GuildManager.calculateGuildRank(playerGuild3, damagerGuild3);
                    }
                }
            }
            else {
                String mob2 = damagerEntity2.getType().toString();
                final String mobTranslated2 = MessagesData.getMessage("playerDeathEvent.mobTranslated." + mob2);
                if (mobTranslated2 != null) {
                    mob2 = mobTranslated2;
                }
                PubSub.broadcastMessage(MessagesData.getMessage("playerDeathEvent.playerWasKilledByMob").replace("%player", player.getName()).replace("%mob", mob2));
            }
        }
        else {
            if (entityDamageEvent == null) {
                return;
            }
            final EntityDamageEvent.DamageCause cause = entityDamageEvent.getCause();
            if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
                PubSub.broadcastMessage(MessageManager.getPlayerExplodeDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.CONTACT) {
                PubSub.broadcastMessage(MessageManager.getPlayerContactDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
                PubSub.broadcastMessage(MessageManager.getPlayerDrowingDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                PubSub.broadcastMessage(MessageManager.getPlayerEntityExplodeDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.FALL) {
                PubSub.broadcastMessage(MessageManager.getPlayerFallDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
                PubSub.broadcastMessage(MessageManager.getPlayerFallingBlockMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                PubSub.broadcastMessage(MessageManager.getPlayerFireDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.LAVA) {
                PubSub.broadcastMessage(MessageManager.getPlayerLavaDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.LIGHTNING) {
                PubSub.broadcastMessage(MessageManager.getPlayerLightingDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.POISON) {
                PubSub.broadcastMessage(MessageManager.getPlayerPoisonDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.STARVATION) {
                PubSub.broadcastMessage(MessageManager.getPlayerStarvationDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.SUICIDE) {
                PubSub.broadcastMessage(MessageManager.getPlayerSuicideDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.VOID) {
                PubSub.broadcastMessage(MessageManager.getPlayerVoidDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.WITHER) {
                PubSub.broadcastMessage(MessageManager.getPlayerWitherDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                PubSub.broadcastMessage(MessageManager.getPlayerEntityAttackDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                PubSub.broadcastMessage(MessageManager.getPlayerProjectileDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                PubSub.broadcastMessage(MessageManager.getPlayerSuffocationDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.MELTING) {
                PubSub.broadcastMessage(MessageManager.getPlayerMeltingDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.THORNS) {
                PubSub.broadcastMessage(MessageManager.getPlayerThornsDeathMessage(player.getName()));
            }
            else if (cause == EntityDamageEvent.DamageCause.CUSTOM) {
                PubSub.broadcastMessage(MessageManager.getPlayerCustomDeathMessage(player.getName()));
            }
            else {
                PubSub.broadcastMessage(player.getName() + " died by " + cause.toString());
            }
        }
        EntityDamageByEntityListener.antiRelog.remove(player.getUniqueId());
    }
}
