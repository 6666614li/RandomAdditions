package com.lw.random_additions.proxy;

import com.lw.random_additions.Tags;
import com.lw.random_additions.client.PatternUploadClient;
import com.lw.random_additions.client.handler.KeyHandler;
import com.lw.random_additions.common.init.Mods;
import com.lw.random_additions.common.integration.ae2.patternupload.PatternUploadTargetInfo;
import com.lw.random_additions.common.integration.tconstruct.ModRemoveInscription;
import com.lw.random_additions.common.integration.top.TheOneProbeCompat;
import com.lw.random_additions.misc.BlockRegister;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Objects;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        KeyHandler.init();
        TheOneProbeCompat.register();
        MinecraftForge.EVENT_BUS.register(new KeyHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        if(Mods.TC.isLoaded()){
            ModRemoveInscription.addTConstructBookEntry();
        }
    }

    @Override
    public void handlePatternUploadTargets(List<PatternUploadTargetInfo> targets) {
        Minecraft.getMinecraft().addScheduledTask(() -> PatternUploadClient.showTargets(targets));
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MOD_ID)
    public static class ModelRegistration {
        @SubscribeEvent
        public static void registerBlockModels(ModelRegistryEvent event) {
            if (Mods.THAUMCRAFT.isLoaded()){
                registerBlockModel(BlockRegister.INFUSION_INTERCEPTOR);
            }

        }
        @SubscribeEvent
        public static void registerItemModels(ModelRegistryEvent event){
            if (Mods.THAUMCRAFT.isLoaded()){
                registerItemBlockModel(BlockRegister.INFUSION_INTERCEPTOR);
            }

        }

        private static void registerItemModel(Item item) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        }

        private static void registerItemBlockModel(Block block) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory"));
        }

        private static void registerBlockModel(Block block) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory"));
        }
    }
}
