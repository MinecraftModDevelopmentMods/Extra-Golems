package com.mcmoddev.golems.client.menu.guide_book.page;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class GolemDiagramPage extends BookPage {

	protected final ItemStack head;
	protected final ItemStack body;
	protected final float scale;

	public GolemDiagramPage(Font font, int x, int y, int width, int height, int padding, ItemStack head, ItemStack body, float scale) {
		super(font, x, y, width, height, padding);
		this.head = head;
		this.body = body;
		this.scale = scale;
	}

	//// BOOK PAGE ////

	@Override
	public void render(IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {
		super.render(parent, graphics, pageNumber, ticksOpen);
		renderDiagram(parent, graphics, pageNumber, ticksOpen);
	}

	protected void renderDiagram(IBookScreen parent, GuiGraphics graphics, int pageNumber, float ticksOpen) {

		final int deltaXY = (int) (8.0F * scale);
		int x = this.x + (width / 8);
		int y = this.y + deltaXY;

		// start rendering
		graphics.pose().pushPose();
		graphics.pose().scale(scale, scale, scale);

		// head
		graphics.renderItem(this.head, (int) (x / scale), (int) (y / scale));
		// middle-bottom
		y += deltaXY * 4;
		graphics.renderItem(this.body, (int) (x / scale), (int) (y / scale));
		// arm-right
		x += deltaXY * 2;
		y -= (deltaXY * 5) / 2;
		graphics.renderItem(this.body, (int) (x / scale), (int) (y / scale));
		// middle-top
		x -= deltaXY * 2;
		y += (deltaXY / 2);
		graphics.renderItem(this.body, (int) (x / scale), (int) (y / scale));
		// arm-left
		x -= deltaXY * 2;
		y += (deltaXY / 2);
		graphics.renderItem(this.body, (int) (x / scale), (int) (y / scale));

		// finish rendering
		graphics.pose().popPose();
	}

	//// BUILDER ////

	public static class Builder extends BookPage.Builder {

		protected ItemStack head;
		protected ItemStack body;
		protected float scale;

		public Builder(IBookScreen parent) {
			super(parent);
			this.head = ExtraGolems.CONFIG.pumpkinBuildsGolems() ? new ItemStack(Blocks.CARVED_PUMPKIN) : new ItemStack(EGRegistry.BlockReg.GOLEM_HEAD.get());
			this.body = new ItemStack(Blocks.IRON_BLOCK);
			this.scale = 2.0F;
		}

		public Builder head(final ItemStack itemStack) {
			this.head = itemStack;
			return this;
		}

		public Builder body(final ItemStack itemStack) {
			this.body = itemStack;
			return this;
		}

		public Builder scale(final float scale) {
			this.scale = scale;
			return this;
		}

		@Override
		public GolemDiagramPage build() {
			return new GolemDiagramPage(font, x, y, width, height, padding, head, body, scale);
		}
	}
}
