package com.mcmoddev.golems.screen.guide_book;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.screen.guide_book.button.BlockButton;
import com.mcmoddev.golems.screen.guide_book.button.GolemEntryButton;
import com.mcmoddev.golems.screen.guide_book.button.ScrollButton;
import com.mcmoddev.golems.screen.guide_book.module.DrawBlockModule;
import com.mcmoddev.golems.screen.guide_book.module.DrawDiagramPageModule;
import com.mcmoddev.golems.screen.guide_book.module.DrawEntryPageModule;
import com.mcmoddev.golems.screen.guide_book.module.DrawPageModule;
import com.mcmoddev.golems.screen.guide_book.module.DrawRecipePageModule;
import com.mcmoddev.golems.screen.guide_book.module.DrawTableOfContentsPageModule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GolemBookScreen extends Screen implements ScrollButton.IScrollListener {

	protected static final float GOLEM_BLOCK_SCALE = 1.60F;
	protected static final int MARGIN = 12;
	protected static final int INTRO_PAGE_COUNT = 6;
	// there are six entries showing at a time
	protected static final int GOLEM_BOOK_ENTRY_COUNT = 5;

	protected static final int CONTENTS_WIDTH = 106;
	protected static final int CONTENTS_HEIGHT = 112;
	protected static final int SCROLL_X = MARGIN;
	protected static final int SCROLL_Y = MARGIN * 2;

	// The texture used by the majority of this gui
	protected static final ResourceLocation TEXTURE = new ResourceLocation(ExtraGolems.MODID, "textures/gui/info_book.png");
	protected static final ResourceLocation CONTENTS = new ResourceLocation(ExtraGolems.MODID, "textures/gui/info_book_contents.png");

	protected static final Component INTRO_TITLE = Component.translatable("item.golems.info_book").withStyle(ChatFormatting.ITALIC);
	protected static final Component INTRO_PAGE = Component.translatable("golembook.intro1").append("\n").append(Component.translatable("golembook.intro2"));

	protected static final Component CONTENTS_TITLE = Component.translatable("golembook.contents.title").withStyle(ChatFormatting.ITALIC);

	protected static final Component BUILD_GOLEM_TITLE = Component.translatable("golembook.build_golem.title").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_GOLEM_PAGE = Component.translatable("golembook.build_golem.howto1").append(" ")
			.append(Component.translatable("golembook.build_golem.howto2")).append("\n\n")
			.append(Component.translatable("golembook.build_golem.howto3", Component.translatable("block.golems.golem_head")));

	protected static final Component BUILD_HEAD_TITLE = Component.translatable("block.golems.golem_head").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_HEAD_PAGE = Component.literal("\n\n\n\n")
			.append(Component.translatable("golembook.recipe_head.recipe", BUILD_HEAD_TITLE,
					Component.translatable("item.golems.golem_spell"), Component.translatable("block.minecraft.pumpkin")));

	protected static final Component BUILD_SPELL_TITLE = Component.translatable("item.golems.golem_spell").withStyle(ChatFormatting.ITALIC);
	protected static final Component BUILD_SPELL_PAGE = Component.literal("\n\n\n\n")
			.append(Component.translatable("golembook.recipe_spell.recipe", BUILD_SPELL_TITLE,
					Component.translatable("item.minecraft.paper"), Component.translatable("item.minecraft.feather"),
					Component.translatable("item.minecraft.ink_sac"), Component.translatable("item.minecraft.redstone")));

	// book texture has these dimensions
	protected static final int BOOK_HEIGHT = 164;
	protected static final int BOOK_WIDTH = 256;
	protected static final int ENTRY_WIDTH = 88;
	protected static final int ENTRY_HEIGHT = 22;
	// icons are this many pixels apart in the textures
	protected static final int ICON_SP = 5;

	// how far down the Minecraft screen the book starts
	protected static final int SCR_OFFSET_Y = 16;
	// width of the arrow button and its texture
	protected static final int ARROW_WIDTH = 18;
	protected static final int ARROW_HEIGHT = 10;
	/**
	 * size of the supplemental image for each entry, if one is present.
	 * Any image with a 2:1 ratio will render with no issues.
	 **/
	protected static final int GOLEM_PAGE_IMAGE_WIDTH = 100;
	protected static final int GOLEM_PAGE_IMAGE_HEIGHT = 50;

	private Button doneBtn;
	// If a Block is displayed on the LEFT side, this helps with the tooltip
	private BlockButton leftBlockBtn;
	// If a Block is displayed on the RIGHT side, this helps with the tooltip
	private BlockButton rightBlockBtn;
	// Increments the page number by 2
	private Button nextPageBtn;
	// Decrements the page number by 2
	private Button prevPageBtn;
	// Clickable link to each entity, used on page 2
	private final GolemEntryButton[] tableOfContentsBtns;

	protected final List<GolemBookEntry> golemBookEntryList = new ArrayList<>();
	protected final List<Tuple<GolemBookEntry, Integer>> golemBookEntryListSorted = new ArrayList<>();
	protected int page;
	protected int totalPages;

	protected ScrollButton scrollButton;
	protected int scrollOffset;
	/** in-game tick counter for when GUI is paused **/
	protected long ticksOpen;

	//// MODULES ////
	protected DrawBlockModule drawBlockModule;
	protected DrawEntryPageModule drawdrawEntryPageModule;
	protected DrawPageModule drawPageModule;
	protected DrawDiagramPageModule drawDiagramPageModule;
	protected DrawRecipePageModule drawRecipePageModule;
	protected DrawTableOfContentsPageModule drawTableOfContentsPageModule;

	// for use in drawing entity spell recipe
	private static final ResourceLocation SPELL_RECIPE = new ResourceLocation(ExtraGolems.MODID, "golem_spell");
	private static final ResourceLocation HEAD_RECIPE = new ResourceLocation(ExtraGolems.MODID, "golem_head");
	private final CraftingRecipe spellRecipe;
	private final CraftingRecipe headRecipe;

	public GolemBookScreen(Player playerIn, ItemStack itemIn) {
		super(EGRegistry.ItemReg.GUIDE_BOOK.get().getDescription());
		this.initGolemBookEntries();
		// init variables
		this.spellRecipe = DrawRecipePageModule.loadRecipe(playerIn.level().getRecipeManager(), SPELL_RECIPE);
		this.headRecipe = DrawRecipePageModule.loadRecipe(playerIn.level().getRecipeManager(), HEAD_RECIPE);
		this.page = 0;
		this.totalPages = INTRO_PAGE_COUNT + golemBookEntryList.size();
		this.tableOfContentsBtns = new GolemEntryButton[GOLEM_BOOK_ENTRY_COUNT];
		this.ticksOpen = 0L;
		this.scrollOffset = 0;
	}

	@Override
	public void init() {
		// init modules
		this.drawBlockModule = new DrawBlockModule(MARGIN);
		this.drawPageModule = new DrawPageModule(this.font, BOOK_WIDTH, BOOK_HEIGHT, MARGIN);
		this.drawDiagramPageModule = new DrawDiagramPageModule(drawBlockModule, this.font, BOOK_WIDTH, BOOK_HEIGHT, MARGIN);
		this.drawRecipePageModule = new DrawRecipePageModule(this.font, BOOK_WIDTH, BOOK_HEIGHT, MARGIN, CONTENTS, 84, 46, 111, 54);
		this.drawTableOfContentsPageModule = new DrawTableOfContentsPageModule(this.font, BOOK_WIDTH, BOOK_HEIGHT, MARGIN, CONTENTS, 0, 0, CONTENTS_WIDTH, CONTENTS_HEIGHT);
		this.drawdrawEntryPageModule = new DrawEntryPageModule(this.font, BOOK_WIDTH, BOOK_HEIGHT, MARGIN, GolemBookEntry.IMAGE_WIDTH, GolemBookEntry.IMAGE_HEIGHT);

		// Scroll button
		this.scrollButton = this.addRenderableWidget(new ScrollButton(Button.builder(Component.empty(), b -> {})
				.pos((width / 2) + MARGIN + CONTENTS_WIDTH - 14, MARGIN * 3 + 4).size(12, CONTENTS_HEIGHT - 2),
				CONTENTS, 0, 115, 12, 15, 15, true,
				1.0F / (golemBookEntryList.size() - GOLEM_BOOK_ENTRY_COUNT), this));

		// Done button
		int doneBtnWidth = 98;
		this.doneBtn = this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> this.minecraft.setScreen(null))
				.pos((this.width - doneBtnWidth) / 2, BOOK_HEIGHT + SCR_OFFSET_Y + 8)
				.size(98, 20)
				.build());
		// Previous Page button
		int arrowX = (this.width - BOOK_WIDTH) / 2;
		int arrowY = SCR_OFFSET_Y + BOOK_HEIGHT - ARROW_HEIGHT * 2;
		this.prevPageBtn = this.addRenderableWidget(new ImageButton(arrowX + ARROW_WIDTH, arrowY, ARROW_WIDTH, ARROW_HEIGHT,
				23, 169, ARROW_HEIGHT, TEXTURE, b -> addPage(-2)));
		// Next Page button
		this.nextPageBtn = this.addRenderableWidget(new ImageButton(arrowX + BOOK_WIDTH - ARROW_WIDTH * 2, arrowY, ARROW_WIDTH, ARROW_HEIGHT,
				0, 169, ARROW_HEIGHT, TEXTURE, b -> addPage(2)));
		// Block buttons
		int blockX = ((this.width - BOOK_WIDTH) / 2) + MARGIN + 4;
		int blockY = SCR_OFFSET_Y + MARGIN;
		this.leftBlockBtn = this.addRenderableWidget(new BlockButton(this, drawBlockModule, new Block[]{}, blockX, blockY, MARGIN, GOLEM_BLOCK_SCALE));
		blockX = (this.width / 2) + MARGIN;
		this.rightBlockBtn = this.addRenderableWidget(new BlockButton(this, drawBlockModule, new Block[]{}, blockX, blockY, MARGIN, GOLEM_BLOCK_SCALE));
		// Golem Entry buttons in table of contents
		for (int i = 0, l = Math.min(GOLEM_BOOK_ENTRY_COUNT, golemBookEntryListSorted.size()), btnX = (this.width / 2) + SCROLL_X, btnY; i < l; i++) {
			final Button.OnPress onPress = b -> {
				int page = ((GolemEntryButton) b).getPage();
				GolemBookScreen.this.setPage(page);
				GolemBookScreen.this.updateButtons();
			};
			btnY = SCR_OFFSET_Y + SCROLL_Y + ENTRY_HEIGHT * i;
			this.tableOfContentsBtns[i] = this.addRenderableWidget(new GolemEntryButton(this, this.font, drawBlockModule,
					btnX, btnY, ENTRY_WIDTH, ENTRY_HEIGHT, MARGIN, CONTENTS, 111, 0, ENTRY_HEIGHT, this::getTicksOpen, onPress));
		}
		this.scrollButton.setScrollPercent(0.0F);
		this.updateButtons();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void tick() {
		super.tick();
		++ticksOpen;

		if(leftBlockBtn != null) {
			leftBlockBtn.tick(this, (int) ticksOpen);
		}
		if(rightBlockBtn != null) {
			rightBlockBtn.tick(this, (int) ticksOpen);
		}
	}

	public long getTicksOpen() {
		return ticksOpen;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		// draw background
		int bookX = (this.width - BOOK_WIDTH) / 2;
		int bookY = SCR_OFFSET_Y;
		graphics.blit(TEXTURE, bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);

		// draw pages: left and right
		this.drawPageAt(graphics, bookX + 1, bookY, this.page, partialTicks);
		this.drawPageAt(graphics, bookX + (BOOK_WIDTH / 2) - 2, bookY, this.page + 1, partialTicks);

		// draw buttons, etc.
		super.render(graphics, mouseX, mouseY, partialTicks);
	}

	/**
	 * Uses the given page number to calculate which page to draw and calls the
	 * appropriate methods to do so.
	 *
	 * @param graphics       the graphics manager
	 * @param cornerX      the left corner of the page
	 * @param cornerY      the upper corner of the page
	 * @param pageNum      the page to draw
	 * @param partialTicks the partial tick count
	 **/
	private void drawPageAt(final GuiGraphics graphics, final int cornerX, final int cornerY, final int pageNum, final float partialTicks) {
		// declare these for the following switch statement
		Component title;
		Component body;
		// using the page number, decides which page to draw and how to draw it
		switch (pageNum) {
			case 0:
				drawPageModule
						.withPage(pageNum)
						.withTitle(INTRO_TITLE)
						.withBody(INTRO_PAGE)
						.withPos(cornerX, cornerY)
						.render(this, graphics, partialTicks);
				return;
			case 1:
				// draw Table of Contents
				drawTableOfContentsPageModule
						.withPage(pageNum)
						.withTitle(CONTENTS_TITLE)
						.withPos(cornerX, cornerY)
						.render(this, graphics, partialTicks);
				return;
			case 2:
				// draw Golem Spell instructions
				drawRecipePageModule
						.withRecipe(spellRecipe)
						.withTick((int) ticksOpen)
						.withScale(1.0F)
						.withPage(pageNum)
						.withTitle(BUILD_SPELL_TITLE)
						.withBody(BUILD_SPELL_PAGE)
						.withPos(cornerX, cornerY)
						.render(this, graphics, partialTicks);
				return;
			case 3:
				// draw Golem Head instructions
				drawRecipePageModule
						.withRecipe(headRecipe)
						.withTick((int) ticksOpen)
						.withScale(1.0F)
						.withPage(pageNum)
						.withTitle(BUILD_HEAD_TITLE)
						.withBody(BUILD_HEAD_PAGE)
						.withPos(cornerX, cornerY)
						.render(this, graphics, partialTicks);
				return;
			case 4:
				// draw Make Golem instructions
				drawPageModule
						.withPage(pageNum)
						.withTitle(BUILD_GOLEM_TITLE)
						.withBody(BUILD_GOLEM_PAGE)
						.withPos(cornerX, cornerY)
						.render(this, graphics, partialTicks);
				return;
			case 5:
				// draw Golem diagram
				drawDiagramPageModule
						.withPage(pageNum)
						.withPos(cornerX, cornerY)
						.render(this, graphics, partialTicks);
				return;
			case 6:
			default:
				// draw entity entry
				if (isPageGolemEntry(pageNum, this.totalPages)) {
					GolemBookEntry entry = getGolemEntryForPage(pageNum);
					drawdrawEntryPageModule
							.withEntry(entry)
							.withPage(pageNum)
							.withPos(cornerX, cornerY)
							.render(this, graphics, partialTicks);
				}
				return;
		}
	}

	protected void addPage(final int pages) {
		this.page += pages;
		updateButtons();
	}

	protected void setPage(final int page) {
		this.page = page;
		updateButtons();
	}

	/**
	 * Used to determine whether to show buttons
	 **/
	protected void updateButtons() {
		// next page arrows
		this.prevPageBtn.visible = this.page > 0;
		this.nextPageBtn.visible = this.page + 2 < this.totalPages;
		// table of contents buttons
		boolean tableContentsVisible = isPageTableContents(this.page);
		for (Button b : this.tableOfContentsBtns) {
			b.visible = tableContentsVisible;
		}
		this.scrollButton.visible = tableContentsVisible;
		// entity-entry block buttons
		if (isPageGolemEntry(this.page, this.totalPages)) {
			this.leftBlockBtn.visible = true;
			this.leftBlockBtn.updateBlocks(getGolemEntryForPage(this.page).getBlocks());
		} else {
			this.leftBlockBtn.visible = false;
		}
		if (isPageGolemEntry(this.page + 1, this.totalPages)) {
			this.rightBlockBtn.visible = true;
			this.rightBlockBtn.updateBlocks(getGolemEntryForPage(this.page + 1).getBlocks());
		} else {
			this.rightBlockBtn.visible = false;
		}
	}

	private static boolean isPageGolemEntry(final int page, final int totalPages) {
		return page >= INTRO_PAGE_COUNT && page < totalPages;
	}

	private static boolean isPageTableContents(final int page) {
		return page >= 0 && page < 2;
	}

	/**
	 * Populates the GolemEntry list to use in book gui
	 **/
	public final void initGolemBookEntries() {
		golemBookEntryList.clear();
		final Registry<GolemContainer> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(ExtraGolems.Keys.GOLEM_CONTAINERS);
		for (Map.Entry<ResourceKey<GolemContainer>, GolemContainer> entry : registry.entrySet()) {
			if (!entry.getValue().isHidden()) {
				golemBookEntryList.add(new GolemBookEntry(entry.getKey().location(), entry.getValue()));
			}
		}
		// sort golems by attack power
		Collections.sort(golemBookEntryList, (g1, g2) -> Float.compare(g1.getAttack(), g2.getAttack()));

		// make and sort alphabetical list
		golemBookEntryListSorted.clear();
		final List<GolemBookEntry> naturalOrderList = new ArrayList<>(golemBookEntryList);
		Collections.sort(naturalOrderList, Comparator.comparing(g -> g.getGolemName().getString()));
		// add alphabetical list and page entries to sorted list
		for(GolemBookEntry entry : naturalOrderList) {
			// determine lowest even number page that corresponds to this entry
			int page = INTRO_PAGE_COUNT + (golemBookEntryList.indexOf(entry) >> 1) * 2;
			golemBookEntryListSorted.add(new Tuple<>(entry, page));
		}
	}

	public GolemBookEntry getGolemEntryForPage(final int page) {
		return golemBookEntryList.get(page - INTRO_PAGE_COUNT);
	}

	//// SCROLL BAR /////


	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if(isPageTableContents(page)) {
			return scrollButton.mouseScrolled(mouseX, mouseY, amount);
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if(button == 0 && scrollButton != null && scrollButton.isDragging()) {
			scrollButton.onDrag(mouseX, mouseY, dragX, dragY);
			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void onScroll(ScrollButton button, float percent) {
		scrollOffset = Mth.floor(percent * (golemBookEntryListSorted.size() - GOLEM_BOOK_ENTRY_COUNT));
		updateButtons();
		if(tableOfContentsBtns != null && tableOfContentsBtns.length > 0 && !golemBookEntryListSorted.isEmpty()) {
			for(int i = 0, n = GOLEM_BOOK_ENTRY_COUNT; i < n; i++) {
				boolean outOfBounds = n >= golemBookEntryListSorted.size();
				if(outOfBounds) {
					this.tableOfContentsBtns[i].visible = false;
				} else {
					Tuple<GolemBookEntry, Integer> tuple = golemBookEntryListSorted.get(scrollOffset + i);
					this.tableOfContentsBtns[i].setEntry(tuple.getA(), tuple.getB());
				}
			}
		}
	}
}
