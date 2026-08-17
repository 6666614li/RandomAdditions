package com.lw.random_additions.common.mixins.thaumicenergistics;

import com.lw.random_additions.common.block.thaumcraft.BlockInfusionInterceptor;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumicenergistics.tile.TileInfusionProvider;

@Mixin(value = TileInfusionProvider.class, remap = false)
public abstract class MixinTileInfusionProvider {

    @Inject(method = "isBlocked", at = @At("HEAD"), cancellable = true)
    private void RandomAdditions$blockDirectMatrixExtraction(final CallbackInfoReturnable<Boolean> cir) {
        final TileEntity provider = (TileEntity) (Object) this;
        if (provider.getWorld() != null && provider.getWorld().getBlockState(provider.getPos().up()).getBlock() instanceof BlockInfusionInterceptor) {
            cir.setReturnValue(true);
        }
    }
}
