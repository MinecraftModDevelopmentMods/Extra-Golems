package com.golems.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.golems.entity.GolemBase;
import com.golems.events.GolemBuildEvent;
import com.golems.integration.GolemDescriptionManager;

import akka.japi.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ItemInfoBook extends Item {
	
	protected static final String KEY_PAGES = "pages";
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_AUTHOR = "author";
	/** The GolemDescriptionManager for this item to use in book displays **/
	protected static final BookExtraGolems DESC = new BookExtraGolems();
	/** Used for NBT book. Each String entry is a separate page. **/
	protected static final List<String> PAGES = new ArrayList();
	
	public ItemInfoBook() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
	}
	
	/** Meant to be called only once upon World initialization **/
	public static void initGolemInfo(World world) {
		if(PAGES.isEmpty())
		{
			// make a map of golems and their respective blocks
			final Map<GolemBase, Block> golemMap = getDummyGolems(world);
			// make a sorted version of this map by making a sorted list and using it later
			final List<GolemBase> sorted = new LinkedList(golemMap.keySet());
			// make a comparator to sort golems by attack power
			final Comparator<GolemBase> comparator = new Comparator<GolemBase>() {
				@Override
				public int compare(GolemBase arg0, GolemBase arg1) {
					float attack0 = arg0.getBaseAttackDamage();
					float attack1 = arg1.getBaseAttackDamage();
					return attack0 < attack1 ? -1 : (attack0 - attack1 < 0.01F ? 0 : 1);
				}
			};
			// sort the List
			Collections.sort(sorted, comparator);
			
			// iterate through the sorted list and add Block+Description pairs to another list
			final List<List<String>> DESC_LIST = new LinkedList<List<String>>();
			// use the sorted list
			for(GolemBase golem : sorted) {
				List<String> desc = DESC.getEntriesForBook(golem);
				String blockName = I18n.format("itemGroup.buildingBlocks") + " : " + golemMap.get(golem).getLocalizedName();
				// insert block name at beginning of description
				desc.add(0, blockName);
				// add the final description to the main list
				DESC_LIST.add(desc);
			}
			
			System.out.println("Done making GolemInfoMap");
			
			// finally, use DESC_LIST to make each page of the book
			final String SEP = "\n\n";
			// make the PAGES for the NBT tag (until we figure out something better)
			for(List<String> list : DESC_LIST) {
				String page = "";
				// Consolidate all the description data into one string
				for(String s : list) {
					page += s + SEP;
				}
				PAGES.add(page);
			}
		}
	}
	
	/**
	 * @param world used just to make the dummy golems
	 * @return a Map that indicates which Golems require
	 * which Blocks in their construction. Supports add-ons.
	 **/
	private static Map<GolemBase, Block> getDummyGolems(World world) {
		Map<GolemBase, Block> map = new HashMap();
		// for each block, see if there is a golem that matches it
		for(Block block : Block.REGISTRY) { 
			final GolemBuildEvent event = new GolemBuildEvent(world, block.getDefaultState(), true, true);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isGolemNull() && !event.isGolemBanned()) {
				map.put(event.getGolem(), block);
			}
		}
		return map;
	}
	
	// DEBUG
	public static final void printDesc() {
		System.out.println("\nPrinting DescriptionMap pages:");
		for(String page : PAGES) {
			System.out.print(page.replaceAll("\n\n", "\n"));
		}
	}
	
	/**
     * allows items to add custom lines of information to the mouseover description
     *
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    	//tooltip.add
    }*/
	
	/**
     * Called when the equipped item is right clicked.
     */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		
		this.addNBT(itemstack);
		
		if (playerIn.getEntityWorld().isRemote)
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiScreenBook(playerIn, itemstack, false));
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	private void addNBT(ItemStack itemstack) 
	{
		if(itemstack != null) {
			NBTTagCompound nbt = itemstack.hasTagCompound() ? itemstack.getTagCompound() : new NBTTagCompound();
			// skip this bit if the NBT already has been set
			if(nbt.hasKey(KEY_PAGES))
				return;
			//create pages
			NBTTagList pagesTag = new NBTTagList();
			for (String pageText : PAGES) {
				pagesTag.appendTag(new NBTTagString(pageText));
			}
			
			nbt.setTag(KEY_PAGES, pagesTag);
			nbt.setString(KEY_AUTHOR, "");
		 	nbt.setString(KEY_TITLE, I18n.format("item.info_book.name"));
			itemstack.setTagCompound(nbt);
		}
	}

	protected static final class BookExtraGolems extends GolemDescriptionManager {
		
		public BookExtraGolems() {
			super();
			this.showAttack = true;
			this.showMultiTexture = true;
			this.showSpecial = true;
			this.showFireproof = true;
			this.showKnockbackResist = false;
		}

		public List<String> getEntriesForBook(final GolemBase golem) {
			List<String> entries = super.getEntityDescription(golem);
			// change white formatting to black
			for(int i = 0, l = entries.size(); i < l; i++) {
				String s = TextFormatting.getTextWithoutFormattingCodes(entries.get(i));
				entries.set(i, s);
			}
			// TODO add these keys to all .lang files
			// insert name and health tips
			entries.add(0, trans("entitytip.name") + ": "
					+ golem.getName());
			entries.add(1, trans("entitytip.health") + " : "
					+ golem.getMaxHealth());
			
			return entries;
		}
	}
}
