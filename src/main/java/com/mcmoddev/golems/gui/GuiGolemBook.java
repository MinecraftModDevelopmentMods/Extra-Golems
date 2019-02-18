package com.mcmoddev.golems.gui;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiGolemBook extends GuiScreen {
	
	//////////// GUI GOLEM BOOK LAYOUT ////////////
	//[X] 2-Page Layout:
	////// [X] Each screen will have a double-version of the
	////// default Minecraft Book GUI. 
	////// [X] There will be arrows
	////// for "Previous Page" and "Next Page" but each one
	////// will actually change the page count by (2) because
	////// we show 2 pages at a time.
	//[ ] Buttons:
	////// [X] Like Minecraft book, there is a "Done" button below.
	////// [X] Also the Previous/Next Page buttons as described above.
	////// [ ] Button to jump to page 1
	//[X] PAGE 1 (Left): WELCOME
	////// [X] Small blurb about golems
	//[X] PAGE 2 (Right): TABLE OF CONTENTS
	////// [X] Lists the name of each Golem in the book.
	////// [X] Click-able (linked) list of all Golem names, maybe
	////// with an icon of their building block.
	////// [X] Clicking that name will jump you to the correct page.
	//[X] PAGE 3 (Left): GOLEM SPELL RECIPE
	////// [X] Name of item + Picture of crafting grid.
	//[X] PAGE 4 (Right): GOLEM HEAD RECIPE
	////// [X] Name of block + Picture of crafting grid.
	//[X] PAGE 5 (Left): HOW TO BUILD A GOLEM
	////// Verbal instructions on where to place blocks, etc.
	//[X] PAGE 6 (Right): IMAGE OF HOW TO BUILD A GOLEM
	////// [X] Diagram of 4 blocks in golem-shape and a Golem Head
	////// above them, indicating where to place the head.
	////// [X] Extra Fancy option:  blocks are 3d
	//[X] PAGES 7+ (Left and Right): GOLEM ENTRIES
	////// Defined in the GolemBookEntry object:
	////// [X] Large picture of Building Block on upper-left of page.
	////// [X] Hover-over text tells you the name of the Block.
	////// [X] Page number in upper-right: "Page x of xx"
	////// [X] Golem Name below page number, right-aligned
	////// [X] Attributes listed:  
	//////     Health and Attack
	//////     "Multi-Textured" prompt if Golem has multiple textures
	//////     "Fireproof" if the Golem is fireproof
	//////     All Golem Specials as defined in GolemBase#addSpecialDesc(List<String>)
	//////     (each entry as a separate line)
	
	///////////////////////////////////////////////

	// The texture used by the majority of this gui
	protected static final ResourceLocation TEXTURE = new ResourceLocation(ExtraGolems.MODID, "textures/gui/info_book.png");
	protected static final ResourceLocation CONTENTS = new ResourceLocation(ExtraGolems.MODID, "textures/gui/info_book_contents.png");

	// book texture has these dimensions
	protected static final int BOOK_HEIGHT = 164, BOOK_WIDTH = 256;
	// icons are this many pixels apart in the textures
	protected static final int DEF_SEP = 5;

	// how far down the Minecraft screen the book starts
	protected static final int SCR_OFFSET_Y = 16;
	// width of the arrow button and its texture
	protected static final int ARROW_WIDTH = 13 + DEF_SEP, ARROW_HEIGHT = 10 + DEF_SEP;
	/** 
	 * size of the supplemental (optional) image for each golem. 
	 * As long as it's a 2:1 ratio, it'll work 
	 **/
	protected static final int SUPP_WIDTH = 100, SUPP_HEIGHT = 50;

	// Button to exit GUI
	private GuiButton buttonDone;
	 // If a Block is displayed on the LEFT side, this helps with the tooltip
	private GuiGolemBook.BlockButton buttonBlockLeft;
	// If a Block is displayed on the RIGHT side, this helps with the tooltip
	private GuiGolemBook.BlockButton buttonBlockRight;
	// Increments the page number by 2
	private GuiGolemBook.NextPageButton buttonNextPage;
	// Decrements the page number by 2
	private GuiGolemBook.NextPageButton buttonPreviousPage;
	// Clickable link to each golem, used on page 2
	private GuiGolemBook.GolemEntryButton[] tableOfContents;
    
    protected int curPage;
    protected int totalPages;
    
    public static final List<GolemBookEntry> GOLEMS = new ArrayList();
    private static final List<GolemBookEntry> ALPHABETICAL = new ArrayList();
	private final EntityPlayer player;
	private final ItemStack book;
    
    private static final int idDone = 0;
	private static final int idNextPage = 1;
    private static final int idPrevPage = 2;
    private static final int idBlockLeft = 3;
    private static final int idBlockRight = 4;
    private static final int idTableContents = 5;
    
    private static final float GOLEM_BLOCK_SCALE = 1.60F;
    private static final int MARGIN = 12;
    private static final int NUM_PAGES_INTRO = 6;
    // there are six entries showing at a time
    private static final int NUM_CONTENTS_ENTRIES = 5;
    
    protected static final int CONTENTS_W = 106, CONTENTS_H = 110;
    protected static final int SCROLL_STARTX = MARGIN, SCROLL_STARTY = MARGIN * 2;
    protected static final int SCROLL_W = 12, SCROLL_H = 15;
    protected static final int ENTRY_W = 88, ENTRY_H = 22;
    /** Amount scrolled in Table of Contents (0 = top, 1 = bottom) */
    private float currentScroll;
    /** True if the scrollbar is being dragged */
    private boolean isScrolling;
    
    // for use in drawing golem spell recipe
    private static final ItemStack[] ingredientsSpell = new ItemStack[] 
			{ new ItemStack(Items.PAPER), new ItemStack(Items.FEATHER), 
			  new ItemStack(Items.REDSTONE), new ItemStack(Items.DYE, 1, EnumDyeColor.BLACK.getDyeDamage()) };
	private static final ItemStack outputSpell = new ItemStack(GolemItems.golemPaper);
	// for use in drawing golem head recipe
	private static final ItemStack[] ingredientsHead = new ItemStack[]
		{new ItemStack(Blocks.PUMPKIN), new ItemStack(GolemItems.golemPaper)};
	private static final ItemStack outputHead = new ItemStack(GolemItems.golemHead);
	
	public GuiGolemBook(EntityPlayer playerIn, ItemStack itemIn) {
		super();
		if (GOLEMS.isEmpty()) {
			initGolemBookEntries(playerIn.getEntityWorld());
		}
		this.player = playerIn;
		this.book = itemIn;
		this.curPage = 0;
		this.totalPages = NUM_PAGES_INTRO + GOLEMS.size();
		this.currentScroll = 0;
		this.isScrolling = false;
    	this.tableOfContents = new GuiGolemBook.GolemEntryButton[NUM_CONTENTS_ENTRIES];
	}

	/**
	 * Populates the GolemEntry list to use in book gui
	 **/
	private static final void initGolemBookEntries(World world) {
		GOLEMS.clear();
		for (GolemBase golem : ExtraGolems.PROXY.DUMMY_GOLEMS) {
			if(GolemBase.getConfig(golem).canSpawn()) {
				GOLEMS.add(new GolemBookEntry(golem));
			}
		}
		// sort golems by attack power
		Collections.sort(GOLEMS, (GolemBookEntry g1, GolemBookEntry g2) -> Float.compare(g1.getAttack(), g2.getAttack()));

		// make and sort alphabetical list
		ALPHABETICAL.clear();
		ALPHABETICAL.addAll(GOLEMS);
		Collections.sort(ALPHABETICAL, (GolemBookEntry g1, GolemBookEntry g2) -> g1.getGolemName().compareTo(g2.getGolemName()));
	}

	@Override
	public void initGui() {
		// initialize buttons
    	this.buttonList.clear();
    	// add the "close gui" button
    	int doneW = 98, doneH = 20;
    	int doneX = (this.width - doneW) / 2;
    	int doneY = BOOK_HEIGHT + SCR_OFFSET_Y + 8;
    	this.buttonDone = this.addButton(new GuiButton(idDone, doneX, doneY, doneW, doneH, I18n.format("gui.done")));
    	// locate and activate the "page change" arrows
		int arrowX = (this.width - BOOK_WIDTH) / 2;
		int arrowY = SCR_OFFSET_Y + BOOK_HEIGHT - (ARROW_HEIGHT * 3 / 2);
		this.buttonPreviousPage = this.addButton(new GuiGolemBook.NextPageButton(idPrevPage, arrowX + ARROW_WIDTH, arrowY, false));
		this.buttonNextPage = this.addButton(new GuiGolemBook.NextPageButton(idNextPage, arrowX + BOOK_WIDTH - ARROW_WIDTH * 2, arrowY, true));
    	// calculate location and size of blocks icons, if present
		int blockX = ((this.width - BOOK_WIDTH ) / 2) + MARGIN + 4;
    	int blockY = SCR_OFFSET_Y + MARGIN;
		this.buttonBlockLeft = this.addButton(new GuiGolemBook.BlockButton(idBlockLeft, blockX, blockY, GOLEM_BLOCK_SCALE));
		blockX = (this.width / 2) + MARGIN;
    	this.buttonBlockRight = this.addButton(new GuiGolemBook.BlockButton(idBlockRight, blockX, blockY, GOLEM_BLOCK_SCALE));
		// create table of contents
    	for(int i = 0; i < NUM_CONTENTS_ENTRIES; i++) {
    		this.tableOfContents[i] = this.addButton(new GuiGolemBook.GolemEntryButton(idTableContents + i, this, 
    				ALPHABETICAL.get(i), (this.width / 2) + SCROLL_STARTX, SCR_OFFSET_Y + SCROLL_STARTY + ENTRY_H * i));
    	}	
    	this.updateButtons();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// draw background (book)
		this.mc.getTextureManager().bindTexture(TEXTURE);
		int bookX = (this.width - BOOK_WIDTH) / 2;
		int bookY = SCR_OFFSET_Y;
		this.drawTexturedModalRect(bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);

		// draw pages: left and right
		this.drawPageAt(bookX + 1, bookY, this.curPage);
		this.drawPageAt(bookX + (BOOK_WIDTH / 2) - 2, bookY, this.curPage + 1);

		// draw buttons, etc.
		super.drawScreen(mouseX, mouseY, partialTicks);

		// hovering text has to be the last thing you do
		if (this.isPageGolemEntry(this.curPage) && this.buttonBlockLeft.isMouseOver()) {
			// check for hover-over on left side
			GolemBookEntry entry = this.getGolemEntryForPage(this.curPage);
			if (entry.getBlock() != Blocks.AIR) {
				this.drawHoveringText(entry.getBlock().getLocalizedName(), mouseX, mouseY);
			}
		}
		if (this.isPageGolemEntry(this.curPage + 1) && this.buttonBlockRight.isMouseOver()) {
			// check for hover-over on right side
			GolemBookEntry entry = this.getGolemEntryForPage(this.curPage + 1);
			if (entry.getBlock() != Blocks.AIR) {
				this.drawHoveringText(entry.getBlock().getLocalizedName(), mouseX, mouseY);
			}
		}
	}

	/**
	 * Uses the given page number to calculate which page to draw
	 * and calls the appropriate methods to do so.
	 *
	 * @param cornerX the left corner of the page
	 * @param cornerY the upper corner of the page
	 * @param pageNum the page to draw
	 **/
	private void drawPageAt(final int cornerX, final int cornerY, final int pageNum) {
		// draw the page number
		this.drawPageNum(cornerX, cornerY, pageNum + 1);
		// declare these for the following switch statement
		String title, body;
		float scale;
		int startX, startY;
		// using the page number, decides which page to draw and how to draw it
		switch (pageNum) {
			case 0:
				// draw introduction
				title = trans("item.info_book.name");
				body = trans("golembook.intro1") + "\n" + trans("golembook.intro2");
				drawBasicPage(cornerX, cornerY, title, body);
				return;
			case 1:
				// draw Table of Contents
				title = trans("golembook.contents.title");
				drawBasicPage(cornerX, cornerY, title, "");
				
				// draw background
				this.mc.getTextureManager().bindTexture(CONTENTS);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				startX = cornerX + SCROLL_STARTX + 1;
				startY = cornerY + SCROLL_STARTY - 1;
				this.drawTexturedModalRect(startX, startY, 0, 0, CONTENTS_W, CONTENTS_H + 2);
				
				// draw scroll bar
				startX = getScrollX(this.width) - 2;
				startY = getScrollY(this.currentScroll);
				this.drawTexturedModalRect(startX, startY, this.isScrolling ? SCROLL_W + DEF_SEP : 0, 
						CONTENTS_H + DEF_SEP, SCROLL_W, SCROLL_H);
				
				// update button contents
				if(this.isScrolling) {
					GolemBookEntry[] visibleArray = this.getGolemEntriesForScroll(this.currentScroll);
					for(int i = 0, l = this.tableOfContents.length; i < l; i++) {
						this.tableOfContents[i].setEntry(visibleArray[i]);
					}
				}
				return;
			case 2:
				// draw Golem Spell instructions
				title = TextFormatting.getTextWithoutFormattingCodes(trans("item.golem_paper.name"));
				body = "\n\n\n\n" + I18n.format("golembook.recipe_spell.recipe", title,
					trans("item.paper.name"), trans("item.feather.name"),
					trans("item.dyePowder.black.name"), trans("item.redstone.name"));
				drawBasicPage(cornerX, cornerY, title, body);
				draw2x2GridAt(cornerX + MARGIN * 2, cornerY + MARGIN * 2, ingredientsSpell, outputSpell);
				return;
			case 3:
				// draw Golem Head instructions
				title = trans("tile.golem_head.name");
				body = "\n\n\n\n" + TextFormatting.getTextWithoutFormattingCodes(trans("golembook.recipe_head.recipe", title,
					trans("item.golem_paper.name"), trans("tile.pumpkin.name")));
				drawBasicPage(cornerX, cornerY, title, body);
				draw2x2GridAt(cornerX + MARGIN * 2, cornerY + MARGIN * 2, ingredientsHead, outputHead);
				return;
			case 4:
				// draw Make Golem instructions
				title = trans("golembook.build_golem.title");
				body = trans("golembook.build_golem.howto1") + " "
					+ trans("golembook.build_golem.howto2") + "\n\n"
					+ I18n.format("golembook.build_golem.howto3", trans("tile.golem_head.name"));
				drawBasicPage(cornerX, cornerY, title, body);
				return;
			case 5:
				// draw Golem diagram
				Block golemBody = Blocks.IRON_BLOCK;
				Block golemHead = GolemItems.golemHead;
				scale = 2.0F;
				final int blockW = (int) (8.0F * scale);
				startX = cornerX + (BOOK_WIDTH / 8);
				startY = cornerY + blockW;
				// head
				this.drawBlock(golemHead, startX, startY, scale);
				// middle-bottom
				startY += blockW * 4;
				this.drawBlock(golemBody, startX, startY, scale);
				// arm-right
				startX += blockW * 2;
				startY -= (blockW * 5) / 2;
				this.drawBlock(golemBody, startX, startY, scale);
				// middle-top
				startX -= blockW * 2;
				startY += (blockW / 2);
				this.drawBlock(golemBody, startX, startY, scale);
				// arm-left
				startX -= blockW * 2;
				startY += (blockW / 2);
				this.drawBlock(golemBody, startX, startY, scale);
				return;
			case 6:
			default:
				// draw golem entry
				if (this.isPageGolemEntry(pageNum)) {
					GolemBookEntry entry = this.getGolemEntryForPage(pageNum);
					this.drawGolemEntry(cornerX, cornerY, entry);
				}
				return;
		}
	}


	private void drawPageNum(final int cornerX, final int cornerY, final int pageNum) {
    	boolean isLeft = pageNum % 2 == 1;
    	// 'page x of xx'
 		int numX = isLeft ? ((this.width / 2) - MARGIN) : ((this.width / 2) + MARGIN);
 		int numY = cornerY + BOOK_HEIGHT - (ARROW_HEIGHT * 3 / 2);
 		String pageNumLeft = String.valueOf(pageNum);
 		int sWidth = isLeft ? this.fontRenderer.getStringWidth(pageNumLeft) : 0;
        this.fontRenderer.drawString(pageNumLeft, numX - sWidth, numY , 0);
    }
    
    /** 
	 * Draws the given Block in the upper-left corner of the passed page coordinates 
	 **/
    protected void drawBlock(final Block blockIn, final int cornerX, final int cornerY, final float scale) {
    	// 'Blocks.AIR' is the flag for 'no block'
    	Block block = blockIn != Blocks.AIR ? blockIn : Blocks.BARRIER;
    	// draw 'golem block'
		float blockX = (float)(cornerX + MARGIN + 4);
    	float blockY = (float)(cornerY + MARGIN);
    	//float unScale = (float)Math.pow(scale,-1);
    	// Render the Block with given scale
    	GlStateManager.pushMatrix();
    	GlStateManager.enableRescaleNormal();
    	RenderHelper.enableGUIStandardItemLighting();
    	GlStateManager.scale(scale, scale, scale);
    	this.itemRender.renderItemIntoGUI(new ItemStack(block), (int)(blockX / scale), (int)(blockY / scale));
    	//GlStateManager.scale(unScale, unScale, unScale);
    	//RenderHelper.disableStandardItemLighting();
    	//GlStateManager.disableRescaleNormal();
    	GlStateManager.popMatrix();
    }
    
    /** 
	 * Draws the GolemEntry name and description at the given location 
	 **/
    private void drawGolemEntry(int cornerX, int cornerY, final GolemBookEntry entry) {
 		// 'golem name' text box
    	int nameX = cornerX + MARGIN * 4;
 		int nameY = cornerY + MARGIN;
 		String golemName = entry.getGolemName();
 		this.fontRenderer.drawSplitString(golemName, nameX, nameY, (BOOK_WIDTH / 2) - MARGIN * 5, 0);

 		// 'golem stats' text box
 		int statsX = cornerX + MARGIN;
 		int statsY = nameY + MARGIN * 2;
 		String stats = entry.getDescriptionPage();
 		this.fontRenderer.drawSplitString(stats, statsX, statsY, (BOOK_WIDTH / 2) - (MARGIN * 2), 0);  
 		
 		// 'golem block'
 		this.drawBlock(entry.getBlock(), cornerX, cornerY, GOLEM_BLOCK_SCALE);
 		
 		// 'screenshot' (supplemental image)
 		if(entry.hasImage()) {
 			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
 			float scale = 0.9F;
 			int imgX = cornerX + (BOOK_WIDTH / 4) - (int)((SUPP_WIDTH * scale) / 2.0F);
 			int imgY = cornerY + BOOK_HEIGHT - (int)(SUPP_HEIGHT * scale) - (MARGIN * 2);
 			this.mc.getTextureManager().bindTexture(entry.getImageResource());
 			int w = (int)(SUPP_WIDTH * scale);
 			int h = (int)(SUPP_HEIGHT * scale);
 			drawModalRectWithCustomSizedTexture(imgX, imgY, 0, 0, w, h, w, h);
 		}
    }
    
    private void drawBasicPage(int cornerX, int cornerY, String title, String body) {
    	final int maxWidth = (BOOK_WIDTH / 2) - (MARGIN * 2);
    	
    	int titleX = cornerX + MARGIN + 4;
    	int titleY = cornerY + MARGIN;
    	int sWidth = this.fontRenderer.getStringWidth(title);
    	if(sWidth > maxWidth) {
    		// draw title wrapped
    		this.fontRenderer.drawSplitString(title, titleX, titleY, maxWidth, 0);
    	} else {
    		// draw title centered
    		this.fontRenderer.drawString(title, titleX + ((maxWidth - sWidth) / 2), titleY, 0);
    	}
    	
    	int bodyX = titleX;
    	int bodyY = titleY + MARGIN * 2;
    	this.fontRenderer.drawSplitString(body, bodyX, bodyY, maxWidth, 0);    	
    }   
    
    private void draw2x2GridAt(final int startX, final int startY, final ItemStack[] ingredients, final ItemStack result) {
    	final int frameWidth = 3;
    	final float scale = 1.0F;
    	final float unScale = (float)Math.pow(scale,-1);
    	/** texture location and size of 2x2 crafting **/
    	final int gridW = 84, gridH = 46;
    	GlStateManager.pushMatrix();
    	GlStateManager.scale(scale, scale, scale);
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    	// draw 2x2 grid background
    	this.mc.getTextureManager().bindTexture(TEXTURE);
    	this.drawTexturedModalRect(startX, startY, BOOK_WIDTH - gridW, BOOK_HEIGHT + DEF_SEP, gridW, gridH);
    	
    	// draw itemstacks
    	GlStateManager.enableRescaleNormal();
    	RenderHelper.enableGUIStandardItemLighting();
    	float posX, posY;
    	int iconW = 15;
    	switch(ingredients.length) {
    	// intentional omission of break statements
    	case 4:
    		posX = startX + iconW + frameWidth * 3.0F;
    		posY = startY + iconW + frameWidth * 3.0F;
    		this.itemRender.renderItemIntoGUI(ingredients[3], (int)(posX / scale), (int)(posY / scale));
    	case 3:
    		posX = startX + frameWidth * 2.0F;
    		posY = startY + iconW + frameWidth * 3.0F;
    		this.itemRender.renderItemIntoGUI(ingredients[2], (int)(posX / scale), (int)(posY / scale));
    	case 2:
    		posX = startX + iconW + frameWidth * 3.0F;
    		posY = startY + frameWidth * 2.0F;
    		this.itemRender.renderItemIntoGUI(ingredients[1], (int)(posX / scale), (int)(posY / scale));
    	case 1:
    		posX = startX + frameWidth * 2.0F;
    		posY = startY + frameWidth * 2.0F;
    		this.itemRender.renderItemIntoGUI(ingredients[0], (int)(posX / scale), (int)(posY / scale));
    	default: break;
    	}
    	
    	// draw result itemstack
    	posX = startX + gridW - 16.0F - frameWidth * 2.0F;
    	posY = startY + 16.0F;
    	this.itemRender.renderItemIntoGUI(result, (int)(posX / scale), (int)(posY / scale));
    	
    	// reset scale
    	GlStateManager.popMatrix();
    }
    
    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    @Override
    protected void mouseClickMove(final int mouseX, final int mouseY, final int clickedMouseButton, final long timeSinceLastClick) {
		// detect if it's the table of contents and updates scroll button
		if(isPageTableContents(this.curPage)) {
			// if it's scrolling, center the scroll bar around the mouse
			int scrollYCentered = getScrollY(this.currentScroll) - (SCROLL_H / 2);
			int mouseYCentered = mouseY - SCROLL_H / 2;
			if(isMouseOverScroll(mouseX, mouseYCentered)) {
				this.isScrolling = true;
				// if the mouse Y is within the correct bounds...
				if(mouseYCentered < SCR_OFFSET_Y + SCROLL_STARTY + CONTENTS_H
						&& mouseYCentered >= SCR_OFFSET_Y + SCROLL_STARTY) {
					// update scrolling variables
					this.currentScroll = getScrollFloat(mouseY - SCROLL_H / 2);
				}
			} else {
				this.isScrolling = false;
			}
		}
    }
    
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    	super.mouseReleased(mouseX, mouseY, state);
    	this.isScrolling = false;
    }
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	super.actionPerformed(button);
    	switch(button.id) {
    	case idDone: 
    		// finish because we closed the gui
    		this.mc.displayGuiScreen((GuiScreen)null);
    		return;
    	case idNextPage:
    		this.curPage += 2;
    		break;
    	case idPrevPage:
    		this.curPage -= 2;
    		break;
    	default: 
    		// if the id is within the "table of contents" range, turn to that golem's page
    		if(button.id >= idTableContents && button.id < idTableContents + NUM_CONTENTS_ENTRIES 
    			&& button instanceof GuiGolemBook.GolemEntryButton) {
    			// find out which page this button should go to
    			GolemBookEntry entry = ((GuiGolemBook.GolemEntryButton)button).entry;
    			int index = GOLEMS.indexOf(entry);
    			if(index >= 0 && index <= GOLEMS.size()) {
    				int page = index + NUM_PAGES_INTRO;
    				// make sure it's an even number
    				page = Math.floorDiv(page, 2) * 2;
    				this.curPage = page;
    			}
    		}
    		break;
    	}
    	this.updateButtons();
    }
	
	/** Used to determine whether to show buttons **/
	private void updateButtons() {
		this.buttonBlockLeft.visible = isPageGolemEntry(this.curPage);
		this.buttonBlockRight.visible = isPageGolemEntry(this.curPage + 1);
		this.buttonPreviousPage.visible = this.curPage > 0;
		this.buttonNextPage.visible = this.curPage + 2 < this.totalPages;
		boolean tableContentsVisible = isPageTableContents(this.curPage);
		for(GuiButton b : this.tableOfContents) {
			b.visible = tableContentsVisible;
		}
	}
	
	private boolean isMouseOverScroll(final int mouseX, final int mouseY) {
		//int currentScrollY = getScrollY(this.currentScroll);
		// check if the mouse is in the "scroll" column
		int scrollStartX = getScrollX(this.width);
		int scrollStartY = SCR_OFFSET_Y + SCROLL_STARTY;
		return mouseX >= scrollStartX && mouseY >= scrollStartY
				&& mouseX < scrollStartX + SCROLL_W && mouseY < scrollStartY + CONTENTS_H - SCROLL_H / 2;
	}
	
	private int getScrollX(final int screenWidth) {
		return (screenWidth / 2) + SCROLL_STARTX + CONTENTS_W - SCROLL_W;
	}
	
	private int getScrollY(final float scroll) {
		// clamp scroll between 0.0 and 1.0
		final float f = Math.max(0.0F, Math.min(1.0F, scroll));
		return SCR_OFFSET_Y + SCROLL_STARTY + (int)(f * (CONTENTS_H - SCROLL_H));
	}
	
	private float getScrollFloat(final int currentY) {
		final int minY = SCR_OFFSET_Y + SCROLL_STARTY;
		final int maxY = minY + CONTENTS_H - SCROLL_H;
		final float f = Math.max(minY, Math.min(maxY, currentY)) - minY;
		return (f / (CONTENTS_H - SCROLL_H));
	}

	private boolean isPageGolemEntry(final int page) {
		return page >= NUM_PAGES_INTRO && page < this.totalPages;
	}
	
	private boolean isPageTableContents(final int page) {
		return page >= 0 && page < 2;
	}

	private GolemBookEntry getGolemEntryForPage(final int page) {
		return GOLEMS.get(page - NUM_PAGES_INTRO);
	}
	
	private GolemBookEntry[] getGolemEntriesForScroll(final float scrollIn) {
		float scroll = MathHelper.clamp(scrollIn, 0.0F, 1.0F);
		int i = (int)(scroll * (float)(ALPHABETICAL.size() - NUM_CONTENTS_ENTRIES));
		return ALPHABETICAL.subList(i, i + NUM_CONTENTS_ENTRIES).toArray(new GolemBookEntry[NUM_CONTENTS_ENTRIES]);
	}
	
	/** Helper method for translating text into local language using {@code I18n} **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}

	protected static class BlockButton extends GuiButton {

		public BlockButton(int buttonId, int x, int y, float scaleIn) {
			super(buttonId, x, y, (int) (scaleIn * 16.0F), (int) (scaleIn * 16.0F), "");
		}

		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		}
	}
	
	protected static class GolemEntryButton extends GuiButton {
		
		private final GuiGolemBook gui;
		private GolemBookEntry entry;
		
		public GolemEntryButton(final int buttonId, final GuiGolemBook guiIn, final GolemBookEntry entryIn, final int x, final int y) {
			super(buttonId, x, y, ENTRY_W, ENTRY_H, "");
			this.gui = guiIn;
			this.entry = entryIn;
		}
		
		public void setEntry(GolemBookEntry toSet) {
			this.entry = toSet;
		}
		
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			if(this.visible) {
	            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				mc.getTextureManager().bindTexture(CONTENTS);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				// draw the background of the button
				this.drawTexturedModalRect(this.x, this.y, CONTENTS_W + DEF_SEP, 
						this.hovered ? ENTRY_H + DEF_SEP : 0, ENTRY_W, ENTRY_H);
				// draw the block and name of the golem
				gui.drawBlock(this.entry.getBlock(), this.x - MARGIN - 2, this.y - 9, 1.0F);
				
				// prepare to draw the golem's name
				GlStateManager.pushMatrix();
				
				final String name = entry.getGolemName();
				final int wrap = this.width - 20;
				float scale = 1.0F;
				int nameH = gui.fontRenderer.getWordWrappedHeight(name, wrap);
				if(nameH > this.height) {
					scale = 0.7F;
					nameH = (int) (scale * gui.fontRenderer.getWordWrappedHeight(name, (int)(wrap / scale)));
				}
				int nameX = this.x + 20;
				int nameY = this.y + ((this.height - nameH) / 2) + 1;
				// re-scale and draw the golem name
				GlStateManager.scale(scale, scale, scale);
				gui.fontRenderer.drawSplitString(name, (int)((nameX) / scale), (int)(nameY / scale), (int)(wrap / scale), 0);
				GlStateManager.popMatrix();
			}
		}
	}

	protected static class NextPageButton extends GuiButton {

		// texture starts at this location
		protected static final int TEXTURE_STARTY = BOOK_HEIGHT + DEF_SEP;
		private final boolean isForward;

		public NextPageButton(int buttonId, int x, int y, boolean isForwardIn) {
			super(buttonId, x, y, ARROW_WIDTH, ARROW_HEIGHT, "");
			this.isForward = isForwardIn;
		}

		/**
		 * Draws this button to the screen.
		 */
		@Override
		public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {

			if (this.visible) {
				boolean mouseOver = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(GuiGolemBook.TEXTURE);
				int tx = 0;
				int ty = TEXTURE_STARTY;

				if (mouseOver) {
					tx += ARROW_WIDTH + DEF_SEP;
				}

				if (!this.isForward) {
					ty += ARROW_HEIGHT;
				}

				this.drawTexturedModalRect(this.x, this.y, tx, ty, ARROW_WIDTH, ARROW_HEIGHT);
			}
		}
	}
}
