package com.lw.random_additions.common.mixins.ae2;

import appeng.client.gui.implementations.GuiPatternTerm;
import appeng.client.gui.widgets.GuiImgButton;
import com.lw.random_additions.Tags;
import com.lw.random_additions.api.PatternUploadScreen;
import com.lw.random_additions.common.integration.ae2.patternupload.PatternUploadTargetInfo;
import com.lw.random_additions.common.network.NetworkHandler;
import com.lw.random_additions.common.network.PacketPatternUploadRequest;
import com.lw.random_additions.common.network.PacketPatternUploadSelect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(value = GuiPatternTerm.class, remap = false, priority = 2000)
public abstract class MixinGuiPatternTerm implements PatternUploadScreen {

    @Unique
    private static final int RandomAdditions$PATTERN_UPLOAD_WIDTH = 160;

    @Unique
    private static final int RandomAdditions$PATTERN_UPLOAD_ROW_HEIGHT = 18;

    @Unique
    private static final int RandomAdditions$PATTERN_UPLOAD_MIN_ROWS = 3;

    @Unique
    private static final int RandomAdditions$PATTERN_UPLOAD_MAX_ROWS = 12;

    @Unique
    private static final ResourceLocation RandomAdditions$PATTERN_UPLOAD_BUTTON = new ResourceLocation(
            Tags.MOD_ID, "textures/gui/pattern_upload/button.png");

    @Unique
    private static final ResourceLocation RandomAdditions$PATTERN_UPLOAD_BUTTON_HIGHLIGHTED = new ResourceLocation(
            Tags.MOD_ID, "textures/gui/pattern_upload/button_highlighted.png");

    @Unique
    private static final ResourceLocation RandomAdditions$PATTERN_UPLOAD_BUTTON_DISABLED = new ResourceLocation(
            Tags.MOD_ID, "textures/gui/pattern_upload/button_disabled.png");

    @Unique
    private static final ResourceLocation RandomAdditions$PATTERN_UPLOAD_SCROLLER = new ResourceLocation(
            Tags.MOD_ID, "textures/gui/pattern_upload/small_scroller.png");

    @Unique
    private static final ResourceLocation RandomAdditions$PATTERN_UPLOAD_BACKGROUND = new ResourceLocation(
            Tags.MOD_ID, "textures/gui/pattern_upload/background.png");

    @Shadow
    private GuiImgButton encodeBtn;

    @Unique
    private List<PatternUploadTargetInfo> RandomAdditions$patternUploadTargets = new ArrayList<>();

    @Unique
    private List<PatternUploadTargetInfo> RandomAdditions$filteredPatternUploadTargets = new ArrayList<>();

    @Unique
    private boolean RandomAdditions$showPatternUploadTargets;

    @Unique
    private int RandomAdditions$patternUploadScrollOffset;

    @Unique
    private String RandomAdditions$patternUploadSearch = "";

    @Unique
    private boolean RandomAdditions$patternUploadSearchFocused;

    @Override
    public void RandomAdditions$showPatternUploadTargets(final List<PatternUploadTargetInfo> targets) {
        this.RandomAdditions$patternUploadTargets = new ArrayList<>(targets);
        this.RandomAdditions$patternUploadSearch = "";
        this.RandomAdditions$patternUploadScrollOffset = 0;
        this.RandomAdditions$filterPatternUploadTargets();
        this.RandomAdditions$showPatternUploadTargets = !targets.isEmpty();
    }

    @Override
    public boolean RandomAdditions$handlePatternUploadMouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.RandomAdditions$isRightClickOnEncodeButton(mouseX, mouseY, mouseButton)) {
            this.RandomAdditions$requestPatternUploadTargets();
            return true;
        }

        if (!this.RandomAdditions$showPatternUploadTargets) {
            return false;
        }

        if (this.RandomAdditions$isInSearchBox(mouseX, mouseY)) {
            this.RandomAdditions$patternUploadSearchFocused = true;
            if (mouseButton == 1) {
                this.RandomAdditions$patternUploadSearch = "";
                this.RandomAdditions$filterPatternUploadTargets();
            }
            return true;
        }

        final int index = this.RandomAdditions$getHoveredTargetIndex(mouseX, mouseY);
        if (index >= 0) {
            final PatternUploadTargetInfo target = this.RandomAdditions$filteredPatternUploadTargets.get(index);
            if (!target.isFull()) {
                NetworkHandler.CHANNEL.sendToServer(new PacketPatternUploadSelect(this.RandomAdditions$patternUploadTargets.indexOf(target)));
                this.RandomAdditions$showPatternUploadTargets = false;
            }
            return true;
        }

        this.RandomAdditions$patternUploadSearchFocused = false;
        this.RandomAdditions$showPatternUploadTargets = false;
        return false;
    }

    @Override
    public void RandomAdditions$drawPatternUploadOverlay(final int mouseX, final int mouseY) {
        if (!this.RandomAdditions$showPatternUploadTargets || this.RandomAdditions$patternUploadTargets.isEmpty()) {
            return;
        }

        final Minecraft minecraft = Minecraft.getMinecraft();
        final int x = this.RandomAdditions$getPatternUploadX();
        final int y = this.RandomAdditions$getPatternUploadY();
        final int width = RandomAdditions$PATTERN_UPLOAD_WIDTH;
        final int rowHeight = RandomAdditions$PATTERN_UPLOAD_ROW_HEIGHT;
        final int rows = this.RandomAdditions$getVisibleRows();

        this.RandomAdditions$drawPatternUploadPanel(minecraft, x - 3, y - 33, width + 6,
                Math.max(1, rows) * rowHeight + 36);
        minecraft.fontRenderer.drawString(I18n.format("random_additions.ae2.pattern_upload.title"),
                x, y - 29, 0x404040);
        this.RandomAdditions$drawPatternUploadSearchBox(minecraft, x, y, width);

        if (this.RandomAdditions$filteredPatternUploadTargets.isEmpty()) {
            this.RandomAdditions$drawPatternUploadEmptyResult(minecraft, x, y);
            return;
        }

        this.RandomAdditions$drawPatternUploadTargetRows(minecraft, mouseX, mouseY, x, y, width, rowHeight, rows);

        this.RandomAdditions$drawPatternUploadScrollbar(x, y, width, rowHeight, rows);
    }

    @Override
    public boolean RandomAdditions$handlePatternUploadMouseWheel(final int wheelDelta) {
        if (!this.RandomAdditions$showPatternUploadTargets) {
            return false;
        }
        int maxOffset = Math.max(0, this.RandomAdditions$filteredPatternUploadTargets.size() - this.RandomAdditions$getVisibleRows());
        if (maxOffset <= 0) {
            return true;
        }
        if (wheelDelta < 0) {
            this.RandomAdditions$patternUploadScrollOffset = Math.min(maxOffset, this.RandomAdditions$patternUploadScrollOffset + 1);
        } else if (wheelDelta > 0) {
            this.RandomAdditions$patternUploadScrollOffset = Math.max(0, this.RandomAdditions$patternUploadScrollOffset - 1);
        }
        return true;
    }

    @Override
    public boolean RandomAdditions$handlePatternUploadKeyTyped(final char typedChar, final int keyCode) {
        if (!this.RandomAdditions$showPatternUploadTargets) {
            return false;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.RandomAdditions$showPatternUploadTargets = false;
            return true;
        }
        if (!this.RandomAdditions$patternUploadSearchFocused) {
            return false;
        }
        if (keyCode == Keyboard.KEY_BACK) {
            if (!this.RandomAdditions$patternUploadSearch.isEmpty()) {
                this.RandomAdditions$patternUploadSearch = this.RandomAdditions$patternUploadSearch.substring(0,
                        this.RandomAdditions$patternUploadSearch.length() - 1);
                this.RandomAdditions$filterPatternUploadTargets();
            }
            return true;
        }
        if (Character.isISOControl(typedChar)) {
            return true;
        }
        this.RandomAdditions$patternUploadSearch += typedChar;
        this.RandomAdditions$filterPatternUploadTargets();
        return true;
    }

    @Override
    public boolean RandomAdditions$isPatternUploadOverlayVisible() {
        return this.RandomAdditions$showPatternUploadTargets;
    }

    @Inject(method = {"actionPerformed", "func_146284_a"}, at = @At("HEAD"), cancellable = true)
    private void RandomAdditions$handlePatternUploadEncodeAction(final GuiButton button, final CallbackInfo ci) {
        if (button == this.encodeBtn && this.RandomAdditions$isRightMouseClick()) {
            this.RandomAdditions$requestPatternUploadTargets();
            ci.cancel();
        }
    }

    @Unique
    private int RandomAdditions$getHoveredTargetIndex(final int mouseX, final int mouseY) {
        final int x = this.RandomAdditions$getPatternUploadX();
        final int y = this.RandomAdditions$getPatternUploadY();
        final int width = RandomAdditions$PATTERN_UPLOAD_WIDTH;
        final int rowHeight = RandomAdditions$PATTERN_UPLOAD_ROW_HEIGHT;
        final int rows = this.RandomAdditions$getVisibleRows();

        if (!this.RandomAdditions$isInRect(mouseX, mouseY, x, y, width, rows * rowHeight)) {
            return -1;
        }
        return this.RandomAdditions$patternUploadScrollOffset + (mouseY - y) / rowHeight;
    }

    @Unique
    private boolean RandomAdditions$isRightClickOnEncodeButton(final int mouseX, final int mouseY, final int mouseButton) {
        return mouseButton == 1
                && this.encodeBtn != null
                && this.RandomAdditions$isMouseOverEncodeButton(mouseX, mouseY);
    }

    @Unique
    private boolean RandomAdditions$isMouseOverEncodeButton(final int mouseX, final int mouseY) {
        final GuiButton button = (GuiButton) this.encodeBtn;
        final int x = this.encodeBtn.xPos();
        final int y = this.encodeBtn.yPos();
        return button.enabled && button.visible
                && mouseX >= x
                && mouseY >= y
                && mouseX < x + button.width
                && mouseY < y + button.height;
    }

    @Unique
    private boolean RandomAdditions$isRightMouseClick() {
        return Mouse.getEventButton() == 1 || Mouse.isButtonDown(1);
    }

    @Unique
    private void RandomAdditions$requestPatternUploadTargets() {
        NetworkHandler.CHANNEL.sendToServer(new PacketPatternUploadRequest());
    }

    @Unique
    private boolean RandomAdditions$isInSearchBox(final int mouseX, final int mouseY) {
        return this.RandomAdditions$isInRect(mouseX, mouseY, this.RandomAdditions$getPatternUploadX(),
                this.RandomAdditions$getPatternUploadY() - 18, RandomAdditions$PATTERN_UPLOAD_WIDTH, 14);
    }

    @Unique
    private boolean RandomAdditions$isInRect(final int mouseX, final int mouseY, final int x, final int y,
                                             final int width, final int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Unique
    private int RandomAdditions$getVisibleRows() {
        return Math.min(this.RandomAdditions$getMaxVisibleRows(),
                Math.max(0, this.RandomAdditions$filteredPatternUploadTargets.size() - this.RandomAdditions$patternUploadScrollOffset));
    }

    @Unique
    private int RandomAdditions$getMaxVisibleRows() {
        final int y = this.RandomAdditions$getPatternUploadY();
        final int rowHeight = RandomAdditions$PATTERN_UPLOAD_ROW_HEIGHT;
        final int availableHeight = ((GuiPatternTerm) (Object) this).height - y - 8;
        return Math.min(RandomAdditions$PATTERN_UPLOAD_MAX_ROWS,
                Math.max(RandomAdditions$PATTERN_UPLOAD_MIN_ROWS, availableHeight / rowHeight));
    }

    @Unique
    private int RandomAdditions$getPatternUploadX() {
        return this.encodeBtn.xPos() + 20;
    }

    @Unique
    private int RandomAdditions$getPatternUploadY() {
        return this.encodeBtn.yPos() - 4;
    }

    @Unique
    private void RandomAdditions$drawPatternUploadSearchBox(final Minecraft minecraft, final int x, final int y,
                                                           final int width) {
        this.RandomAdditions$drawPatternUploadTexture(minecraft,
                this.RandomAdditions$patternUploadSearchFocused
                        ? RandomAdditions$PATTERN_UPLOAD_BUTTON_HIGHLIGHTED
                        : RandomAdditions$PATTERN_UPLOAD_BUTTON,
                x, y - 18, width, 14);
        final String searchText = this.RandomAdditions$patternUploadSearch.isEmpty()
                ? I18n.format("random_additions.ae2.pattern_upload.search")
                : this.RandomAdditions$patternUploadSearch;
        minecraft.fontRenderer.drawString(searchText, x + 4, y - 15,
                this.RandomAdditions$patternUploadSearch.isEmpty() ? 0x777777 : 0x303030);
    }

    @Unique
    private void RandomAdditions$drawPatternUploadEmptyResult(final Minecraft minecraft, final int x, final int y) {
        minecraft.fontRenderer.drawString(I18n.format("random_additions.ae2.pattern_upload.no_results"),
                x + 4, y + 5, 0x606060);
    }

    @Unique
    private void RandomAdditions$drawPatternUploadTargetRows(final Minecraft minecraft, final int mouseX,
                                                            final int mouseY, final int x, final int y,
                                                            final int width, final int rowHeight, final int rows) {
        for (int i = 0; i < rows; i++) {
            final int targetIndex = this.RandomAdditions$patternUploadScrollOffset + i;
            final PatternUploadTargetInfo target = this.RandomAdditions$filteredPatternUploadTargets.get(targetIndex);
            this.RandomAdditions$drawPatternUploadTargetRow(minecraft, target, mouseX, mouseY,
                    x, y + i * rowHeight, width, rowHeight);
        }
    }

    @Unique
    private void RandomAdditions$drawPatternUploadTargetRow(final Minecraft minecraft, final PatternUploadTargetInfo target,
                                                           final int mouseX, final int mouseY, final int x,
                                                           final int rowY, final int width, final int rowHeight) {
        final boolean hovered = this.RandomAdditions$isInRect(mouseX, mouseY, x, rowY, width, rowHeight);
        final ResourceLocation texture = target.isFull()
                ? RandomAdditions$PATTERN_UPLOAD_BUTTON_DISABLED
                : hovered ? RandomAdditions$PATTERN_UPLOAD_BUTTON_HIGHLIGHTED : RandomAdditions$PATTERN_UPLOAD_BUTTON;
        this.RandomAdditions$drawPatternUploadTexture(minecraft, texture, x, rowY, width, rowHeight);

        final ItemStack icon = target.getIcon();
        if (!icon.isEmpty()) {
            minecraft.getRenderItem().renderItemAndEffectIntoGUI(icon, x + 1, rowY + 1);
        }

        final String suffix = this.RandomAdditions$getPatternUploadTargetSuffix(target);
        final int suffixWidth = suffix.isEmpty() ? 0 : minecraft.fontRenderer.getStringWidth(suffix) + 4;
        final String name = minecraft.fontRenderer.trimStringToWidth(target.getName(), width - 22 - suffixWidth);
        minecraft.fontRenderer.drawString(name, x + 20, rowY + 5, target.isFull() ? 0x777777 : 0x303030);
        if (!suffix.isEmpty()) {
            minecraft.fontRenderer.drawString(suffix, x + width - suffixWidth,
                    rowY + 5, target.isFull() ? 0xAA3333 : 0x606060);
        }
    }

    @Unique
    private String RandomAdditions$getPatternUploadTargetSuffix(final PatternUploadTargetInfo target) {
        if (target.isFull()) {
            return I18n.format("random_additions.ae2.pattern_upload.full");
        }
        return target.getTotalCount() > 1 ? target.getFreeCount() + "/" + target.getTotalCount() : "";
    }

    @Unique
    private void RandomAdditions$drawPatternUploadScrollbar(final int x, final int y, final int width,
                                                           final int rowHeight, final int rows) {
        if (rows <= 0 || this.RandomAdditions$filteredPatternUploadTargets.size() <= rows) {
            return;
        }
        final int barX = x + width - 7;
        final int listHeight = rows * rowHeight;
        final int total = this.RandomAdditions$filteredPatternUploadTargets.size();
        final int thumbHeight = 15;
        final int maxOffset = Math.max(1, total - rows);
        final int thumbY = y + (listHeight - thumbHeight) * this.RandomAdditions$patternUploadScrollOffset / maxOffset;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.RandomAdditions$drawPatternUploadPanel(minecraft, barX + 1, y, 5, listHeight);
        minecraft.getTextureManager().bindTexture(RandomAdditions$PATTERN_UPLOAD_SCROLLER);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(barX, thumbY, 0, 0, 7, thumbHeight, 7, 15);
    }

    @Unique
    private void RandomAdditions$drawPatternUploadTexture(final Minecraft minecraft, final ResourceLocation texture,
                                                          final int x, final int y, final int width, final int height) {
        minecraft.getTextureManager().bindTexture(texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, 200, 20);
    }

    @Unique
    private void RandomAdditions$drawPatternUploadPanel(final Minecraft minecraft, final int x, final int y,
                                                        final int width, final int height) {
        final int border = 2;
        final int innerWidth = Math.max(0, width - border * 2);
        final int innerHeight = Math.max(0, height - border * 2);
        minecraft.getTextureManager().bindTexture(RandomAdditions$PATTERN_UPLOAD_BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        Gui.drawScaledCustomSizeModalRect(x + border, y + border, border, border, 252, 252,
                innerWidth, innerHeight, 256, 256);
        Gui.drawScaledCustomSizeModalRect(x + border, y, border, 0, 252, border,
                innerWidth, border, 256, 256);
        Gui.drawScaledCustomSizeModalRect(x + border, y + height - border, border, 254, 252, border,
                innerWidth, border, 256, 256);
        Gui.drawScaledCustomSizeModalRect(x, y + border, 0, border, border, 252,
                border, innerHeight, 256, 256);
        Gui.drawScaledCustomSizeModalRect(x + width - border, y + border, 254, border, border, 252,
                border, innerHeight, 256, 256);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, border, border, 256, 256);
        Gui.drawModalRectWithCustomSizedTexture(x + width - border, y, 254, 0, border, border, 256, 256);
        Gui.drawModalRectWithCustomSizedTexture(x, y + height - border, 0, 254, border, border, 256, 256);
        Gui.drawModalRectWithCustomSizedTexture(x + width - border, y + height - border,
                254, 254, border, border, 256, 256);
    }

    @Unique
    private void RandomAdditions$filterPatternUploadTargets() {
        this.RandomAdditions$filteredPatternUploadTargets.clear();
        String query = this.RandomAdditions$patternUploadSearch.trim().toLowerCase(Locale.ROOT);
        String[] terms = query.isEmpty() ? new String[0] : query.split("\\s+");
        for (PatternUploadTargetInfo target : this.RandomAdditions$patternUploadTargets) {
            String searchText = target.getSearchText().toLowerCase(Locale.ROOT);
            boolean matched = true;
            for (String term : terms) {
                if (!searchText.contains(term)) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                this.RandomAdditions$filteredPatternUploadTargets.add(target);
            }
        }
        this.RandomAdditions$patternUploadScrollOffset = 0;
    }
}
