package com.golems.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;
import com.golems.events.GolemBuildEvent;
import com.golems.integration.GolemDescriptionManager;

import akka.japi.Pair;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemInfoBook extends Item {
	
	protected static final String KEY_PAGES = "pages";
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_AUTHOR = "author";
	/** The GolemDescriptionManager for this item to use in book displays **/
	protected static final BookDescriptionManager DESC = new BookDescriptionManager();
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
			// sort the List using the above comparator
			Collections.sort(sorted, comparator);
			
			// iterate through the sorted list and add Block+Description pairs to another list
			final List<List<String>> DESC_LIST = new LinkedList<List<String>>();
			// use the sorted list
			for(GolemBase golem : sorted) {
				final List<String> desc = DESC.getEntityDescription(golem);
				final String blockName = I18n.format("itemGroup.buildingBlocks") + " : " + golemMap.get(golem).getLocalizedName() + "\n";
				// insert block name at beginning of description
				desc.add(0, blockName);
				// add the now-complete description to the main list
				DESC_LIST.add(desc);
			}
			// use the information gathered to populate the PAGES field
			buildPages(DESC_LIST);
		}
	}
	
	private static void buildPages(final List<List<String>> fromList) {
		// first add the introduction to the book
		PAGES.addAll(DESC.getIntroduction());
		// use the list of descriptions to make the remaining pages of the book
		final String SEP = "\n";
		for(List<String> list : fromList) {
			String page = "";
			// Consolidate all the description data into one string
			for(String s : list) {
				page += s + SEP;
			}
			// add the page
			PAGES.add(page);
		}
	}
	
	/**
	 * @param world used just to make the dummy golems
	 * @return a Map that indicates which Golems require
	 * which Blocks in their construction. Supports add-ons.
	 **/
	private static Map<GolemBase, Block> getDummyGolems(final World world) {
		final Map<GolemBase, Block> map = new HashMap();
		// for each entity, find out if it's a golem and if it's creative return includes a block.
		final Set<ResourceLocation> set = EntityList.getEntityNameList();
		for(EntityEntry entry : ForgeRegistries.ENTITIES) {
			Entity e = entry.newInstance(world);
			if(e instanceof GolemBase) {
				GolemBase instance = (GolemBase) e;
				final ItemStack stack = instance.getCreativeReturn();
				if(stack != null && stack.getItem() instanceof ItemBlock) {
					Block b = ((ItemBlock)stack.getItem()).getBlock();
					map.put(instance, b);
				}
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

	protected static final class BookDescriptionManager extends GolemDescriptionManager {
		
		private static final ArrayList<String> INTRO = new ArrayList<String>(); {
			// page 1: "Welcome"
			INTRO.add(trans("golembook.intro"));
			// page 2: "Make Golem Spell"
			INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_spell.intro", trans("item.golem_paper.name")) 
					+ "\n\n" + I18n.format("golembook.recipe_spell.recipe", trans("item.golem_paper.name"), trans("item.paper.name"), trans("item.feather.name"),
					trans("item.dyePowder.black.name"), trans("item.redstone.name"))));
			// page 3: "Make Golem Head"
			INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_head.intro", trans("tile.golem_head.name")) + "\n\n"
					+ trans("golembook.recipe_head.recipe", trans("tile.golem_head.name"), trans("item.golem_paper.name"), trans("tile.pumpkin.name"))));
			// page 4: "Make Golem"
			INTRO.add(trans("golembook.build_golem.intro") + "\n\n" + trans("golembook.build_golem.howto1") + " "
					+ trans("golembook.build_golem.howto2") + "\n\n" + I18n.format("golembook.build_golem.howto3", trans("tile.golem_head.name")));
		};
		
		public BookDescriptionManager() {
			super();
			this.showAttack = true;
			this.showMultiTexture = true;
			this.showSpecial = true;
			this.showFireproof = true;
			this.showKnockbackResist = false;
		}
		
		/** @return a COPY of the introduction pages, each as a separate String element **/
		public List<String> getIntroduction() {
			return (ArrayList<String>) INTRO.clone();
		}

		@Override
		public List<String> getEntityDescription(final GolemBase golem) {
			final List<String> list = new LinkedList<>();
			
			// ADD NAME TIP
			list.add(TextFormatting.DARK_GRAY + trans("entitytip.name") + ": "
					+ TextFormatting.BLACK + golem.getName() + "\n");
			// ADD HEALTH (ROUNDED) TIP
			list.add(TextFormatting.DARK_GRAY + trans("entitytip.health") + " : " + TextFormatting.BLACK
					+ Math.round(golem.getMaxHealth()) + TextFormatting.DARK_RED + " \u2764" + TextFormatting.BLACK);
			// ADD ATTACK POWER TIP
			list.add(TextFormatting.DARK_GRAY + trans("entitytip.attack") + " : "
					+ TextFormatting.BLACK + golem.getBaseAttackDamage() + " \u2694" + "\n");
			// ADD FIREPROOF TIP
			if (golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem)) {
				list.add(TextFormatting.GOLD + trans("entitytip.is_fireproof"));
			}
			// ADD INTERACT-TEXTURE TIP
			if (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture()) {
				list.add(TextFormatting.BLUE + trans("entitytip.click_change_texture"));
			}
			// ADD SPECIAL
			golem.addSpecialDesc(list);
			
			return list;
		}
	}
}
