package com.lw.random_additons.config;

import com.lw.random_additons.Tags;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Tags.MOD_ID , name = "RandomAdditons/RandomAdditonsConfig")
@Config.LangKey("RandomAdditons.config.title")
public class RandomAdditonsConfig {

    @Config.Comment({
            "Items that cannot be dissolved in the crucible",
            "Format: modid:itemid",
            "Example: minecraft:dirt"
    })
    @Config.Name("crucible_whitelist")
    @Config.RequiresMcRestart
    public static String[] crucibleWhitelist = {
            "minecraft:dirt",
            "minecraft:grass"
    };

    public static boolean crucibleWhitelist(ItemStack item) {
        for (String itemId : crucibleWhitelist) {
            if (item.getItem().getRegistryName().toString().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    @Mod.EventBusSubscriber(modid = Tags.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Tags.MOD_ID)) {
                ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
