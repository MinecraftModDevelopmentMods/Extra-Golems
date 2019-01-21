package com.golems.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.golems.entity.GolemBase;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemLookup;

import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GuiGolemBook extends GuiScreenBook {
	
	//////////// GUI GOLEM BOOK LAYOUT ////////////
	// 2-Page Layout:
	////// Each screen will have a double-version of the
	////// default Minecraft Book GUI. There will be arrows
	////// for "Previous Page" and "Next Page" but each one
	////// will actually change the page count by (2) because
	////// we show 2 pages at a time.
	// Buttons:
	////// Like Minecraft book, there is a "Done" button below.
	////// Also the Previous/Next Page buttons as described above.
	////// On the right there are 3 tabs. The top tab ("i") will take
	////// you directly to page 1 and is active only on pages 1-2.
	////// The middle tab ("I") will take you to the "How-To"s which starts
	////// on page 3 and is active from pages 3-6.
	////// The bottom tab ("II") will take you directly to the first Golem
	////// entry on page 7. It is active from page 7 to the end of
	////// the book.
	////// There is also (eventually) going to be a Search bar
	////// attached at the upper-right corner of the book.
	////// Typing into this bar shows a drop-down of possible matches.
	////// Clicking on a match will take you directly to that page.
	// PAGE 1 (Left): WELCOME
	////// Small blurb about golems
	// PAGE 2 (Right): TABLE OF CONTENTS
	////// Lists the title of pages 3, 4, and 5.
	////// Lists the name of each Golem in the book.
	////// Click-able (linked) list of all Golem names, maybe
	////// with an icon of their building block.
	////// Clicking that name will jump you to the correct page.
	// PAGE 3 (Left): GOLEM SPELL RECIPE
	////// Name of item + Picture of crafting grid.
	////// Extra Fancy option:  hover-over text tells
	////// you name of each ingredient.
	// PAGE 4 (Right): GOLEM HEAD RECIPE
	////// Name of block + Picture of crafting grid.
	////// Maybe include hover-over text here too.
	// PAGE 5 (Left): HOW TO BUILD A GOLEM
	////// Verbal instructions on where to place blocks, etc.
	// PAGE 6 (Right): IMAGE OF HOW TO BUILD A GOLEM
	////// Diagram of 4 blocks in golem-shape and a Golem Head
	////// above them, indicating where to place the head.
	////// Extra Fancy option:  blocks are 3d
	// PAGES 7 (Left) TO END: GOLEM ENTRIES
	////// Defined in the GolemBookEntry object:
	////// Large picture of Building Block on upper-left of page.
	////// Hover-over text tells you the name of the Block.
	////// Page number in upper-right: "Page x of xx"
	////// Golem Name below page number, right-aligned
	////// Attributes listed:  
	////// Health and Attack
	////// "Multi-Textured" prompt if Golem has multiple textures
	////// "Fireproof" if the Golem is fireproof
	////// All Golem Specials as defined in GolemBase#addSpecialDesc(List<String>)
	////// (each entry as a separate line)
	
	///////////////////////////////////////////////
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(ExtraGolems.MODID, "textures/gui/info_book.png");
	
	private static final String KEY_PAGES = "pages";
	private static final String KEY_TITLE = "title";
	private static final String KEY_AUTHOR = "author";
	
	public static final List<GolemBookEntry> GOLEMS = new ArrayList();
	
	public GuiGolemBook(EntityPlayer player, ItemStack book) {
		super(player, book, false);
		if(GOLEMS.isEmpty()) {
			initGolemBookEntries(player.getEntityWorld());
		}
	}
	
	/** Call this BEFORE making a new GuiGolemBook object!
	 * At least until we fully separate it from GuiScreenBook **/
	public static final void initGolemBookEntries(World world) {
		GOLEMS.clear();
		for(GolemBase golem : GolemLookup.getDummyGolemList(world)) {
			GOLEMS.add(new GolemBookEntry(golem));
		}
	}

	 /**
     * Uses the passed list of GolemEntry objects to make a list of
     * each page entry for the Golem Book. 
     * @param golemEntries
     * @return a List where each entry represents one page
     */
    public static final List<String> getPages(final List<GolemBookEntry> golemEntries) {
    	
    	final List<String> pages = new LinkedList();
		// first add the introduction to the book
		pages.addAll(getBookIntroduction());
		// make a page for each golem and add it to the list
		for(GolemBookEntry entry : golemEntries) {
			pages.add(entry.getPageString());
		}
		
		return pages;
	}
	
    /** Adds Book NBT data to the itemstack using the passed pages. Temporary until we stop using NBT for this **/
	public static void addNBT(ItemStack itemstack, List<String> pages) {
		if(itemstack != null) {
			NBTTagCompound nbt = itemstack.hasTagCompound() ? itemstack.getTagCompound() : new NBTTagCompound();
			// skip this bit if the NBT has already been set
			//if(nbt.hasKey(KEY_PAGES))
			//	return;
			// for each page in the list, add it to the NBT
			NBTTagList pagesTag = new NBTTagList();
			for (String pageText : pages) {
				pagesTag.appendTag(new NBTTagString(pageText));
			}
			
			nbt.setTag(KEY_PAGES, pagesTag);
			nbt.setString(KEY_AUTHOR, "");
		 	nbt.setString(KEY_TITLE, "");
			itemstack.setTagCompound(nbt);
		}
	}
	 
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
}
