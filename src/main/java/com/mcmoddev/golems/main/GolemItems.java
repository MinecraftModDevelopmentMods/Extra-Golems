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
	public static Item GOLEM_SPELL;

	@ObjectHolder(ExtraGolems.MODID + ":spawn_bedrock_golem")
	public static Item SPAWN_BEDROCK_GOLEM;

	@ObjectHolder(ExtraGolems.MODID + ":info_book")
	public static Item GOLEM_BOOK;

	////// BLOCKS //////
	@ObjectHolder(ExtraGolems.MODID + ":golem_head")
	public static Block GOLEM_HEAD;

	@ObjectHolder(ExtraGolems.MODID + ":light_provider_full")
	public static Block UTILITY_LIGHT;

	@ObjectHolder(ExtraGolems.MODID + ":power_provider_all")
	public static Block UTILITY_POWER;
	
	////// OTHER //////
	@ObjectHolder(ExtraGolems.MODID + ":crafting_portable")
	public static ContainerType<CraftingGolem.ContainerPortableWorkbench> CRAFTING;
}
