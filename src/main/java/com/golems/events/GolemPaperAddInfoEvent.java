package com.golems.events;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Fired after {@link ItemGolemPaper} has added information to itself. Only activates when player is
 * sneaking.
 **/
@SideOnly(Side.CLIENT)
public class GolemPaperAddInfoEvent extends Event {

	public final ItemStack itemStack;
	@Nullable
	public final World world;
	public final List<String> infoList;
	public final ITooltipFlag flagIn;

	public GolemPaperAddInfoEvent(ItemStack stack, @Nullable World worldIn, List<String> tooltip,
			ITooltipFlag flagIn) {
		this.itemStack = stack;
		this.world = worldIn;
		this.infoList = tooltip;
		this.flagIn = flagIn;
	}

	/** Removes all entries matching the passed String **/
	public void replaceWith(String toRemove, String replace) {
		for (String line : this.infoList) {
			line.replaceAll(toRemove, replace);
		}
	}
}
