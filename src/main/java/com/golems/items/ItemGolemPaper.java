package com.golems.items;

import java.util.List;

import javax.annotation.Nullable;

import com.golems.events.GolemPaperAddInfoEvent;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGolemPaper extends Item {

	public ItemGolemPaper() {
		super();
		this.setCreativeTab(CreativeTabs.MISC);
	}

	/**
	 * allows items to add custom lines of information to the mouseover description.
	 *

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip,
			final ITooltipFlag flagIn) {
		if (GuiScreen.isShiftKeyDown()) {
			// String loreListIntro= TextFormatting.WHITE + trans("tooltip.in_order_of_attack") +
			// ":";
			// par3List.add(loreListIntro);
			final String C = ", "; // C = comma
			final String toAdd = trans("tile.blockDiamond.name", C) + trans("tile.blockEmerald.name") 
					+ add("tile.obsidian.name", C) + add("tile.glass.name") 
					+ add("tile.stainedGlass.name", C) + add("tile.lightgem.name") 
					+ add("tile.whiteStone.name", C) + add("tile.quartzBlock.default.name") 
					+ add("tile.blockGold.name", C) + add("tile.prismarine.rough.name") 
					+ add("tile.icePacked.name", C) + add("tile.netherBrick.name") 
					+ add("tile.blockIron.name", C) + add("tile.seaLantern.name") 
					+ add("tile.sandStone.name", C) + add("tile.redSandStone.default.name")
					+ add("tile.clayHardened.name", C) + add("tile.clayHardenedStained.name")
					 + add("material.shroom_block", C) + add("material.log") 
					+ add("tile.tnt.name", C) + add("tile.blockCoal.name") 
					+ add("tile.blockRedstone.name", C) + add("tile.blockLapis.name") 
					+ add("tile.slime.name", C) +add("tile.clay.name") 
					+ add("tile.bookshelf.name", C) + add("tile.sponge.dry.name") 
					+ add("tile.melon.name", C) + add("tile.workbench.name") 
					+ add("tile.cloth.name", C) + add("tile.hayBlock.name") 
					+ add("tile.leaves.name", C) + add("tile.magma.name") 
					+ add("tile.netherWartBlock.name", C) + add("tile.redNetherBrick.name") 
					+ add("tile.boneBlock.name");
			tooltip.add(toAdd);
			GolemPaperAddInfoEvent event = new GolemPaperAddInfoEvent(stack, worldIn, tooltip,
					flagIn);
			MinecraftForge.EVENT_BUS.post(event);
		} else {
			final String lorePressShift = TextFormatting.GRAY + trans("tooltip.press") + " "
					+ TextFormatting.YELLOW + trans("tooltip.shift").toUpperCase() + " "
					+ TextFormatting.GRAY + trans("tooltip.for_golem_materials");
			tooltip.add(lorePressShift);
		}
	}
	
	@SideOnly(Side.CLIENT)
	private List<String> add(List<String> list, String toAdd, String comma) {
		list.add(I18n.format(toAdd) + comma);
		return list;
	}
	*/
}
