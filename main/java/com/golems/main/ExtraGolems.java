package com.golems.main;

import com.golems.proxies.CommonProxy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = ExtraGolems.MODID, name = ExtraGolems.NAME, version = ExtraGolems.VERSION, acceptedMinecraftVersions = ExtraGolems.MCVERSION)
public class ExtraGolems 
{	
	public static final String MODID = "golems";
	public static final String NAME = "Extra Golems";
	public static final String VERSION = "6.02";
	public static final String MCVERSION = "1.10.2";
	
	@SidedProxy(clientSide = "com." + MODID + ".proxies.ClientProxy", serverSide = "com." + MODID + ".proxies.CommonProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(ExtraGolems.MODID)
	public static ExtraGolems instance;
	
	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) 
	{	
		Config.mainRegistry(new Configuration(event.getSuggestedConfigurationFile()));
		GolemItems.mainRegistry();
		proxy.registerEntities();
		proxy.preInitRenders();
	}
	
	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) 
	{		
		registerCrafting();
		proxy.registerEvents();
	}
	
	public static void registerCrafting()
	{
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(GolemItems.golemPaper, Config.paperRecipeOutput), Items.FEATHER, Items.REDSTONE, "dyeBlack", Items.PAPER));
		GameRegistry.addShapelessRecipe(new ItemStack(GolemItems.golemHead, 1), GolemItems.golemPaper, Blocks.PUMPKIN);
	}
}

