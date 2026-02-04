package com.lw.random_additons;

import com.lw.random_additons.common.handler.KeyHandler;
import com.lw.random_additons.common.handler.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class RandomAdditons {

    public static String MOD_ID = "RandomAdditons";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        NetworkHandler.registerPackets();

        if (event.getSide() == Side.CLIENT) {
            KeyHandler.init();
            MinecraftForge.EVENT_BUS.register(new KeyHandler());
        }
    }
}
