package com.mcmoddev.golems.client.menu.guide_book.book;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.client.menu.guide_book.GuideBookGroup;
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
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class GuideBook {

	protected static final String PREFIX = "item.golems.guide_book.page.";

	protected static final Component INTRO_TITLE = Component.translatable("item.golems.guide_book").withStyle(ChatFormatting.ITALIC);
	protected static final Component INTRO_BODY = Component.translatable(PREFIX + "intro1").append("\n").append(Component.translatable(PREFIX + "intro2"));

	protected static final Component CONTENTS_TITLE = Component.translatable(PREFIX + "contents.title").withStyle(ChatFormatting.ITALIC);

	protected static final Component BUILD_GOLEM_TITLE = Component.translatable(PREFIX + "build_golem.title").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_GOLEM_BODY = Component.translatable(PREFIX + "build_golem.howto1").append(" ")
			.append(Component.translatable(PREFIX + "build_golem.howto2")).append("\n\n")
			.append(Component.translatable(PREFIX + "build_golem.howto3", Component.translatable("block.golems.golem_head")));

	protected static final Component BUILD_SPELL_TITLE = Component.translatable("item.golems.golem_spell").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_SPELL_BODY = Component.literal("\n\n\n\n")
			.append(Component.translatable(PREFIX + "recipe_spell.recipe", BUILD_SPELL_TITLE,
					Component.translatable("block.minecraft.carved_pumpkin").withStyle(ChatFormatting.ITALIC)));

	protected static final Component BUILD_HEAD_TITLE = Component.translatable("block.golems.golem_head").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_HEAD_BODY = Component.literal("\n\n\n\n")
			.append(Component.translatable(PREFIX + "recipe_head.recipe", BUILD_HEAD_TITLE));

	protected static final ResourceLocation SPELL_RECIPE = new ResourceLocation(ExtraGolems.MODID, "golem_spell");
	protected static final ResourceLocation HEAD_RECIPE = new ResourceLocation(ExtraGolems.MODID, "golem_head");

	private final int x;
	private final int y;
	private final int pageWidth;
	private final int pageHeight;

	private final List<BookPage> pages;

	public GuideBook(final IBookScreen screen, final List<GuideBookGroup> groups, final int x, final int y, final int pageWidth, final int pageHeight) {
		this.x = x;
		this.y = y;
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;

		// build all pages
		this.pages = ImmutableList.copyOf(createBookPages(screen, groups));

		// hide all pages
		for(BookPage p : this.pages) {
			p.onHide(screen);
		}
	}

	private List<BookPage> createBookPages(final IBookScreen screen, List<GuideBookGroup> groups) {
		// create table of contents map
		final Map<GuideBookGroup, Integer> tableOfContents = new HashMap<>();
		// prepare to create book sections
		final ImmutableList.Builder<BookPage> pages = ImmutableList.builder();
		int page = 0;

		// add introduction section
		pages.add(new TitleAndBodyPage.Builder(screen, page++)
				.title(INTRO_TITLE)
				.body(INTRO_BODY)
				.pos(x, y)
				.dimensions(pageWidth, pageHeight)
				.build());

		// add table of contents section
		final List<GuideBookGroup> sortedByName = ImmutableList.sortedCopyOf(GuideBookGroup.SORT_BY_NAME, groups);
		pages.add(new TableOfContentsPage.Builder(screen, page++, sortedByName, i -> screen.setPageIndex(tableOfContents.getOrDefault(sortedByName.get(i), 0)))
				.title(CONTENTS_TITLE)
				.pos(x + pageWidth, y)
				.dimensions(pageWidth, pageHeight)
				.build());

		// add crafting sections
		final RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		final Optional<CraftingRecipe> oSpellRecipe = loadRecipe(recipeManager, SPELL_RECIPE);
		final Optional<CraftingRecipe> oHeadRecipe = loadRecipe(recipeManager, HEAD_RECIPE);
		if(oSpellRecipe.isPresent() && oHeadRecipe.isPresent()) {
			pages.add(new CraftingRecipePage.Builder(screen, page++, oSpellRecipe.get())
					.title(BUILD_SPELL_TITLE)
					.body(BUILD_SPELL_BODY)
					.pos(x, y)
					.dimensions(pageWidth, pageHeight)
					.build());
			pages.add(new CraftingRecipePage.Builder(screen, page++, oHeadRecipe.get())
					.title(BUILD_HEAD_TITLE)
					.body(BUILD_HEAD_BODY)
					.pos(x + pageWidth, y)
					.dimensions(pageWidth, pageHeight)
					.build());
		}

		// add build instructions section
		pages.add(new TitleAndBodyPage.Builder(screen, page++)
				.title(BUILD_GOLEM_TITLE)
				.body(BUILD_GOLEM_BODY)
				.pos(x, y)
				.dimensions(pageWidth, pageHeight)
				.build());

		// add build diagram section
		pages.add(new GolemDiagramPage.Builder(screen, page++)
				.pos(x + pageWidth, y)
				.dimensions(pageWidth, pageHeight)
				.build());

		// create queue for groups that have a table of contents.
		// groups with tables of contents must start on an even page.
		// we could add a blank page before the groups that start on odd pages,
		// or use this queue to fill in the gaps with single-page entries.
		final Queue<GuideBookGroup> tabulatedGroups = new LinkedList<>();
		// add guide book group sections
		for(GuideBookGroup group : groups) {
			// check if this group has a table of contents
			boolean hasTableOfContents = group.getList().size() > 1;
			if(hasTableOfContents) {
				// add to the queue
				tabulatedGroups.add(group);
			} else {
				// add directly to pages
				pages.add(new GolemDescriptionPage.Builder(screen, page, group)
						.pos(x + (page % 2) * pageWidth, y)
						.dimensions(pageWidth, pageHeight)
						.build());
				// add entry to table of contents
				tableOfContents.put(group, page);
				// update page
				page++;
			}
			// check if any enqueued groups can be added to an even number page
			while(!tabulatedGroups.isEmpty() && page % 2 == 0) {
				GuideBookGroup qGroup = tabulatedGroups.remove();
				Tuple<BookPage, BookPage> addedPages = groupedGolemDescriptionPage(qGroup, screen, page);
				// add table of contents page first, then add description page
				pages.add(addedPages.getA());
				pages.add(addedPages.getB());
				// add entry to table of contents
				tableOfContents.put(qGroup, page);
				// update page
				page += 2;
			}
		}

		// ensure book has even number of pages
		if(page % 2 != 0) {
			pages.add(blankPage(screen, page, x + pageWidth, y));
			page++;
		}

		// add remaining elements from queue, if any
		while(!tabulatedGroups.isEmpty()) {
			GuideBookGroup qGroup = tabulatedGroups.remove();
			Tuple<BookPage, BookPage> addedPages = groupedGolemDescriptionPage(qGroup, screen, page);
			// add table of contents page first, then add description page
			pages.add(addedPages.getA());
			pages.add(addedPages.getB());
			// add entry to table of contents
			tableOfContents.put(qGroup, page);
			// update page
			page += 2;
		}

		// build list of pages
		return pages.build();
	}

	/**
	 * Adds a table of contents page and golem description page (total 2 pages)
	 * @param group the guide book group
	 * @param screen the parent screen
	 * @param page the page number
	 * @return the table of contents page and golem description page, in that order
	 */
	private Tuple<BookPage, BookPage> groupedGolemDescriptionPage(final GuideBookGroup group, final IBookScreen screen, final int page) {
		// create description page
		GolemDescriptionPage descriptionPage = (GolemDescriptionPage) new GolemDescriptionPage.Builder(screen, page + 1, group)
				.pos(x + pageWidth, y)
				.dimensions(pageWidth, pageHeight)
				.build();
		// create table of contents page and link it to description page
		BookPage tableOfContentsPage = new TableOfContentsPage.Builder(screen, page, group.getList(), descriptionPage::setEntryIndex)
				.title(group.getTitle())
				.pos(x, y)
				.dimensions(pageWidth, pageHeight)
				.build();
		return new Tuple<>(tableOfContentsPage, descriptionPage);
	}

	/**
	 * @param screen the parent screen
	 * @param page the page number
	 * @param x the x position
	 * @param y the y position
	 * @return a newly constructed blank page
	 */
	private BookPage blankPage(final IBookScreen screen, final int page, final int x, final int y) {
		return new BookPage.Builder(screen, page).pos(x, y).dimensions(pageWidth, pageHeight).build();
	}

	private static Optional<CraftingRecipe> loadRecipe(final RecipeManager recipeManager, final ResourceLocation recipe) {
		final Optional<? extends Recipe<?>> oRecipe = recipeManager.byKey(recipe);
		if(oRecipe.isPresent() && oRecipe.get() instanceof CraftingRecipe craftingRecipe && craftingRecipe.canCraftInDimensions(2, 2)) {
			return Optional.of(craftingRecipe);
		}
		return Optional.empty();
	}

	public List<BookPage> getPages() {
		return this.pages;
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
