package com.lw.random_additions.proxy;

import com.lw.random_additions.common.init.Mods;
import com.lw.random_additions.common.integration.ae2.patternupload.PatternUploadTargetInfo;
import com.lw.random_additions.common.integration.thaumcraft.InfusionInterceptorRecipe;
import com.lw.random_additions.common.integration.tconstruct.ModRemoveInscription;
import com.lw.random_additions.misc.BlockRegister;
import com.lw.random_additions.misc.ItemRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.List;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ItemRegister());
        MinecraftForge.EVENT_BUS.register(new BlockRegister());
        if (Mods.THAUMCRAFT.isLoaded()) {
            BlockRegister.init();
            BlockRegister.registerTileEntity();
        }
    }

    public void init(FMLInitializationEvent event) {
    }

    public void postInit(FMLPostInitializationEvent event) {
        if (Mods.THAUMCRAFT.isLoaded()) {
            InfusionInterceptorRecipe.register();
        }
        registerTConstructModifiers();
    }

    public void handlePatternUploadTargets(List<PatternUploadTargetInfo> targets) {
    }

    private void registerTConstructModifiers() {
        if (Mods.TC.isLoaded()) {
            ModRemoveInscription modifier = new ModRemoveInscription();
            modifier.addRecipeItem(new ItemStack(Blocks.DIAMOND_BLOCK));
        }
    }
}
