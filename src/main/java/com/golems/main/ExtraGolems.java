package com.golems.main;

import com.golems.events.handlers.GolemCommonEventHandler;
import com.golems.proxies.ProxyClient;
import com.golems.proxies.ProxyCommon;
import com.golems.proxies.ProxyServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
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

	public ExtraGolems() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		MinecraftForge.EVENT_BUS.register(new GolemCommonEventHandler());
	}

	private void setup(final FMLCommonSetupEvent event) {
		//TODO: Figure out what replaces Configuration
		//Config.mainRegistry(new Configuration(event.getSuggestedConfigurationFile()));
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		//TODO: Wait for WAILA and TheOneProbe
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
