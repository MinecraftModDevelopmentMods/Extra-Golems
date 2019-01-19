package com.golems.gui;

import java.util.LinkedList;
import java.util.List;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GuiLoader {

    public static void loadBookGui(EntityPlayer playerIn, ItemStack itemstack) {
    	if(!playerIn.getEntityWorld().isRemote)
    		return;
    	// initialize golem list if it hasn't already
    	if(ExtraGolems.proxy.GOLEMS.isEmpty()) {
    		GolemEntry.addGolemEntries(playerIn.getEntityWorld(), ExtraGolems.proxy.GOLEMS);
    	}
    	// open gui
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenBook(playerIn, itemstack, false));
    }
    
    private static final void addPages(final List<String> pages, List<List<String>> fromList) {
		// first add the introduction to the book
		pages.addAll(getBookIntroduction());
		// use the list of descriptions to make the remaining pages of the book
		final String SEP = "\n";
		for(List<String> list : fromList) {
			String page = "";
			// Consolidate all the description data into one string
			for(String s : list) {
				page += s + SEP;
			}
			// add the page
			pages.add(page);
		}
	}
    
    /** @return a COPY of the introduction pages, each page as a separate String element **/
	public static final List<String> getBookIntroduction() {
		List<String> INTRO = new LinkedList();
		// page 1: "Welcome"
		INTRO.add(trans("golembook.intro1") + "\n" + trans("golembook.intro2"));
		// page 2: "Part 1"
		String partIntro = TextFormatting.GOLD + trans("golembook.part_intro") + TextFormatting.BLACK;
		INTRO.add("\n\n" + partIntro + "\n\n" + trans("golembook.part1") + "\n\n" + partIntro);
		// page 3: "Make Golem Spell"
		INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_spell.intro", trans("item.golem_paper.name")) 
				+ "\n\n" + I18n.format("golembook.recipe_spell.recipe", trans("item.golem_paper.name"), trans("item.paper.name"), trans("item.feather.name"),
				trans("item.dyePowder.black.name"), trans("item.redstone.name"))));
		// page 4: "Make Golem Head"
		INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_head.intro", trans("tile.golem_head.name")) + "\n\n"
				+ trans("golembook.recipe_head.recipe", trans("tile.golem_head.name"), trans("item.golem_paper.name"), trans("tile.pumpkin.name"))));
		// page 5: "Make Golem"
		INTRO.add(trans("golembook.build_golem.intro") + "\n\n" + trans("golembook.build_golem.howto1") + " "
				+ trans("golembook.build_golem.howto2") + "\n\n" + I18n.format("golembook.build_golem.howto3", trans("tile.golem_head.name")));
		// page 6: "Part 2"
		INTRO.add("\n\n" + partIntro + "\n\n" + trans("golembook.part2") + "\n\n" + partIntro);
		
		return INTRO;
	}
	
	/** 
	 * Helper method for translating text into local language using {@code I18n}
	 * @see addSpecialDesc 
	 **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
}
