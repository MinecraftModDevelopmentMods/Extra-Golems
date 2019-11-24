package com.golems.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.golems.integration.ModIds;
import com.golems.proxies.CommonProxy;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = ExtraGolems.MODID, name = ExtraGolems.NAME, version = ExtraGolems.VERSION)
public class ExtraGolems {

	public static final String MODID = "golems";
	protected static final String NAME = "Extra Golems";
	protected static final String VERSION = "6.3.0";

	@SidedProxy(clientSide = "com." + MODID + ".proxies.ClientProxy", serverSide = "com." + MODID
		+ ".proxies.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance(ExtraGolems.MODID)
	public static ExtraGolems instance;

	public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);

	@Mod.EventHandler
	public static void preInit(final FMLPreInitializationEvent event) {
		Config.mainRegistry(new Configuration(event.getSuggestedConfigurationFile()));
		proxy.registerEntities();
	}

	@Mod.EventHandler
	public static void init(final FMLInitializationEvent event) {
		// events
		proxy.registerEvents();
		// gui handler
		//NetworkRegistry.INSTANCE.registerGuiHandler(instance, null);
		
//		if (Loader.isModLoaded(ModIds.WAILA)) {
//			FMLInterModComms.sendMessage(ModIds.WAILA, "register",
//				"com.golems.integration.waila.WailaExtraGolems.callbackRegister");
//		}
		if (Loader.isModLoaded(ModIds.TOP)) {
			FMLInterModComms.sendFunctionMessage(ModIds.TOP, "getTheOneProbe",
				"com.golems.integration.theoneprobe.TOPExtraGolems$GetTheOneProbe");
		}
		// Trial-run these methods to give the user feedback if there's errors
		Config.getPlainsGolems();
		Config.getDesertGolems();
		
		// register crafting recipes
		IRecipe recipeGolemPaper = new ShapelessOreRecipe(new ItemStack(GolemItems.golemPaper, 1), Items.FEATHER, Items.REDSTONE, "dyeBlack", Items.PAPER);
		GameRegistry.addRecipe(recipeGolemPaper);
		GameRegistry.addShapelessRecipe(new ItemStack(GolemItems.golemHead, 1), GolemItems.golemPaper, Blocks.PUMPKIN);
		GameRegistry.addShapelessRecipe(new ItemStack(GolemItems.infoBook), Blocks.PUMPKIN, Items.BOOK);
	}
}
