package com.mcmoddev.golems.screen.guide_book.button;

import com.mcmoddev.golems.screen.guide_book.GolemBookScreen;
import com.mcmoddev.golems.screen.guide_book.module.DrawBlockModule;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class BlockButton extends Button {

	protected final Screen parent;
	protected final DrawBlockModule drawBlockModule;
	protected final int margin;
	private final float scale;
	
	private Block[] blocks;
	private Block currentBlock;

	public BlockButton(Screen parent, DrawBlockModule drawBlockModule, Block[] blockValues, int x, int y, 
					   int margin, float scaleIn) {
		super(Button.builder(Component.empty(), b -> {
				})
				.pos(x, y)
				.size((int) (scaleIn * 16.0F), (int) (scaleIn * 16.0F)));
		this.parent = parent;
		this.drawBlockModule = drawBlockModule;
		this.blocks = blockValues;
		this.margin = margin;
		this.scale = scaleIn;
	}

	@Override
	public void renderButton(final PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			// update hovered flag
			this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
			// draw the block
			matrix.pushPose();
			this.drawBlockModule
					.withBlock(this.currentBlock)
					.withScale(this.scale)
					.withPos(this.getX() - margin - 4, this.getY() - margin)
					.render(parent, matrix, partialTicks);
			matrix.popPose();
		}
	}

	public void tick(final Screen parent, final int ticksOpen) {
		// update the block to draw
		if (blocks != null && blocks.length > 0) {
			int index = (ticksOpen / 30) % blocks.length;
			this.currentBlock = this.blocks[index];
			setTooltip(Tooltip.create(this.currentBlock.getName()));
		} else {
			this.currentBlock = Blocks.AIR;
			setTooltip(null);
		}
	}

	public void updateBlocks(final Block[] blocksToDraw) {
		this.blocks = blocksToDraw;
		this.tick(parent, 0);
	}
}
