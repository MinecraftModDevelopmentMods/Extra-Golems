package com.mcmoddev.golems.screen.guide_book.module;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class DrawRecipePageModule extends DrawPageModule {

	protected final ItemRenderer itemRenderer;
	protected final ResourceLocation texture;
	protected final int imageWidth;
	protected final int imageHeight;
	protected final int u;
	protected final int v;

	protected float scale;
	protected CraftingRecipe recipe;
	protected ItemStack[] ingredients;

	public DrawRecipePageModule(Font font, int width, int height, int margin,
								ItemRenderer itemRenderer, ResourceLocation texture,
								int imageWidth, int imageHeight, int u, int v) {
		super(font, width, height, margin);
		this.itemRenderer = itemRenderer;
		this.texture = texture;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.u = u;
		this.v = v;
		this.scale = 1.0F;
		this.ingredients = NonNullList.withSize(4, ItemStack.EMPTY).toArray(new ItemStack[4]);
	}

	public DrawRecipePageModule withRecipe(final RecipeManager recipeManager, final ResourceLocation recipe) {
		return this.withRecipe(loadRecipe(recipeManager, recipe));
	}

	public DrawRecipePageModule withRecipe(final CraftingRecipe recipe) {
		this.recipe = recipe;
		for(int i = 0; i < 4; i++) {
			this.ingredients[i] = ItemStack.EMPTY;
		}
		return this.withTick(0);
	}

	public DrawRecipePageModule withScale(final float scale) {
		this.scale = scale;
		return this;
	}

	@Nullable
	public static CraftingRecipe loadRecipe(final RecipeManager recipeManager, final ResourceLocation recipe) {
		final Optional<? extends Recipe<?>> oRecipe = recipeManager.byKey(recipe);
		if(oRecipe.isPresent() && oRecipe.get() instanceof CraftingRecipe craftingRecipe && craftingRecipe.canCraftInDimensions(2, 2)) {
			return craftingRecipe;
		}
		return null;
	}

	public DrawRecipePageModule withTick(int ticksOpen) {
		if (recipe != null) {
			final List<Ingredient> recipeIngredients = recipe.getIngredients();
			for (int i = 0; i < 4; i++) {
				if (i >= recipeIngredients.size()) {
					ingredients[i] = ItemStack.EMPTY;
					continue;
				}
				ItemStack[] items = recipeIngredients.get(i).getItems();
				ingredients[i] = items[(ticksOpen / 30) % items.length];
			}
		}
		return this;
	}

	@Override
	public void render(Screen parent, PoseStack poseStack, float partialTicks) {
		drawBasicPage(poseStack, title, body);
		drawRecipe(parent, poseStack);
		drawPageNum(poseStack);
	}

	protected void drawRecipe(final Screen parent, final PoseStack matrix) {
		final int startX = x + margin * 2;
		final int startY = y + margin * 2;
		final int frameWidth = 3;
		/** texture location and size of 2x2 crafting **/
		matrix.pushPose();
		matrix.scale(scale, scale, scale);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		// draw 2x2 grid background
		RenderSystem.setShaderTexture(0, texture);
		parent.blit(matrix, startX, startY, u, v, imageWidth, imageHeight);

		// draw itemstacks
		if(null == recipe) {
			return;
		}
		Lighting.setupForFlatItems();
		float posX;
		float posY;
		int iconW = 15;

		switch (recipe.getIngredients().size()) {
			// intentional omission of break statements
			case 4:
				posX = startX + iconW + frameWidth * 3.0F;
				posY = startY  + iconW + frameWidth * 3.0F;
				this.itemRenderer.renderGuiItem(ingredients[3], (int) (posX / scale), (int) (posY / scale));
			case 3:
				posX = startX + frameWidth * 2.0F;
				posY = startY  + iconW + frameWidth * 3.0F;
				this.itemRenderer.renderGuiItem(ingredients[2], (int) (posX / scale), (int) (posY / scale));
			case 2:
				posX = startX + iconW + frameWidth * 3.0F;
				posY = startY  + frameWidth * 2.0F;
				this.itemRenderer.renderGuiItem(ingredients[1], (int) (posX / scale), (int) (posY / scale));
			case 1:
				posX = startX + frameWidth * 2.0F;
				posY = startY  + frameWidth * 2.0F;
				this.itemRenderer.renderGuiItem(ingredients[0], (int) (posX / scale), (int) (posY / scale));
			default:
				break;
		}

		// draw result itemstack
		posX = startX + imageWidth - 16.0F - frameWidth * 2.0F;
		posY = startY  + 16.0F;
		this.itemRenderer.renderGuiItem(recipe.getResultItem(), (int) (posX / scale), (int) (posY / scale));
		this.itemRenderer.renderGuiItemDecorations(font, recipe.getResultItem(), (int) (posX / scale), (int) (posY / scale));
		// reset scale
		matrix.popPose();
	}

}
