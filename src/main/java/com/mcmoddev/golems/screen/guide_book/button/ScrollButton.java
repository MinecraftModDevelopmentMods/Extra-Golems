package com.mcmoddev.golems.screen.guide_book.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScrollButton extends Button {

    protected final ResourceLocation resourceLocation;
    protected final int iconU;
    protected final int iconV;
    protected final int iconWidth;
    protected final int iconHeight;
    protected final int iconDeltaV;

    protected final boolean isVertical;
    protected final float scrollAmountMultiplier;
    protected final IScrollListener listener;

    /** The percent of scroll progress **/
    protected float scrollPercent;
    protected boolean dragging;

	public ScrollButton(Button.Builder builder, ResourceLocation resourceLocation,
						int iconU, int iconV, int iconWidth, int iconHeight, int iconDeltaV,
						boolean isVertical, final float scrollAmountMultiplier, final IScrollListener listener) {
		super(builder);
		this.resourceLocation = resourceLocation;
		this.iconU = iconU;
		this.iconV = iconV;
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		this.iconDeltaV = iconDeltaV;
		this.isVertical = isVertical;
		this.scrollAmountMultiplier = scrollAmountMultiplier;
		this.listener = listener;
		this.scrollPercent = 0.0F;
	}

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (this.visible) {
            RenderSystem.setShaderTexture(0, resourceLocation);
            // determine v coordinate
            int v = iconV;
            if(!isActive()) {
                v += iconDeltaV;
            }
            // determine icon position
            int renderX = this.getX();
            int renderY = this.getY();
            if(isVertical) {
                renderY += Mth.floor((float) (height - iconHeight) * scrollPercent);
            } else {
                renderX += Mth.floor((float) (width - iconWidth) * scrollPercent);
            }
            // draw button icon
            fill(poseStack, renderX, renderY, renderX + iconWidth, renderY + iconHeight, 0xFFFF00);
            blit(poseStack, renderX, renderY, iconU, v, iconWidth, iconHeight);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.dragging = true;
        setValueFromMouse(mouseX, mouseY);
        super.onClick(mouseX, mouseY);
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.dragging = true;
        setValueFromMouse(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if(isActive()) {
            float scrollPercent = Mth.clamp(this.scrollPercent - (float) amount * scrollAmountMultiplier, 0.0F, 1.0F);
            setScrollPercent(scrollPercent);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean isDragging() {
        return dragging;
    }

    protected void setValueFromMouse(final double mouseX, final double mouseY) {
        float scrollPercent = (float) ((isVertical ? (mouseY - this.getY()) : (mouseX - this.getX())) / (float) height);
        this.setScrollPercent(Mth.clamp(scrollPercent, 0.0F, 1.0F));
    }

    public void setScrollPercent(final float scrollPercent) {
        this.scrollPercent = scrollPercent;
        listener.onScroll(this, scrollPercent);
    }

    public double getScrollPercent() {
        return this.scrollPercent;
    }

    @FunctionalInterface
    public static interface IScrollListener {
        /**
         * Called when the scroll percent changes
         * @param button the scroll button
         * @param percent the updated scroll amount
         */
        void onScroll(final ScrollButton button, final float percent);
    }
}
