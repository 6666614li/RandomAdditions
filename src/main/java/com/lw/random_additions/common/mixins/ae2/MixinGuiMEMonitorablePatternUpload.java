package com.lw.random_additions.common.mixins.ae2;

import appeng.client.gui.implementations.GuiMEMonitorable;
import com.lw.random_additions.api.PatternUploadScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = GuiMEMonitorable.class, remap = false)
public abstract class MixinGuiMEMonitorablePatternUpload {

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true, remap = true)
    private void RandomAdditions$handlePatternUploadKeyTyped(final char typedChar, final int keyCode, final CallbackInfo ci) throws IOException {
        if ((Object) this instanceof PatternUploadScreen
                && ((PatternUploadScreen) (Object) this).RandomAdditions$handlePatternUploadKeyTyped(typedChar,
                keyCode)) {
            ci.cancel();
        }
    }
}
