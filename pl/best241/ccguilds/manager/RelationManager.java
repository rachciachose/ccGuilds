// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import pl.best241.ccguilds.data.PlayerData;
import java.util.UUID;
import pl.best241.ccguilds.data.Relation;
import org.bukkit.entity.Player;

public class RelationManager
{
    public static Relation getRelation(final Player player, final Player namedPlayer) {
        final UUID playerUUID = DataManager.getPlayerUUID(player);
        final UUID namedUUID = DataManager.getPlayerUUID(namedPlayer);
        final PlayerData playerData = DataManager.getPlayerData(playerUUID);
        final PlayerData namedData = DataManager.getPlayerData(namedUUID);
        if (playerData.getGuildName() == null) {
            return Relation.WROG;
        }
        if (namedData.getGuildName() == null) {
            return Relation.INNY;
        }
        if (playerData.getGuildName().equals(namedData.getGuildName())) {
            return Relation.CZLONEK;
        }
        if (!playerData.getGuildName().equals(namedData.getGuildName())) {
            return Relation.WROG;
        }
        return Relation.INNY;
    }
}
