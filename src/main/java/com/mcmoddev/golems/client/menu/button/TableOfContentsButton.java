package com.mcmoddev.golems.client.menu.button;

import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import com.mcmoddev.golems.client.menu.guide_book.ITableOfContentsEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;

/**
 * Displays an {@link ItemStack} and message on an {@link ImageButton}
 */
public class TableOfContentsButton extends ImageButton {

	protected final IBookScreen parent;
	protected final Font font;
	protected final int margin;

	protected ITableOfContentsEntry entry;
	protected Component tooltip;
	protected int index;

	public TableOfContentsButton(final IBookScreen parent, final Font font,
								 final int x, final int y, final int width, final int height, int margin,
								 final ResourceLocation texture, final int u, final int v, final int dv,
								 final OnPress onPress) {
		super(x, y, width, height, u, v, dv, texture, onPress);
		this.parent = parent;
		this.font = font;
		this.margin = margin;
		this.index = 0;
	}

	public void setEntry(final ITableOfContentsEntry entry, final int index) {
		this.entry = entry;
		this.index = index;
		// update message
		final Component message = entry.getMessage(0);
		final int maxWidth = (this.width - 18 - 2 * 2);
		if(font.wordWrapHeight(message, maxWidth) > font.lineHeight * 2) {
			final String sMessage = StringUtil.truncateStringIfNecessary(ChatFormatting.stripFormatting(message.getString()), (int) (maxWidth / 4.8F), true);
			this.setMessage(Component.literal(sMessage).withStyle(message.getStyle()));
		} else {
			this.setMessage(message);
		}
		// update tooltip
		final Component tooltip = message.copy();
		if(Minecraft.getInstance().options.advancedItemTooltips) {
			tooltip.getSiblings().add(Component.literal("\n"));
			tooltip.getSiblings().add(entry.getAdvancedMessage(0));
		}
		this.setTooltip(Tooltip.create(tooltip));
	}

	public int getIndex() {
		return index;
	}

	@Override
	public void renderWidget(final GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.renderWidget(graphics, mouseX, mouseY, partialTicks);
		// determine index
		int index = (int) (this.parent.getTicksOpen() / 30L);
		// draw the block itemstack
		int posX = this.getX() + 3;
		int posY = this.getY() + (height - 16) / 2;
		ItemStack itemStack = this.entry.getItem(index);
		graphics.renderItem(itemStack, posX, posY);
		// draw the message
		int maxWidth = (this.width - 18 - 2 * 2);
		posX += 18;
		posY = this.getY() + 1 + (this.height - font.wordWrapHeight(getMessage(), maxWidth)) / 2;
		graphics.drawWordWrap(font, getMessage(), posX, posY, maxWidth, 0);
		//graphics.drawString(font, getMessage(), posX, posY, 0, false);
	}
}
