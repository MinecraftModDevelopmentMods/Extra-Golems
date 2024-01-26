package com.mcmoddev.golems.client.menu.guide_book.page;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.client.menu.button.CyclingItemButton;
import com.mcmoddev.golems.client.menu.guide_book.GuideBookScreen;
import com.mcmoddev.golems.client.menu.guide_book.IBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftingRecipePage extends TitleAndBodyPage {

	protected final ResourceLocation texture;
	protected final int imageWidth;
	protected final int imageHeight;
	protected final int u;
	protected final int v;

	protected List<CyclingItemButton> itemButtons;

	public CraftingRecipePage(Font font, int x, int y, int width, int height, int padding, @Nullable Component title, @Nullable Component body,
							  ResourceLocation texture, int imageWidth, int imageHeight, int u, int v, List<CyclingItemButton> itemButtons) {
		super(font, x, y, width, height, padding, title, body);
		this.texture = texture;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.u = u;
		this.v = v;
		this.itemButtons = itemButtons;
	}

	//// RENDER METHODS ////

	@Override
	public void render(final IBookScreen parent, final GuiGraphics graphics, int pageNumber, final float ticksOpen) {
		// render background
		graphics.blit(texture, this.x + padding * 2, this.y + padding * 2, u, v, imageWidth, imageHeight);
		// update item button index
		int index = (int) (ticksOpen / 30);
		for(CyclingItemButton b : itemButtons) {
			b.setIndex(index);
		}
		super.render(parent, graphics, pageNumber, ticksOpen);
	}

	//// BUILDER ////

	public static class Builder extends TitleAndBodyPage.Builder {
		private final CraftingRecipe recipe;
		private ResourceLocation texture;
		private int textureWidth;
		private int textureHeight;
		private int textureU;
		private int textureV;

		//// CONSTRUCTOR ////

		public Builder(IBookScreen parent, CraftingRecipe recipe) {
			super(parent);
			this.recipe = recipe;
			this.texture = GuideBookScreen.CONTENTS;
			this.textureWidth = 84;
			this.textureHeight = 46;
			this.textureU = 111;
			this.textureV = 54;
		}

		public Builder texture(final ResourceLocation texture) {
			this.texture = texture;
			return this;
		}

		public Builder textureUV(final int u, final int v) {
			this.textureU = u;
			this.textureV = v;
			return this;
		}

		public Builder textureDimensions(final int width, final int height) {
			this.textureWidth = width;
			this.textureHeight = height;
			return this;
		}

		//// BUILD ////

		public CraftingRecipePage build() {
			// create list of item buttons
			int bx = this.x + padding * 2;
			int by = this.y + padding * 2;
			final List<Ingredient> ingredients = recipe.getIngredients();
			final List<CyclingItemButton> itemButtons = new ArrayList<>();
			// add first four ingredients from recipe to the list
			for(int i = 0, n = Math.max(ingredients.size(), 4); i < n; i++) {
				itemButtons.add(parent.addButton(new CyclingItemButton(new Button.Builder(Component.empty(), b -> {})
						.pos(bx + (i % 2) * 18, by + (i / 2) * 18), ingredients.get(i), 1.0F)));
			}
			// add result to the list
			// TODO figure out x position
			final ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
			itemButtons.add(parent.addButton(new CyclingItemButton(new Button.Builder(Component.empty(), b -> {})
					.pos(bx + 40, by + 16), ImmutableList.of(result), 1.0F)));
			// build the page
			return new CraftingRecipePage(font, x, y, width, height, padding, title, body, texture, textureWidth, textureHeight, textureU, textureV, itemButtons);
		}
	}
}
