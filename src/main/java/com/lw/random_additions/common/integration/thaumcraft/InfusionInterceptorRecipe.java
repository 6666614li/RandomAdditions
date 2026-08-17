package com.lw.random_additions.common.integration.thaumcraft;

import com.lw.random_additions.Tags;
import com.lw.random_additions.misc.BlockRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;

public final class InfusionInterceptorRecipe {

    private static final int INSTABILITY_MODERATE = 5;
    private static final int ESSENTIA_COST = 10000;
    private static final int ACCELERATOR_COUNT = 16;

    private InfusionInterceptorRecipe() {
    }

    public static void register() {
        final Object[] components = new Object[ACCELERATOR_COUNT];
        for (int index = 0; index < components.length; index++) {
            components[index] = new ItemStack(BlocksTC.matrixSpeed);
        }

        final AspectList aspects = new AspectList()
                .add(Aspect.AIR, ESSENTIA_COST)
                .add(Aspect.FIRE, ESSENTIA_COST)
                .add(Aspect.WATER, ESSENTIA_COST)
                .add(Aspect.EARTH, ESSENTIA_COST)
                .add(Aspect.ORDER, ESSENTIA_COST)
                .add(Aspect.ENTROPY, ESSENTIA_COST);

        ThaumcraftApi.addInfusionCraftingRecipe(
                new ResourceLocation(Tags.MOD_ID, "infusion_interceptor"),
                new InfusionRecipe(
                        "INFUSION",
                        new ItemStack(BlockRegister.INFUSION_INTERCEPTOR),
                        INSTABILITY_MODERATE,
                        aspects,
                        new ItemStack(BlocksTC.stoneArcane),
                        components
                )
        );
    }
}
