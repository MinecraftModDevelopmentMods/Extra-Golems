package com.mcmoddev.golems.main;

import com.mcmoddev.golems.events.handlers.GolemCommonEventHandler;
import com.mcmoddev.golems.proxies.ProxyClient;
import com.mcmoddev.golems.proxies.ProxyCommon;
import com.mcmoddev.golems.proxies.ProxyServer;
import com.mcmoddev.golems.util.config.GolemConfiguration;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraGolems.MODID)
public class ExtraGolems {

	public static final String MODID = "golems";
	@SuppressWarnings("Convert2MethodRef")
	//DO NOT USE METHOD REFERENCES. THESE ARE BAD! (according to gigaherz)
	public static final ProxyCommon PROXY = DistExecutor.runForDist(() -> () -> new ProxyClient(),
		() -> () -> new ProxyServer());

	public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);
	// probably will break
	public static final GolemConfiguration CONFIG = new GolemConfiguration();

	public ExtraGolems() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadConfig);
		MinecraftForge.EVENT_BUS.register(new GolemCommonEventHandler());
	}

	private void setup(final FMLCommonSetupEvent event) {
		//TODO: Figure out what replaces Configuration
		//Config.mainRegistry(new Configuration(event.getSuggestedConfigurationFile()));
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		//TODO: Wait for WAILA and TheOneProbe
	}
	
	// probably the wrong event for this...
	private void loadConfig(final ModConfig.Loading event) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG.build());
		CONFIG.loadData();
	}
	
//	@Mod.EventHandler
//	public static void init(final FMLInitializationEvent event) {
//
//		PROXY.registerEvents();
//
//		if (Loader.isModLoaded(ModIds.WAILA)) {
//			FMLInterModComms.sendMessage(ModIds.WAILA, "register",
//				"com.golems.integration.waila.WailaExtraGolems.callbackRegister");
//		}
//		if (Loader.isModLoaded(ModIds.TOP)) {
//			FMLInterModComms.sendFunctionMessage(ModIds.TOP, "getTheOneProbe",
//				"com.golems.integration.theoneprobe.TOPExtraGolems$GetTheOneProbe");
//		}
//		// Trial-run these methods to give the user feedback if there's errors
//		Config.getPlainsGolems();
//		Config.getDesertGolems();
//	}
}
