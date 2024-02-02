package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.client.EGClientEvents;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.item.GolemSpellItem;
import com.mcmoddev.golems.network.EGNetwork;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(ExtraGolems.MODID)
public class ExtraGolems {

	public static final String MODID = "golems";

	public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final EGConfig CONFIG = new EGConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	public ExtraGolems() {
		// register and load config
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ExtraGolems::loadConfig);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ExtraGolems::reloadConfig);
		// init network
		EGNetwork.register();
		// init registry
		EGRegistry.register();
		// register event handlers
		EGEvents.register();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ExtraGolems::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ExtraGolems::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(AddonLoader::onAddPackFinders);
		// register client event handlers
		DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> EGClientEvents::register);
	}

	private static void setup(final FMLCommonSetupEvent event) {
		// init addons
		AddonLoader.init();
		// register dispenser behavior
		GolemSpellItem.registerDispenserBehavior();
		GolemHeadBlock.registerDispenserBehavior();
	}

	private static void enqueueIMC(final InterModEnqueueEvent event) {
		// register TheOneProbe integration
		if (ModList.get().isLoaded("theoneprobe")) {
			ExtraGolems.LOGGER.info("Extra Golems detected TheOneProbe, registering plugin now");
			InterModComms.sendTo(MODID, "theoneprobe", "getTheOneProbe", () -> new com.mcmoddev.golems.integration.TOPDescriptionManager.GetTheOneProbe());
		}
	}

	private static void loadConfig(final ModConfigEvent.Loading event) {
		CONFIG.bake();
	}

	private static void reloadConfig(final ModConfigEvent.Reloading event) {
		CONFIG.bake();
	}

	/**
	 * Checks all registered GolemContainers until one is found that is constructed
	 * out of the passed Blocks. Parameters are the current World and the 4 blocks
	 * that will be used to build this Golem.
	 *
	 * @param level the level
	 * @param bodyBlock the block directly below the head
	 * @param bodySupportBlock the block 2 positions below the head
	 * @param leftArmBlock the block adjacent to {@code bodyBlock}
	 * @param rightArmBlock the block adjacent to {@code bodyBlock} and opposite {@code leftArmBlock}
	 * @return the ID of the Golem that can be spawned with the given blocks
	 **/
	@Nullable
	public static ResourceKey<Golem> getGolemId(Level level, Block bodyBlock, Block bodySupportBlock, Block leftArmBlock, Block rightArmBlock) {
		// load registry
		final Registry<Golem> registry = level.registryAccess().registryOrThrow(EGRegistry.Keys.GOLEM);
		// iterate all registered golems
		for (ResourceKey<Golem> key : registry.registryKeySet()) {
			// check if the blocks match each golem container
			GolemContainer container = GolemContainer.getOrCreate(level.registryAccess(), key.location());
			if (container.getGolem().getBlocks().matches(bodyBlock, bodySupportBlock, leftArmBlock, rightArmBlock)) {
				return key;
			}
		}
		return null;
	}
}
