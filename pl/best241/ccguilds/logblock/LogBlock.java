// 
// Decompiled by Procyon v0.5.30
// 

package pl.best241.ccguilds.logblock;

import pl.best241.ccguilds.logblock.listeners.LogBlockCheckListener;
import pl.best241.ccguilds.logblock.listeners.ChestLogger;
import pl.best241.ccguilds.logblock.listeners.BlockPlaceLogger;
import pl.best241.ccguilds.logblock.listeners.BlockBreakLogger;
import pl.best241.ccguilds.logblock.data.LoggingManager;

public class LogBlock
{
    public static void enableAll() {
        LoggingManager.runTimer();
        BlockBreakLogger.enableLogger();
        BlockPlaceLogger.enableLogger();
        ChestLogger.enableLogger();
        LogBlockCheckListener.enableLogBlockCheckListener();
    }
}
