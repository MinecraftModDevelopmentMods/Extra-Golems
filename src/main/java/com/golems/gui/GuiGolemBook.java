package com.golems.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.golems.entity.GolemBase;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemLookup;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

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
	////// [ ] On the right there are 3 tabs. The top tab ("i") will take
	////// you directly to page 1 and is active only on pages 1-2.
	////// [ ] The middle tab ("I") will take you to the "How-To"s which starts
	////// on page 3 and is active from pages 3-6.
	////// [ ] The bottom tab ("II") will take you directly to the first Golem
	////// entry on page 7. It is active from page 7 to the end of
	////// the book. ALTERNATIVELY this tab will be called "Extra Golems"
	////// and golems from other addons will appear in separate tabs.
	////// That's going to be a lot of work but it probably could be done.
	////// [ ] There is also (eventually) going to be a Search bar
	////// attached at the upper-right corner of the book.
	////// [ ] Typing into this bar shows a drop-down of possible matches.
	////// [ ] Clicking on a match will take you directly to that page.
	//[ ] PAGE 1 (Left): WELCOME
	////// [ ] Small blurb about golems
	//[ ] PAGE 2 (Right): TABLE OF CONTENTS
	////// [ ] Lists the title of pages 3, 4, and 5.
	////// [ ] Lists the name of each Golem in the book.
	////// [ ] Click-able (linked) list of all Golem names, maybe
	////// with an icon of their building block.
	////// [ ] Clicking that name will jump you to the correct page.
	//[ ] PAGE 3 (Left): GOLEM SPELL RECIPE
	////// [ ] Name of item + Picture of crafting grid.
	////// [ ] Extra Fancy option:  hover-over text tells
	////// you name of each ingredient.
	//[ ] PAGE 4 (Right): GOLEM HEAD RECIPE
	////// [ ] Name of block + Picture of crafting grid.
	////// [ ] Maybe include hover-over text here too.
	//[ ] PAGE 5 (Left): HOW TO BUILD A GOLEM
	////// Verbal instructions on where to place blocks, etc.
	//[ ] PAGE 6 (Right): IMAGE OF HOW TO BUILD A GOLEM
	////// [ ] Diagram of 4 blocks in golem-shape and a Golem Head
	////// above them, indicating where to place the head.
	////// [ ] Extra Fancy option:  blocks are 3d
	//[X] PAGES 7 (Left) TO END: GOLEM ENTRIES
	////// Defined in the GolemBookEntry object:
	////// [X] Large picture of Building Block on upper-left of page.
	////// [X] Hover-over text tells you the name of the Block.
	////// [X] Page number in upper-right: "Page x of xx"
	////// [X] Golem Name below page number, right-aligned
	////// [X] Attributes listed:  
	////// Health and Attack
	////// "Multi-Textured" prompt if Golem has multiple textures
	////// "Fireproof" if the Golem is fireproof
	////// All Golem Specials as defined in GolemBase#addSpecialDesc(List<String>)
	////// (each entry as a separate line)
	
	///////////////////////////////////////////////
	
	protected static final ResourceLocation TEXTURE = new ResourceLocation(ExtraGolems.MODID, "textures/gui/info_book.png");
	/** book texture goes this far down **/
	protected static final int BOOK_HEIGHT = 164;
	/** book texture is this wide **/
	protected static final int BOOK_WIDTH = 256;
	/** icons are this many pixels apart **/
	protected static final int DEF_SEP = 5;
	
	/** how far down the Minecraft screen the book starts **/
	protected static final int SCR_OFFSET_Y = 16;
	/** width of the arrow button and its texture **/
	protected static final int ARROW_WIDTH = 13 + DEF_SEP;
	/** height of the arrow button and its texture **/
	protected static final int ARROW_HEIGHT = 10 + DEF_SEP;

	/** Button to exit GUI **/
	private GuiButton buttonDone;
	/** If a Block is displayed on the LEFT side, this helps with the tooltip **/
	private GuiGolemBook.BlockButton buttonBlockLeft;
	/** If a Block is displayed on the RIGHT side, this helps with the tooltip **/
	private GuiGolemBook.BlockButton buttonBlockRight;
	/** Increments the page number by 2 **/
	private GuiGolemBook.NextPageButton buttonNextPage;
	/** De-increments the page number by 2 **/
    private GuiGolemBook.NextPageButton buttonPreviousPage;
    /** TODO shortcut tab to jump to page 0 **/
    private GuiButton tabIntro;
    /** TODO shortcut tab to jump to part 1 **/
    private GuiButton tabPart1;
    /** TODO shortcut tab to jump to part 2 **/
    private GuiButton tabPart2;
    
    /** TODO clickable links to each golem **/
    private GuiButton[] tableOfContents;
    
    protected int curPage;
    protected int totalPages;
    
    public static final List<GolemBookEntry> GOLEMS = new ArrayList();
	private EntityPlayer player;
	private ItemStack book;
    
    private final int idDone = 0;
	private final int idNextPage = 1;
    private final int idPrevPage = 2;
    private final int idBlockLeft = 3;
    private final int idBlockRight = 4;
    
    private final float BLOCK_SCALE = 1.60F;
    private final int MARGIN = 12;
	
//	private static final String KEY_PAGES = "pages";
//	private static final String KEY_TITLE = "title";
//	private static final String KEY_AUTHOR = "author";
	
	public GuiGolemBook(EntityPlayer playerIn, ItemStack itemIn) {
		super();
		if(GOLEMS.isEmpty()) {
			initGolemBookEntries(playerIn.getEntityWorld());
		}
		this.player = playerIn;
		this.book = itemIn;
		this.curPage = 0;
		this.totalPages = GOLEMS.size();
	}
	
	/** Populates the GolemEntry list to use in book gui **/
	private static final void initGolemBookEntries(World world) {
		GOLEMS.clear();
		for(GolemBase golem : GolemLookup.getDummyGolemList(world)) {
			GOLEMS.add(new GolemBookEntry(golem));
		}
	}
	
	/** @return a List of all entries containing the given text. List may be empty. **/
	public static final List<GolemBookEntry> searchFor(final String text) {
		final List<GolemBookEntry> list = new LinkedList();
		GOLEMS.forEach((GolemBookEntry entry) -> addIfMatches(list, entry, text));
		return list;
	}
	
	private static void addIfMatches(final List<GolemBookEntry> list, final GolemBookEntry entry, final String text) {
		if(entry.getSearchableString().contains(text)) {
			list.add(entry);
		}
	}
	
	@Override
    public void initGui() {
		// initialize buttons
    	this.buttonList.clear();
    	int doneW = 98;
    	int doneH = 20;
    	int doneX = (this.width - doneW) / 2;
    	int doneY = BOOK_HEIGHT + SCR_OFFSET_Y + 8;
    	this.buttonDone = this.addButton(new GuiButton(idDone, doneX, doneY, doneW, doneH, I18n.format("gui.done")));
		int arrowX = (this.width - BOOK_WIDTH) / 2;
		int arrowY = SCR_OFFSET_Y + BOOK_HEIGHT - (ARROW_HEIGHT * 3 / 2);
		this.buttonPreviousPage = this.addButton(new GuiGolemBook.NextPageButton(idPrevPage, arrowX + ARROW_WIDTH, arrowY, false));
		this.buttonNextPage = this.addButton(new GuiGolemBook.NextPageButton(idNextPage, arrowX + BOOK_WIDTH - ARROW_WIDTH * 2, arrowY, true));
    	int blockX = ((this.width - BOOK_WIDTH ) / 2) + MARGIN + 4;
    	int blockY = SCR_OFFSET_Y + MARGIN;
		this.buttonBlockLeft = this.addButton(new GuiGolemBook.BlockButton(idBlockLeft, blockX, blockY, BLOCK_SCALE));
		blockX = (this.width / 2) + MARGIN;
    	this.buttonBlockRight = this.addButton(new GuiGolemBook.BlockButton(idBlockRight, blockX, blockY, BLOCK_SCALE));
		this.updateButtons();
    }
	
	 /**
     * Draws the screen and all the components in it.
     */
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        // DRAW BOOK
        int bookX = (this.width - BOOK_WIDTH) / 2;
        int bookY = SCR_OFFSET_Y;
        this.drawTexturedModalRect(bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);

        // LEFT SIDE OF BOOK
        int pageL = this.curPage;
        int pageR = this.curPage + 1;
        boolean pageLeftGolemEntry = isPageGolemEntry(pageL);
        boolean pageRight = isPageGolemEntry(pageR);
        Block blockL = null;
        Block blockR = null;
        
        this.drawPageNum(pageL + 1, true);
        this.drawPageNum(pageR + 1, false);
        
        if(pageLeftGolemEntry) {
        	GolemBookEntry entryL = this.getEntryForPage(pageL);
        	blockL = entryL.getBlock();
        	this.drawBlock(blockL, BLOCK_SCALE, true);
        	this.drawEntry(entryL, true);
        }
        // RIGHT SIDE OF BOOK
        if(pageRight) {
        	GolemBookEntry entryR = this.getEntryForPage(pageR);
        	blockR = entryR.getBlock();
        	this.drawBlock(blockR, BLOCK_SCALE, false);
        	this.drawEntry(entryR, false);
        }
        
        // draw buttons, etc.
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        // hovering text has to be the last thing you do
        if(pageLeftGolemEntry && buttonList.get(idBlockLeft).isMouseOver()) {
        	// check for hover-over on left side
        	this.drawHoveringText(blockL.getLocalizedName(), mouseX, mouseY);
        }
        if(pageRight && buttonList.get(idBlockRight).isMouseOver()) {
        	// check for hover-over on right side
        	this.drawHoveringText(blockR.getLocalizedName(), mouseX, mouseY);
        }
    }
    
    private void drawPageNum(final int pageNum, final boolean isLeft) {
    	// 'page x of xx'
 		//int numX = isLeft ? ((this.width / 2) - MARGIN) : ((this.width + BOOK_WIDTH) / 2 - MARGIN); // right-aligned
 		int numX = isLeft ? ((this.width / 2) - MARGIN) : ((this.width / 2) + MARGIN);
 		//int numY = SCR_OFFSET_Y + MARGIN;
 		int numY = SCR_OFFSET_Y + BOOK_HEIGHT - (ARROW_HEIGHT * 3 / 2);
 		String pageNumLeft = String.valueOf(pageNum);//trans("book.pageIndicator", pageNum, this.totalPages);
 		int sWidth = isLeft ? this.fontRenderer.getStringWidth(pageNumLeft) : 0;
        this.fontRenderer.drawString(pageNumLeft, numX - sWidth, numY , 0);
    }
    
    private void drawBlock(final Block block, final float scale, final boolean isLeft) {
    	// 'Blocks.AIR' is the flag for 'no block'
    	if(block != Blocks.AIR) {
        	// draw 'golem block'
    		float blockX = isLeft ? (float)((this.width - BOOK_WIDTH) / 2 + MARGIN + 4) : (float)((this.width / 2) + MARGIN);
        	float blockY = (float)(SCR_OFFSET_Y + MARGIN);
        	float unScale = (float)Math.pow(scale,-1);
        	// Render the Block with given scale
        	GlStateManager.enableRescaleNormal();
        	RenderHelper.enableGUIStandardItemLighting();
        	GlStateManager.scale(scale, scale, scale);
        	this.itemRender.renderItemIntoGUI(new ItemStack(block), (int)(blockX / scale), (int)(blockY / scale));
        	GlStateManager.scale(unScale, unScale, unScale);
        	RenderHelper.disableStandardItemLighting();
        	GlStateManager.disableRescaleNormal();
        }
    }
    
    private void drawEntry(final GolemBookEntry entry, final boolean isLeft) {
    	final int cornerX = (this.width - BOOK_WIDTH) / 2;
    	final int cornerY = SCR_OFFSET_Y;
    	// DRAW TEXT FIELDS 
 		// 'golem name' text box
 		//int nameX = isLeft ? (cornerX + (BOOK_WIDTH / 2) - MARGIN) : (cornerX + BOOK_WIDTH - MARGIN); // right-aligned
    	int nameX = isLeft ? (cornerX + MARGIN * 4) : (cornerX + (BOOK_WIDTH / 2) + MARGIN * 4);
 		int nameY = cornerY + MARGIN * 2;
 		String golemName = entry.getGolemName();
 		//int sWidth = this.fontRenderer.getStringWidth(golemName);
 		this.fontRenderer.drawSplitString(golemName, nameX, nameY, (BOOK_WIDTH / 2) - MARGIN * 5, 0);

 		// 'golem stats' text box
 		int statsX = isLeft ? (cornerX + MARGIN + 4) : ((this.width / 2) + MARGIN); // left-aligned
 		int statsY = nameY + MARGIN;
 		String stats = entry.getDescriptionPage();
 		this.fontRenderer.drawSplitString(stats, statsX, statsY, (BOOK_WIDTH / 2) - (MARGIN * 2), 0);        
    }
    
    @Override
    public void actionPerformed(GuiButton button) throws IOException {
    	super.actionPerformed(button);
    	switch(button.id) {
    	case idDone: 
    		this.mc.displayGuiScreen((GuiScreen)null);
    		return; // finish because we closed the gui
    	case idNextPage:
    		this.curPage += 2;
    		break;
    	case idPrevPage:
    		this.curPage -= 2;
    		break;
    	default: return;
    	}
    	this.updateButtons();
    }
	
	/** Used to determine whether to show next/previous page buttons **/
	private void updateButtons() {
		 this.buttonBlockLeft.visible = isPageGolemEntry(this.curPage);
		 this.buttonBlockRight.visible = isPageGolemEntry(this.curPage + 1);
		 this.buttonPreviousPage.visible = this.curPage > 0;
		 this.buttonNextPage.visible = this.curPage + 2 < this.totalPages;
	}
	
	private boolean isPageGolemEntry(int page) {
		return page > -1 && page < this.totalPages; // TODO
	}
	
	private GolemBookEntry getEntryForPage(int page) {
		return GOLEMS.get(page);
	}
	/*
	public void drawString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GlStateManager.scale(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,x,y,color);
        GlStateManager.scale(mSize,mSize,mSize);
    }

	public void drawSplitString(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, float size, float padding, int textColor)
    {
        GlStateManager.scale(size,size,size);
        float mSize = (float)Math.pow(size,-1);

        int i = 0;
        for (String string:fontRenderer.listFormattedStringToWidth(str,wrapWidth)) {
            drawString(fontRenderer,string,x,y + Math.round(i * size * fontRenderer.FONT_HEIGHT * padding),size,textColor);
            i++;
        }

        GlStateManager.scale(mSize,mSize,mSize);
    }
*/
//	 /**
//     * Uses the passed list of GolemEntry objects to make a list of
//     * each page entry for the Golem Book. 
//     * @param golemEntries
//     * @return a List where each entry represents one page
//     */
//    public static final List<String> getPages(final List<GolemBookEntry> golemEntries) {
//    	
//    	final List<String> pages = new LinkedList();
//		// first add the introduction to the book
//		pages.addAll(getBookIntroduction());
//		// make a page for each golem and add it to the list
//		for(GolemBookEntry entry : golemEntries) {
//			pages.add(entry.getPageString());
//		}
//		
//		return pages;
//	}
	
    /** Adds Book NBT data to the itemstack using the passed pages. Temporary until we stop using NBT for this **/
//	public static void addNBT(ItemStack itemstack, List<String> pages) {
//		if(itemstack != null) {
//			NBTTagCompound nbt = itemstack.hasTagCompound() ? itemstack.getTagCompound() : new NBTTagCompound();
//			// skip this bit if the NBT has already been set
//			//if(nbt.hasKey(KEY_PAGES))
//			//	return;
//			// for each page in the list, add it to the NBT
//			NBTTagList pagesTag = new NBTTagList();
//			for (String pageText : pages) {
//				pagesTag.appendTag(new NBTTagString(pageText));
//			}
//			
//			nbt.setTag(KEY_PAGES, pagesTag);
//			nbt.setString(KEY_AUTHOR, "");
//		 	nbt.setString(KEY_TITLE, "");
//			itemstack.setTagCompound(nbt);
//		}
//	}
	 
    /** @return a COPY of the introduction pages, each page as a separate String element **/
	private static final List<String> getBookIntroduction() {
		List<String> INTRO = new LinkedList();
		// page 1: "Welcome"
		INTRO.add(trans("golembook.intro1") + "\n" + trans("golembook.intro2"));
		// page 2: "Part 1"
		String partIntro = TextFormatting.GOLD + trans("golembook.part_intro") + TextFormatting.BLACK;
		String golemPaper = trans("item.golem_paper.name");
		String golemHead = trans("tile.golem_head.name");
		INTRO.add("\n\n" + partIntro + "\n\n" + trans("golembook.part1") + "\n\n" + partIntro);
		// page 3: "Make Golem Spell"
		INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_spell.intro", golemPaper) 
				+ "\n\n" + I18n.format("golembook.recipe_spell.recipe", golemPaper, trans("item.paper.name"), trans("item.feather.name"),
				trans("item.dyePowder.black.name"), trans("item.redstone.name"))));
		// page 4: "Make Golem Head"
		INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_head.intro", golemHead) + "\n\n"
				+ trans("golembook.recipe_head.recipe", golemHead, trans("item.golem_paper.name"), trans("tile.pumpkin.name"))));
		// page 5: "Make Golem"
		INTRO.add(trans("golembook.build_golem.intro") + "\n\n" + trans("golembook.build_golem.howto1") + " "
				+ trans("golembook.build_golem.howto2") + "\n\n" + I18n.format("golembook.build_golem.howto3", golemHead));
		// page 6: "Part 2"
		INTRO.add("\n\n" + partIntro + "\n\n" + trans("golembook.part2") + "\n\n" + partIntro);
		
		return INTRO;
	}
	
	/** Helper method for translating text into local language using {@code I18n} **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
	
	protected static class BlockButton extends GuiButton {
				
		public BlockButton(int buttonId, int x, int y, float scaleIn) {
			super(buttonId, x, y, (int)(scaleIn * 16.0F), (int)(scaleIn * 16.0F), "");
		}
		
		@Override
	    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
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
