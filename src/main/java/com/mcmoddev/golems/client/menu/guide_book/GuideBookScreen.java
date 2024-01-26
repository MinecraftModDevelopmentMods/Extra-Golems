package com.mcmoddev.golems.client.menu.guide_book;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.menu.button.ScrollButton;
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

	public static final ResourceLocation TEXTURE = new ResourceLocation(ExtraGolems.MODID, "textures/gui/guide_book.png");
	public static final ResourceLocation CONTENTS = new ResourceLocation(ExtraGolems.MODID, "textures/gui/guide_book_contents.png");

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
		this.groups = GuideBookGroup.buildGroups(getMinecraft().level.registryAccess());
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
		this.doneButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.minecraft.setScreen(null))
				.pos(this.x + (this.width - doneButtonWidth) / 2, this.y + this.imageHeight + 8)
				.size(98, 20)
				.build());

		// prepare to add previous and next page buttons
		final int arrowWidth = 18;
		final int arrowHeight = 10;
		final int arrowY = this.y + this.height - arrowHeight - 8;
		// add Previous Page button
		this.prevPageButton = this.addRenderableWidget(new ImageButton(this.x + 8, arrowY, arrowWidth, arrowHeight,
				23, 169, arrowHeight, TEXTURE, b -> addPage(-2)));
		// add Next Page button
		this.nextPageButton = this.addRenderableWidget(new ImageButton(this.x + this.imageWidth - arrowWidth - 8, arrowY, arrowWidth, arrowHeight,
				0, 169, arrowHeight, TEXTURE, b -> addPage(2)));

		// create guide book
		guideBook = new GuideBook(this.groups, this, this.x, this.y, 164, 256);
		// update index
		setPageIndex(this.page);
	}

	//// TICK ////

	@Override
	public void tick() {
		super.tick();
		ticksOpen++;
	}

	//// RENDER ////

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		// draw background
		renderBackground(graphics);
		graphics.blit(TEXTURE, this.x, this.y, 0, 0, this.imageWidth, this.imageHeight);

		// calculate ticks open
		final float ticksOpen = this.ticksOpen + partialTicks;

		// render open pages
		if(this.guideBook != null) {
			this.guideBook.getPage(this.page).render(this, graphics, this.page, ticksOpen);
			this.guideBook.getPage(this.page + 1).render(this, graphics, this.page + 1, ticksOpen);
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
	}

	@Override
	public int getPageIndex() {
		return this.page;
	}

	//// PAGE ////

	public void addPage(final int amount) {
		setPageIndex(Mth.clamp(this.page + amount, 0, this.guideBook.getPageCount() - 1));
		this.prevPageButton.visible = this.page > 0;
		this.nextPageButton.visible = this.page < (this.guideBook.getPageCount() - 2);
	}

	//// SCROLL ////

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if(guideBook != null && guideBook.getPage(page) instanceof ScrollButton.IScrollProvider provider) {
			return provider.getScrollButton().mouseScrolled(mouseX, mouseY, amount);
		}
		if(guideBook != null && guideBook.getPage(page + 1) instanceof ScrollButton.IScrollProvider provider) {
			return provider.getScrollButton().mouseScrolled(mouseX, mouseY, amount);
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if(guideBook != null && guideBook.getPage(page) instanceof ScrollButton.IScrollProvider provider && provider.getScrollButton().isDragging()) {
			provider.getScrollButton().onDrag(mouseX, mouseY, dragX, dragY);
			return true;
		}
		if(guideBook != null && guideBook.getPage(page + 1) instanceof ScrollButton.IScrollProvider provider && provider.getScrollButton().isDragging()) {
			provider.getScrollButton().onDrag(mouseX, mouseY, dragX, dragY);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}
}
