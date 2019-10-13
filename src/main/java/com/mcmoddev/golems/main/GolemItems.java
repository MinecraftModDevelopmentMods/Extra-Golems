package com.mcmoddev.golems.main;

import com.mcmoddev.golems.entity.CraftingGolem;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

public final class GolemItems {

	private GolemItems() {
		//
	}

	////// ITEMS //////
	@ObjectHolder(ExtraGolems.MODID + ":golem_paper")
	public static final Item GOLEM_SPELL = null;

	@ObjectHolder(ExtraGolems.MODID + ":spawn_bedrock_golem")
	public static final Item SPAWN_BEDROCK_GOLEM = null;

	@ObjectHolder(ExtraGolems.MODID + ":info_book")
	public static final Item GOLEM_BOOK = null;

	////// BLOCKS //////
	@ObjectHolder(ExtraGolems.MODID + ":golem_head")
	public static final Block GOLEM_HEAD = null;

	@ObjectHolder(ExtraGolems.MODID + ":light_provider_full")
	public static final Block UTILITY_LIGHT = null;

	@ObjectHolder(ExtraGolems.MODID + ":power_provider_all")
	public static final Block UTILITY_POWER = null;
	
	////// OTHER //////
	@ObjectHolder(ExtraGolems.MODID + ":crafting_portable")
	public static final ContainerType<CraftingGolem.ContainerPortableWorkbench> CRAFTING = null;
}
