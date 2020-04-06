// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import org.bukkit.entity.Player;

public class RankManager
{
    public static int calculateRank(final Player victim, final Player damager) {
        final UUID victimUUID = DataManager.getPlayerUUID(victim);
        final UUID damagerUUID = DataManager.getPlayerUUID(damager);
        final PlayerData victimData = DataManager.getPlayerData(victimUUID);
        final PlayerData damagerData = DataManager.getPlayerData(damagerUUID);
        victimData.addPlayerKill(damagerUUID);
        damagerData.addPlayerVictim(victimUUID);
        int victimRank = victimData.getRank();
        int damagerRank = damagerData.getRank();
        final int x = victimRank - damagerRank + 300;
        int damager_reward = 0;
        int victim_reward = 0;
        if (x >= 0) {
            damager_reward = (int)(Math.round(Math.pow(x / 20, 0.95)) + 30L);
            victim_reward = -(int)(Math.round(Math.pow(x / 20, 0.9)) + 20L);
        }
        else {
            damager_reward = (int)(-Math.round(Math.pow(Math.abs(x / 34), 0.59))) + 3;
            victim_reward = (int)(-Math.round(Math.pow(Math.abs(x / 22), 0.46)));
        }
        damagerRank += damager_reward;
        victimRank += victim_reward;
        victimData.setRank(victimRank);
        damagerData.setRank(damagerRank);
        victimData.setDeaths(victimData.getDeaths() + 1);
        damagerData.setKills(damagerData.getKills() + 1);
        victimData.setNeedSave(true);
        damagerData.setNeedSave(true);
        DataManager.updatePlayerData(victimUUID, victimData);
        DataManager.updatePlayerData(damagerUUID, damagerData);
        return damager_reward;
    }
}
