package com.mcmoddev.golems.client.menu.button;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class CyclingItemButton extends Button {

	private static final ItemStack BARRIER_ITEMSTACK = new ItemStack(Items.BARRIER);

	private final List<ItemStack> items;
	private final float scale;

	private ItemStack itemStack;
	private int index;

	public CyclingItemButton(Button.Builder builder, Ingredient ingredient, float scale) {
		this(builder, ImmutableList.copyOf(ingredient.getItems()), scale);
	}

	public CyclingItemButton(Button.Builder builder, List<ItemStack> list, float scale) {
		super(builder.size((int) (16.0F * scale), (int) (16.0F * scale)));
		this.items = new ArrayList<>(list);
		this.scale = scale;
		this.itemStack = ItemStack.EMPTY;
		this.setIndex(0);
	}

	//// GETTERS AND SETTERS ////

	public void setIndex(final int index) {
		// verify index changed
		if(index == this.index) {
			return;
		}
		// update index and item stack
		this.index = index;
		this.itemStack = getItem(index);
		// update message and tooltip
		if(this.itemStack.isEmpty()) {
			this.itemStack = BARRIER_ITEMSTACK;
			this.setMessage(Component.empty());
			this.setTooltip(null);
		} else {
			this.setMessage(this.itemStack.getHoverName());
			this.setTooltip(Tooltip.create(getTooltipFromItem(this.itemStack)));
		}
	}

	public int getIndex() {
		return index;
	}

	public void setItems(final List<ItemStack> list) {
		this.items.clear();
		this.items.addAll(list);
		this.setIndex(index);
	}

	public List<ItemStack> getItems() {
		return items;
	}

	public ItemStack getItem(final int index) {
		if(this.items.isEmpty() || index < 0) {
			return ItemStack.EMPTY;
		}
		return this.items.get(index % this.items.size());
	}

	public float getScale() {
		return scale;
	}

	//// RENDER ////

	@Override
	protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

		// begin rendering
		graphics.pose().pushPose();
		// scale the pose stack
		graphics.pose().scale(scale, scale, scale);

		// render the item stack
		graphics.renderItem(this.itemStack, (int)(getX() / scale), (int) (getY() / scale));

		// finish rendering
		graphics.pose().popPose();
	}

	//// HELPER METHODS ////

	/**
	 * @param itemStack the item stack
	 * @return the result of {@link Screen#getTooltipFromItem(Minecraft, ItemStack)} combined into a single component
	 */
	public static Component getTooltipFromItem(final ItemStack itemStack) {
		Component builder = Component.empty();
		// iterate lines and add each one separated by newline
		for(Component line : Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack)) {
			builder.getSiblings().add(line);
			builder.getSiblings().add(Component.literal("\n"));
		}
		// remove trailing newline
		if(builder.getSiblings().size() > 1) {
			builder.getSiblings().remove(builder.getSiblings().size() - 1);
		}
		return builder;
	}
}
