package com.golems.main;

import com.golems.blocks.BlockGolemHead;
import com.golems.blocks.BlockLightProvider;
import com.golems.blocks.BlockPowerProvider;
import com.golems.blocks.TileEntityMovingLightSource;
import com.golems.blocks.TileEntityMovingPowerSource;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemGolemPaper;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GolemItems 
{
	public static Item golemPaper;				
	public static Item spawnBedrockGolem;	
	
	public static Block golemHead;				
	public static Block blockLightSourceFull;	
	public static Block blockLightSourceHalf;	
	public static Block blockPowerSource;	
	
	public static ItemBlock ibGolemHead;

	public static void mainRegistry()
	{
		initBlocks();
		initItemBlocks();
		initItems();

		register(TileEntityMovingLightSource.class, "TileEntityMovingLightSource");
		register(TileEntityMovingPowerSource.class, "TileEntityMovingPowerSource");

		register(golemPaper, "golem_paper");
		register(spawnBedrockGolem, "spawn_bedrock_golem");
		registerWithItemBlock(golemHead, ibGolemHead, "golem_head");
		register(blockLightSourceFull, "light_provider_full");
		register(blockLightSourceHalf, "light_provider_half");
		register(blockPowerSource, "power_provider_all");
	}

	private static void initBlocks()
	{
		golemHead = new BlockGolemHead();
		blockLightSourceFull = new BlockLightProvider(1.0F);
		blockLightSourceHalf = new BlockLightProvider(0.5F);
		blockPowerSource = new BlockPowerProvider();
	}
	
	private static void initItemBlocks()
	{
		ibGolemHead = new ItemBlock(golemHead);
	}

	private static void initItems()
	{
		golemPaper = new ItemGolemPaper();
		spawnBedrockGolem = new ItemBedrockGolem();
	}

	private static void register(Item item, String name)
	{
		item.setUnlocalizedName(name).setRegistryName(ExtraGolems.MODID, name);
		GameRegistry.register(item);
	}
	
	private static void registerWithItemBlock(Block block, ItemBlock itemBlock, String name)
	{
		register(block, name);
		itemBlock.setUnlocalizedName(name).setRegistryName(ExtraGolems.MODID, name);
		GameRegistry.register(itemBlock);
	}

	private static void register(Block block, String name)
	{
		block.setUnlocalizedName(name).setRegistryName(ExtraGolems.MODID, name);
		GameRegistry.register(block);
	}

	private static void register(Class <? extends TileEntity> teClass, String name)
	{
		GameRegistry.registerTileEntity(teClass, ExtraGolems.MODID + "." + name);
	}
}
