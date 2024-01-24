package com.mcmoddev.golems.client.menu.guide_book.button;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.client.menu.guide_book.module.DrawBlockModule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class BlockButton extends Button {

	protected final Screen parent;
	protected final DrawBlockModule drawBlockModule;
	protected final int margin;
	private final float scale;

	private final List<Block> blocks;
	private Block currentBlock;

	public BlockButton(Screen parent, DrawBlockModule drawBlockModule, Block[] blockValues, int x, int y, 
					   int margin, float scaleIn) {
		super(Button.builder(Component.empty(), b -> {
				})
				.pos(x, y)
				.size((int) (scaleIn * 16.0F), (int) (scaleIn * 16.0F)));
		this.parent = parent;
		this.drawBlockModule = drawBlockModule;
		this.blocks = new ArrayList<>(ImmutableList.copyOf(blockValues));
		this.margin = margin;
		this.scale = scaleIn;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
		// draw the block
		this.drawBlockModule
			.withBlock(this.currentBlock)
			.withScale(this.scale)
			.withPos(this.getX() - margin - 4, this.getY() - margin)
			.render(parent, graphics, partialTicks);
	}

	public void tick(final Screen parent, final int ticksOpen) {
		// update the block to draw
		if (!blocks.isEmpty()) {
			int index = (ticksOpen / 30) % blocks.size();
			this.currentBlock = this.blocks.get(index);
			setTooltip(Tooltip.create(this.currentBlock.getName()));
		} else {
			this.currentBlock = Blocks.AIR;
			setTooltip(null);
		}
	}

	public void updateBlocks(final List<Block> blocksToDraw) {
		this.blocks.clear();
		this.blocks.addAll(blocksToDraw);
		this.tick(parent, 0);
	}
}
