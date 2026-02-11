package com.lw.random_additons.mixin.thaumcraft;

import com.lw.random_additons.config.RandomAdditonsConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.tiles.crafting.TileCrucible;

@Mixin(TileCrucible.class)
public class MixinTileCrucible {

    @ModifyVariable(
            method = "attemptSmelt",
            at = @At(
                    value = "INVOKE",
                    target = "Lthaumcraft/common/lib/crafting/ThaumcraftCraftingManager;getObjectTags(Lnet/minecraft/item/ItemStack;)Lthaumcraft/api/aspects/AspectList;",
                    shift = At.Shift.BY,
                    by = 2
            ),
            name = "ot"
    )
    public AspectList onattemptSmelt(AspectList original, ItemStack item){
        if (RandomAdditonsConfig.crucibleWhitelist(item)) {
            return null;
        }
        return original;
    }
}
