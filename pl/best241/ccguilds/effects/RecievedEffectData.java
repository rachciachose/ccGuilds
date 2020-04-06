// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.effects;

import pl.best241.ccguilds.manager.DataManager;
import pl.best241.ccguilds.data.GuildData;

public class RecievedEffectData
{
    private final EffectType effectType;
    private final GuildData guildData;
    private final long timeStarted;
    
    public RecievedEffectData(final String shortCut, final EffectType type) {
        this.effectType = type;
        this.guildData = DataManager.getGuildData(shortCut);
        this.timeStarted = System.currentTimeMillis();
    }
    
    public EffectType getEffectType() {
        return this.effectType;
    }
    
    public GuildData getGuild() {
        return this.guildData;
    }
    
    public long getTimeStarted() {
        return this.timeStarted;
    }
}
