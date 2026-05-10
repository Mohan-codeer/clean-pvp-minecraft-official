package com.cleanpvp;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CleanPVP.MODID, version = CleanPVP.VERSION, clientSideOnly = true)
public class CleanPVP {
    public static final String MODID = "cleanpvp";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Nothing here for now
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        HudManager.initialize();
        ThemeManager.initialize();
        System.out.println("[CleanPVP] Initialized Forge 1.8.9 version");
    }
}
