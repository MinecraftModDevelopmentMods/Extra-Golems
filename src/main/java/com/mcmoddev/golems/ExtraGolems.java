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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(ExtraGolems.MODID)
public class ExtraGolems {

	public static final String MODID = "golems";

	public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);

	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
	public static final EGConfig CONFIG = new EGConfig(BUILDER);
	public static final ModConfigSpec SPEC = BUILDER.build();

	public ExtraGolems(IEventBus modEventBus, ModContainer modContainer) {
		// register and load config
		modContainer.registerConfig(ModConfig.Type.COMMON, SPEC);
		modEventBus.addListener(ExtraGolems::loadConfig);
		modEventBus.addListener(ExtraGolems::reloadConfig);
		// init network
		modEventBus.addListener(EGNetwork::register);
		// init registry
		EGRegistry.register(modEventBus);
		// register event handlers
		EGEvents.register(modEventBus);
		modEventBus.addListener(ExtraGolems::setup);
		modEventBus.addListener(ExtraGolems::enqueueIMC);
		// init addons
		AddonLoader.register(modEventBus);
		// register client event handlers
		if (FMLEnvironment.dist == Dist.CLIENT) {
			EGClientEvents.register(modEventBus);
		}
	}

	private static void setup(final FMLCommonSetupEvent event) {
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
