package com.mcmoddev.golems.client.menu.guide_book;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.menu.guide_book.page.BookPage;
import com.mcmoddev.golems.client.menu.guide_book.page.CraftingRecipePage;
import com.mcmoddev.golems.client.menu.guide_book.page.GolemDescriptionPage;
import com.mcmoddev.golems.client.menu.guide_book.page.GolemDiagramPage;
import com.mcmoddev.golems.client.menu.guide_book.page.TableOfContentsPage;
import com.mcmoddev.golems.client.menu.guide_book.page.TitleAndBodyPage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class GuideBook {

	protected static final Component INTRO_TITLE = Component.translatable("item.golems.guide_book").withStyle(ChatFormatting.ITALIC);
	protected static final Component INTRO_BODY = Component.translatable("golembook.intro1").append("\n").append(Component.translatable("golembook.intro2"));

	protected static final Component CONTENTS_TITLE = Component.translatable("golembook.contents.title").withStyle(ChatFormatting.ITALIC);

	protected static final Component BUILD_GOLEM_TITLE = Component.translatable("golembook.build_golem.title").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_GOLEM_BODY = Component.translatable("golembook.build_golem.howto1").append(" ")
			.append(Component.translatable("golembook.build_golem.howto2")).append("\n\n")
			.append(Component.translatable("golembook.build_golem.howto3", Component.translatable("block.golems.golem_head")));

	protected static final Component BUILD_HEAD_TITLE = Component.translatable("block.golems.golem_head").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_HEAD_BODY = Component.literal("\n\n\n\n")
			.append(Component.translatable("golembook.recipe_head.recipe", BUILD_HEAD_TITLE,
					Component.translatable("item.golems.golem_spell"), Component.translatable("block.minecraft.pumpkin")));

	protected static final Component BUILD_SPELL_TITLE = Component.translatable("item.golems.golem_spell").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_SPELL_BODY = Component.literal("\n\n\n\n")
			.append(Component.translatable("golembook.recipe_spell.recipe", BUILD_SPELL_TITLE,
					Component.translatable("item.minecraft.paper"), Component.translatable("item.minecraft.feather"),
					Component.translatable("item.minecraft.ink_sac"), Component.translatable("item.minecraft.redstone")));

	protected static final ResourceLocation SPELL_RECIPE = new ResourceLocation(ExtraGolems.MODID, "golem_spell");
	protected static final ResourceLocation HEAD_RECIPE = new ResourceLocation(ExtraGolems.MODID, "golem_head");

	private final List<BookPage> pages;

	public GuideBook(final List<GuideBookGroup> groups, final IBookScreen screen, final int x, final int y, final int pageWidth, final int pageHeight) {
		// prepare to create book sections
		final ImmutableList.Builder<BookPage> pages = ImmutableList.builder();
		int page = 0;
		// add introduction section
		pages.add(new TitleAndBodyPage.Builder(screen)
				.title(INTRO_TITLE)
				.body(INTRO_BODY)
				.build());
		page++;
		// add table of contents section
		pages.add(new TableOfContentsPage.Builder(screen, groups, screen::setPageIndex)
				.title(CONTENTS_TITLE)
				.build());
		page++;
		// add spell crafting section
		final RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		final Optional<CraftingRecipe> oSpellRecipe = loadRecipe(recipeManager, SPELL_RECIPE);
		if(oSpellRecipe.isPresent()) {
			pages.add(new CraftingRecipePage.Builder(screen, oSpellRecipe.get()).build());
			page++;
		}
		// add golem head crafting section
		final Optional<CraftingRecipe> oHeadRecipe = loadRecipe(recipeManager, HEAD_RECIPE);
		if(oHeadRecipe.isPresent()) {
			pages.add(new CraftingRecipePage.Builder(screen, oHeadRecipe.get()).build());
			page++;
		}
		// add build instructions section
		pages.add(new TitleAndBodyPage.Builder(screen)
				.title(BUILD_GOLEM_TITLE)
				.body(BUILD_GOLEM_BODY)
				.build());
		// add build diagram section
		pages.add(new GolemDiagramPage.Builder(screen).build());
		// add guide book group sections
		for(GuideBookGroup group : groups) {
			boolean hasTableOfContents = group.getList().size() > 1;
			if(hasTableOfContents) {
				// check if blank page is needed to ensure table of contents is always on an even number page
				if(page % 2 != 0) {
					pages.add(new TitleAndBodyPage.Builder(screen)
							.pos(x + pageWidth, y)
							.dimensions(pageWidth, pageHeight)
							.build());
					page++;
				}
				// create description page
				GolemDescriptionPage descriptionPage = (GolemDescriptionPage) new GolemDescriptionPage.Builder(screen, group)
						.pos(x + pageWidth, y)
						.dimensions(pageWidth, pageHeight)
						.build();
				// create table of contents page and link it to description page
				BookPage tableOfContents = new TableOfContentsPage.Builder(screen, group.getList(), descriptionPage::setEntryIndex)
						.pos(x, y)
						.dimensions(pageWidth, pageHeight)
						.build();
				// add table of contents page first, then add description page
				pages.add(tableOfContents);
				pages.add(descriptionPage);
				page += 2;
			} else {
				// no table of contents, add simple description page instead
				pages.add(new GolemDescriptionPage.Builder(screen, group)
						.pos(x + (page % 2) * pageWidth, y)
						.dimensions(pageWidth, pageHeight)
						.build());
				page++;
			}
		}

		// ensure book has even number of pages
		if(page % 2 != 0) {
			pages.add(new TitleAndBodyPage.Builder(screen)
					.pos(x + pageWidth, y)
					.dimensions(pageWidth, pageHeight)
					.build());
			page++;
		}

		// build list of pages
		this.pages = pages.build();
	}

	private static Optional<CraftingRecipe> loadRecipe(final RecipeManager recipeManager, final ResourceLocation recipe) {
		final Optional<? extends Recipe<?>> oRecipe = recipeManager.byKey(recipe);
		if(oRecipe.isPresent() && oRecipe.get() instanceof CraftingRecipe craftingRecipe && craftingRecipe.canCraftInDimensions(2, 2)) {
			return Optional.of(craftingRecipe);
		}
		return Optional.empty();
	}

	@Nullable
	public BookPage getPage(int page) {
		if(page < 0 || page >= pages.size()) {
			return null;
		}
		return pages.get(page);
	}

	public int getPageCount() {
		return this.pages.size();
	}
}
