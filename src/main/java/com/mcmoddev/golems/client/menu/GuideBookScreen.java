package com.mcmoddev.golems.client.menu;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.menu.button.ScrollButton;
import com.mcmoddev.golems.client.menu.guide_book.GuideBookGroup;
import com.mcmoddev.golems.client.menu.guide_book.GuideBook;
import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class GuideBookScreen extends Screen implements IBookScreen {

	public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID,
			"textures/gui/guide_book.png");
	public static final ResourceLocation CONTENTS = ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID,
			"textures/gui/guide_book_contents.png");

	protected int imageWidth;
	protected int imageHeight;
	protected int x;
	protected int y;

	protected int page;
	protected int ticksOpen;

	protected final List<GuideBookGroup> groups;
	protected GuideBook guideBook;

	/** Closes the screen **/
	protected Button doneButton;
	/** Increments the page number by 2 **/
	private Button nextPageButton;
	/** Decrements the page number by 2 **/
	private Button prevPageButton;

	public GuideBookScreen(Player player, ItemStack item) {
		super(EGRegistry.ItemReg.GUIDE_BOOK.get().getDescription());
		this.imageWidth = 256;
		this.imageHeight = 164;
		this.groups = GuideBookGroup.buildGroups(player.level().registryAccess());
		this.groups.sort(GuideBookGroup.SORT_BY_ATTACK);
		this.page = 0;
	}

	//// INIT ////

	@Override
	protected void init() {
		super.init();
		// calculate position
		this.x = (this.width - this.imageWidth) / 2;
		this.y = (this.height - this.imageHeight) / 2;
		// reset ticks open
		this.ticksOpen = 0;

		// add Done button
		final int doneButtonWidth = 98;
		this.doneButton = this.addRenderableWidget(
				Button.builder(Component.translatable("gui.done"), b -> this.minecraft.setScreen(null))
						.pos(this.x + (this.imageWidth - doneButtonWidth) / 2, this.y + this.imageHeight + 8)
						.size(98, 20)
						.build());

		// prepare to add previous and next page buttons
		final int arrowWidth = 18;
		final int arrowHeight = 10;
		final int arrowY = this.y + this.imageHeight - arrowHeight - 12;
		// add Previous Page button
		this.prevPageButton = this
				.addRenderableWidget(new SimpleImageButton(this.x + 12, arrowY, arrowWidth, arrowHeight,
						22, 168, arrowHeight, TEXTURE, b -> addPage(-2)));
		// add Next Page button
		this.nextPageButton = this.addRenderableWidget(
				new SimpleImageButton(this.x + this.imageWidth - arrowWidth - 12, arrowY, arrowWidth, arrowHeight,
						0, 168, arrowHeight, TEXTURE, b -> addPage(2)));

		// create guide book
		guideBook = new GuideBook(this, this.groups, this.x, this.y, 128, 164);
		// update index
		setPageIndex(this.page);
	}

	//// TICK ////

	@Override
	public void tick() {
		super.tick();
		ticksOpen++;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/**
	 * Override to disable the blur effect that was added in Minecraft 1.21
	 * Without this override, the screen content appears blurry
	 */
	@Override
	protected void renderBlurredBackground(float partialTick) {
		// Do nothing - this prevents the blur effect from being applied
	}

	//// RENDER ////

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		// draw transparent background without blur effect
		renderTransparentBackground(graphics);
		graphics.blit(TEXTURE, this.x, this.y, 0, 0, this.imageWidth, this.imageHeight);

		// calculate ticks open
		final float ticksOpen = this.ticksOpen + partialTicks;

		// render open pages
		if (this.guideBook != null) {
			this.guideBook.getPage(this.page).render(this, graphics, ticksOpen);
			this.guideBook.getPage(this.page + 1).render(this, graphics, ticksOpen);
		}

		// draw buttons, etc.
		super.render(graphics, mouseX, mouseY, partialTicks);
	}

	//// BOOK SCREEN ////

	@Override
	public <T extends AbstractWidget> T addButton(T button) {
		return addRenderableWidget(button);
	}

	@Override
	public long getTicksOpen() {
		return this.ticksOpen;
	}

	@Override
	public Screen getSelf() {
		return this;
	}

	@Override
	public Font getFont() {
		return this.font;
	}

	@Override
	public int getStartX() {
		return this.x;
	}

	@Override
	public int getStartY() {
		return this.y;
	}

	@Override
	public void setPageIndex(int page) {
		// hide current pages
		this.guideBook.getPage(this.page).onHide(this);
		this.guideBook.getPage(this.page + 1).onHide(this);
		// update page index
		this.page = (page >> 1) * 2;
		// show new pages
		this.guideBook.getPage(this.page).onShow(this);
		this.guideBook.getPage(this.page + 1).onShow(this);
		// update page buttons
		this.prevPageButton.visible = this.page > 0;
		this.nextPageButton.visible = this.page < (this.guideBook.getPageCount() - 2);
	}

	@Override
	public int getPageIndex() {
		return this.page;
	}

	//// PAGE ////

	public void addPage(final int amount) {
		setPageIndex(Mth.clamp(this.page + amount, 0, this.guideBook.getPageCount() - 1));
	}

	//// SCROLL ////

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (guideBook != null && guideBook.getPage(page) instanceof ScrollButton.IScrollProvider provider
				&& provider.getScrollButton() != null) {
			return provider.getScrollButton().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		if (guideBook != null && guideBook.getPage(page + 1) instanceof ScrollButton.IScrollProvider provider
				&& provider.getScrollButton() != null) {
			return provider.getScrollButton().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
		}
		return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (guideBook != null && guideBook.getPage(page) instanceof ScrollButton.IScrollProvider provider
				&& provider.getScrollButton().isDragging()) {
			provider.getScrollButton().onDrag(mouseX, mouseY, dragX, dragY);
			return true;
		}
		if (guideBook != null && guideBook.getPage(page + 1) instanceof ScrollButton.IScrollProvider provider
				&& provider.getScrollButton().isDragging()) {
			provider.getScrollButton().onDrag(mouseX, mouseY, dragX, dragY);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	private static class SimpleImageButton extends Button {
		private final ResourceLocation texture;
		private final int u;
		private final int v;
		private final int dv;

		public SimpleImageButton(int x, int y, int width, int height, int u, int v, int dv, ResourceLocation texture,
				OnPress onPress) {
			super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
			this.texture = texture;
			this.u = u;
			this.v = v;
			this.dv = dv;
		}

		@Override
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int vOffset = this.v;
			if (this.isHoveredOrFocused()) {
				vOffset += this.dv;
			}
			guiGraphics.blit(this.texture, this.getX(), this.getY(), this.u, vOffset, this.width, this.height);
		}
	}
}
