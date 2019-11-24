package com.golems.main;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class GolemItems {

	private GolemItems() {
		//
	}

	////// ITEMS //////
	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":golem_paper")
	public static final Item golemPaper = null;

	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":spawn_bedrock_golem")
	public static final Item spawnBedrockGolem = null;

	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":info_book")
	public static final Item infoBook = null;

	////// BLOCKS //////
	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":golem_head")
	public static final Block golemHead = null;

	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":light_provider_full")
	public static final Block blockLightSource = null;

	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":water_light_provider_full")
	public static final Block blockLightSourceWater = null;

	@GameRegistry.ObjectHolder(ExtraGolems.MODID + ":power_provider_all")
	public static final Block blockPowerSource = null;
}
