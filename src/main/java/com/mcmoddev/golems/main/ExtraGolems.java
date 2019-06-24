package com.mcmoddev.golems.main;

import com.mcmoddev.golems.events.handlers.GolemCommonEventHandler;
import com.mcmoddev.golems.integration.ModIds;
import com.mcmoddev.golems.proxies.ProxyClient;
import com.mcmoddev.golems.proxies.ProxyCommon;
import com.mcmoddev.golems.proxies.ProxyServer;
import com.mcmoddev.golems.util.BlockTagUtil;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraGolems.MODID)
@Mod.EventBusSubscriber(modid = ExtraGolems.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
		ExtraGolems.PROXY.registerListeners();
		BlockTagUtil.loadTags();
		ExtraGolemsEntities.initEntityTypes();
		ExtraGolemsConfig.setupConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER /*world*/, ExtraGolemsConfig.SERVER_CONFIG);
	}

	private void setup(final FMLCommonSetupEvent event) {

	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		if (ModList.get().isLoaded(ModIds.TOP)) {
			// InterModComms.sendTo(ModIds.TOP, "getTheOneProbe", TOPExtraGolems.GetTheOneProbe::new);
		}
	}

	@SubscribeEvent
	public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
		ExtraGolems.LOGGER.info("registerEntities");
		ExtraGolems.PROXY.registerEntities(event);
		ExtraGolems.PROXY.registerEntityRenders();
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		ExtraGolems.LOGGER.info("registerItems");
		ExtraGolems.PROXY.registerItems(event);
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		ExtraGolems.LOGGER.info("registerBlocks");
		ExtraGolems.PROXY.registerBlocks(event);
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
