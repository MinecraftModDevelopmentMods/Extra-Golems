package com.golems.items;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;
import com.golems.gui.GuiLoader;
import com.golems.integration.GolemDescriptionManager;
import com.golems.main.ExtraGolems;
import com.golems.network.GolemNetworkHandler;
import com.golems.network.PacketTriggerGolemEntryLoad;
import com.golems.util.GolemEntry;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class ItemInfoBook extends Item {
			
	public ItemInfoBook() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if(playerIn.getEntityWorld().isRemote) {
			GuiLoader.loadBookGui(playerIn, itemstack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	/**
     * Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
     * update it's contents.
     *
	@Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		
		if(!worldIn.isRemote && ExtraGolems.proxy.DUMMY_GOLEMS.isEmpty() && entityIn instanceof EntityPlayerMP) {
			System.out.println("Side: " + (worldIn.isRemote ? "CLIENT" : "SERVER"));
			ExtraGolems.proxy.DUMMY_GOLEMS.addAll(GolemEntry.getDummyGolemList(worldIn));
			System.out.println("[GuiLoader] Sending GolemEntry Information from Server to Client...");
			GolemNetworkHandler.INSTANCE.sendTo(new PacketTriggerGolemEntryLoad(), (EntityPlayerMP)entityIn);
		}
    }*/
	
//	
//
//	protected static final class BookDescriptionManager extends GolemDescriptionManager {
//		
//		private static final ArrayList<String> INTRO = new ArrayList<String>(); 
//		
//		public BookDescriptionManager() {
//			super();
//			this.showAttack = true;
//			this.showMultiTexture = true;
//			this.showSpecial = true;
//			this.showFireproof = true;
//			this.showKnockbackResist = false;
//		}
//		
//		/** @return a COPY of the introduction pages, each page as a separate String element **/
//		public List<String> getIntroduction() {
//			// reset INTRO and add pages
//			INTRO.clear();
//			// page 1: "Welcome"
//			INTRO.add(trans("golembook.intro1") + "\n" + trans("golembook.intro2"));
//			// page 2: "Part 1"
//			String partIntro = TextFormatting.GOLD + trans("golembook.part_intro") + TextFormatting.BLACK;
//			INTRO.add("\n\n" + partIntro + "\n\n" + trans("golembook.part1") + "\n\n" + partIntro);
//			// page 3: "Make Golem Spell"
//			INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_spell.intro", trans("item.golem_paper.name")) 
//					+ "\n\n" + I18n.format("golembook.recipe_spell.recipe", trans("item.golem_paper.name"), trans("item.paper.name"), trans("item.feather.name"),
//					trans("item.dyePowder.black.name"), trans("item.redstone.name"))));
//			// page 4: "Make Golem Head"
//			INTRO.add(TextFormatting.getTextWithoutFormattingCodes(I18n.format("golembook.recipe_head.intro", trans("tile.golem_head.name")) + "\n\n"
//					+ trans("golembook.recipe_head.recipe", trans("tile.golem_head.name"), trans("item.golem_paper.name"), trans("tile.pumpkin.name"))));
//			// page 5: "Make Golem"
//			INTRO.add(trans("golembook.build_golem.intro") + "\n\n" + trans("golembook.build_golem.howto1") + " "
//					+ trans("golembook.build_golem.howto2") + "\n\n" + I18n.format("golembook.build_golem.howto3", trans("tile.golem_head.name")));
//			// page 6: "Part 2"
//			INTRO.add("\n\n" + partIntro + "\n\n" + trans("golembook.part2") + "\n\n" + partIntro);
//			
//			return (ArrayList<String>) INTRO.clone();
//		}
//
//		@Override
//		public List<String> getEntityDescription(final GolemBase golem) {
//			final List<String> list = new LinkedList<>();
//			
//			// ADD NAME TIP
//			list.add(TextFormatting.GRAY + trans("entitytip.name") + ": "
//					+ TextFormatting.BLACK + golem.getName() + "\n");
//			// ADD HEALTH (ROUNDED) TIP
//			list.add(TextFormatting.GRAY + trans("entitytip.health") + " : " + TextFormatting.BLACK
//					+ Math.round(golem.getMaxHealth()) + TextFormatting.DARK_RED + " \u2764" + TextFormatting.BLACK);
//			// ADD ATTACK POWER TIP
//			list.add(TextFormatting.GRAY + trans("entitytip.attack") + " : "
//					+ TextFormatting.BLACK + golem.getBaseAttackDamage() + " \u2694" + "\n");
//			// ADD FIREPROOF TIP
//			if (golem.isImmuneToFire() && !(golem instanceof EntityBedrockGolem)) {
//				list.add(TextFormatting.GOLD + trans("entitytip.is_fireproof"));
//			}
//			// ADD INTERACT-TEXTURE TIP
//			if (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture()) {
//				list.add(TextFormatting.BLUE + trans("entitytip.click_change_texture"));
//			}
//			// ADD SPECIAL
//			golem.addSpecialDesc(list);
//			
//			return list;
//		}
//	}
}
