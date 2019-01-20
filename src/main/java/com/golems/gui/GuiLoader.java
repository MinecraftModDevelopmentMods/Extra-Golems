package com.golems.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;
import com.golems.main.ExtraGolems;
import com.golems.network.GolemNetworkHandler;
import com.golems.network.PacketTriggerGolemEntryLoad;
import com.golems.util.GolemEntry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class GuiLoader {

	private static final String KEY_PAGES = "pages";
	private static final String KEY_TITLE = "title";
	private static final String KEY_AUTHOR = "author";
	
    public static void loadBookGui(EntityPlayer playerIn, ItemStack itemstack) {
    	System.out.println("[GuiLoader] onLoadBookGui");
    	
    	// only load client-side, of course
    	if(!playerIn.getEntityWorld().isRemote)
    		return;
    	// populate the DummyGolems list if it is empty
    	if(shouldReloadGolems()) {
    		ExtraGolems.proxy.DUMMY_GOLEMS.addAll(ExtraGolems.proxy.getDummyGolemList(playerIn.getEntityWorld()));
    	}
    	// use DummyGolems list to build pages (need real-time for localization)
    	final List<String> pages = getPages(ExtraGolems.proxy.DUMMY_GOLEMS);
    	addNBT(itemstack, pages);
    	
    	// open gui
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenBook(playerIn, itemstack, false));
    }
    
    public static boolean shouldReloadGolems() {
    	return ExtraGolems.proxy.DUMMY_GOLEMS.isEmpty();
    }
    
    /**
     * Uses the passed list of GolemEntry objects to make a list of
     * each page entry for the Golem Book. 
     * @param golemEntries
     * @return a List where each entry represents one page
     */
    private static final List<String> getPages(final List<GolemBase> golemEntries) {
    	
    	final List<String> pages = new LinkedList();
		// first add the introduction to the book
		pages.addAll(getBookIntroduction());
		// make a page for each golem and add it to the list
		for(GolemBase golem : golemEntries) {
			pages.add(makePage(golem));
		}
		
		return pages;
	}
    
    /** @return a COPY of the introduction pages, each page as a separate String element **/
	private static final List<String> getBookIntroduction() {
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
	
	/** Use the GolemBase to translate and make the page for this golem **/
	private static final String makePage(GolemBase golem) {
		
		String page = "";
		// ADD NAME TIP
		page += TextFormatting.GRAY + trans("entitytip.name") + ": "
				+ TextFormatting.BLACK + golem.getName() + "\n";
		// ADD HEALTH (ROUNDED) TIP
		page += TextFormatting.GRAY + trans("entitytip.health") + " : " + TextFormatting.BLACK
				+ Math.round(golem.getMaxHealth()) + TextFormatting.DARK_RED + " \u2764" + TextFormatting.BLACK;
		// ADD ATTACK POWER TIP
		page += TextFormatting.GRAY + trans("entitytip.attack") + " : "
				+ TextFormatting.BLACK + golem.getBaseAttackDamage() + " \u2694" + "\n";
		// ADD FIREPROOF TIP
		if (golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem)) {
			page += TextFormatting.GOLD + trans("entitytip.is_fireproof");
		}
		// ADD INTERACT-TEXTURE TIP
		if (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture()) {
			page += TextFormatting.BLUE + trans("entitytip.click_change_texture");
		}
		// ADD SPECIALS
		List<String> specials = golem.addSpecialDesc(new ArrayList<String>());
		for(String s : specials) {
			page += "\n" + s;
		}
		
		return page;
	}
	
	private static void addNBT(ItemStack itemstack, List<String> pages) 
	{
		if(itemstack != null) {
			NBTTagCompound nbt = itemstack.hasTagCompound() ? itemstack.getTagCompound() : new NBTTagCompound();
			// skip this bit if the NBT has already been set
			if(nbt.hasKey(KEY_PAGES))
				return;
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
	
	/** Helper method for translating text into local language using {@code I18n} **/
	protected static String trans(final String s, final Object... strings) {
		return I18n.format(s, strings);
	}
}
