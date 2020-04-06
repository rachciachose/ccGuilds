// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.data;

import org.bukkit.entity.Entity;

public class DamageData
{
    public long time;
    public long logoutBaseTime;
    public Entity damager;
    
    public DamageData(final Entity damager, final long logoutBaseTime) {
        this.time = System.currentTimeMillis();
        this.damager = damager;
        this.logoutBaseTime = logoutBaseTime;
    }
}
