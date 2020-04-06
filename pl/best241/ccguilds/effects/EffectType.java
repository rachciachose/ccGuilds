// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.effects;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.entity.Player;
import pl.best241.ccguilds.commands.CmdEfekty;
import org.bukkit.inventory.ItemStack;

public enum EffectType
{
    REGEN(110, 150), 
    INSTANT_HEAL(true, 240, -1), 
    SPEED(45, 90), 
    FIRE_RESISTANCE(140, 512), 
    HASTE(400, 300), 
    INSTANT_DAMAGE(true, 20, -1);
    
    private final boolean isInstant;
    private final int cost;
    private final int duration;
    
    private EffectType(final boolean isInstant, final int cost, final int duration) {
        this.cost = cost;
        this.isInstant = isInstant;
        this.duration = duration;
    }
    
    private EffectType(final int cost, final int duration) {
        this.cost = cost;
        this.isInstant = false;
        this.duration = duration;
    }
    
    public boolean isInstant() {
        return this.isInstant;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public int getDuration() {
        return this.duration;
    }
    
    public static EffectType getEffectType(final ItemStack item) {
        if (CmdEfekty.getFireResistancePotion().isSimilar(item)) {
            return EffectType.FIRE_RESISTANCE;
        }
        if (CmdEfekty.getHastePotion().isSimilar(item)) {
            return EffectType.HASTE;
        }
        if (CmdEfekty.getInstantDamagePotion().isSimilar(item)) {
            return EffectType.INSTANT_DAMAGE;
        }
        if (CmdEfekty.getInstantHealthPotion().isSimilar(item)) {
            return EffectType.INSTANT_HEAL;
        }
        if (CmdEfekty.getRegenPotion().isSimilar(item)) {
            return EffectType.REGEN;
        }
        if (CmdEfekty.getSpeedPotion().isSimilar(item)) {
            return EffectType.SPEED;
        }
        return null;
    }
    
    public static void applyPotion(final Player player, final EffectType type, final long timeTo) {
        System.out.println("Time to:" + timeTo + " Time now:" + System.currentTimeMillis());
        final long timeLeft = timeTo - System.currentTimeMillis();
        final int seconds = Math.round(timeLeft / 1000L);
        final int timeInTicks = seconds * 20;
        if (type == EffectType.FIRE_RESISTANCE) {
            player.addPotionEffect(new PotionEffect(PotionType.FIRE_RESISTANCE.getEffectType(), timeInTicks, 1), true);
        }
        else if (type == EffectType.HASTE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, timeInTicks, 1), true);
        }
        else if (type == EffectType.INSTANT_DAMAGE) {
            player.addPotionEffect(new PotionEffect(PotionType.INSTANT_DAMAGE.getEffectType(), 0, 2));
        }
        else if (type == EffectType.INSTANT_HEAL) {
            player.addPotionEffect(new PotionEffect(PotionType.INSTANT_HEAL.getEffectType(), 0, 2));
        }
        else if (type == EffectType.REGEN) {
            player.addPotionEffect(new PotionEffect(PotionType.REGEN.getEffectType(), timeInTicks, 1), true);
        }
        else if (type == EffectType.SPEED) {
            player.addPotionEffect(new PotionEffect(PotionType.SPEED.getEffectType(), timeInTicks, 1), true);
        }
    }
}
