package com.lw.random_additions.misc;

import com.lw.random_additions.Tags;
import com.lw.random_additions.common.block.thaumcraft.BlockInfusionInterceptor;
import com.lw.random_additions.common.init.Mods;
import com.lw.random_additions.common.tile.thaumcraft.TileInfusionInterceptor;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Objects;

public final class BlockRegister {

    public static BlockInfusionInterceptor INFUSION_INTERCEPTOR;

    public static void init() {
        INFUSION_INTERCEPTOR = new BlockInfusionInterceptor();
    }

    public static void registerTileEntity() {
        GameRegistry.registerTileEntity(TileInfusionInterceptor.class, new ResourceLocation(Tags.MOD_ID, "infusion_interceptor"));
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        if (Mods.THAUMCRAFT.isLoaded()){
            event.getRegistry().registerAll(
                    INFUSION_INTERCEPTOR
            );
        }
    }

    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        if (Mods.THAUMCRAFT.isLoaded()){
            event.getRegistry().registerAll(
                    regisItemBlock(INFUSION_INTERCEPTOR)
            );
        }
    }

    public static Item regisItemBlock(Block block){
        return new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
    }

}
