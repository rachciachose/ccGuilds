// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.manager;

import java.util.Random;
import java.util.ArrayList;
import pl.best241.ccguilds.messages.MessagesData;

public class MessageManager
{
    public static String getPlayerExplodeDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.explodeDeath")).replace("%player", nick);
    }
    
    public static String getPlayerContactDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.contactDeath")).replace("%player", nick);
    }
    
    public static String getPlayerDrowingDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.drowingDeath")).replace("%player", nick);
    }
    
    public static String getPlayerEntityExplodeDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.entityExplodeDeath")).replace("%player", nick);
    }
    
    public static String getPlayerFallDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.fallDeath")).replace("%player", nick);
    }
    
    public static String getPlayerFallingBlockMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.fallingBlockDeath")).replace("%player", nick);
    }
    
    public static String getPlayerFireDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.fireDeath")).replace("%player", nick);
    }
    
    public static String getPlayerLavaDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.lavaDeath")).replace("%player", nick);
    }
    
    public static String getPlayerLightingDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.lightingDeath")).replace("%player", nick);
    }
    
    public static String getPlayerPoisonDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.poisonDeath")).replace("%player", nick);
    }
    
    public static String getPlayerStarvationDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.starvationDeath")).replace("%player", nick);
    }
    
    public static String getPlayerSuicideDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.suicideDeath")).replace("%player", nick);
    }
    
    public static String getPlayerVoidDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.void")).replace("%player", nick);
    }
    
    public static String getPlayerWitherDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.wither")).replace("%player", nick);
    }
    
    private static String randomMessage(final ArrayList<String> messages) {
        return messages.get(new Random().nextInt(messages.size()));
    }
    
    public static String getPlayerEntityAttackDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.entityAttack")).replace("%player", nick);
    }
    
    public static String getPlayerProjectileDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.projectileDeath")).replace("%player", nick);
    }
    
    public static String getPlayerSuffocationDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.suffocationDeath")).replace("%player", nick);
    }
    
    public static String getPlayerMeltingDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.meltingDeath")).replace("%player", nick);
    }
    
    public static String getPlayerThornsDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.thornsDeath")).replace("%player", nick);
    }
    
    public static String getPlayerCustomDeathMessage(final String nick) {
        return randomMessage(MessagesData.getMessageList("messages.customDeath")).replace("%player", nick);
    }
}
