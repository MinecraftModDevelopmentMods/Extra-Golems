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

	public GolemPaperAddInfoEvent(final ItemStack stack, @Nullable final World worldIn, final List<String> tooltip,
			final ITooltipFlag flagIn) {
		this.itemStack = stack;
		this.world = worldIn;
		this.infoList = tooltip;
		this.flagIn = flagIn;
	}

	/** Removes all entries matching the passed String **/
	/*
	public void replaceWith(final String toRemove, final String replace) {
		for (final String line : this.infoList) {
			@SuppressWarnings("unused")
			final String temp = line.replaceAll(toRemove, replace);
		}
	}
	*/
}
