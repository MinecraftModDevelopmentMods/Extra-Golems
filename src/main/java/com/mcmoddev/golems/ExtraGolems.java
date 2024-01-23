package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.client.EGClientEvents;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.item.GolemSpellItem;
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
import java.util.Map.Entry;

@Mod(ExtraGolems.MODID)
public class ExtraGolems {

	public static final String MODID = "golems";

	public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final EGConfig CONFIG = new EGConfig(BUILDER);
	public static final ForgeConfigSpec SPEC = BUILDER.build();

	private static final String PROTOCOL_VERSION = "3";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public ExtraGolems() {
		// register and load config
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ExtraGolems::loadConfig);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ExtraGolems::reloadConfig);
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
	 * that will be used to calculate this Golem. It is okay to pass {@code null} or
	 * Air.
	 *
	 * @param level the level
	 * @param bodyBlock the block directly below the head
	 * @param bodySupportBlock the block 2 positions below the head
	 * @param leftArmBlock the block adjacent to {@code bodyBlock}
	 * @param rightArmBlock the block adjacent to {@code bodyBlock} and opposite {@code leftArmBlock}
	 * @return the constructed GolemBase instance if there is one for the passed blocks, otherwise null
	 * @see GolemContainer#matches(Block, Block, Block, Block)
	 **/
	@Nullable
	public static GolemBase getGolem(Level level, Block bodyBlock, Block bodySupportBlock, Block leftArmBlock, Block rightArmBlock) {
		ResourceLocation id = getGolemId(level, bodyBlock, bodySupportBlock, leftArmBlock, rightArmBlock);
		if (null == id) {
			return null;
		}
		return GolemBase.create(level, id);
	}

	/**
	 * Checks all registered GolemContainers until one is found that is constructed
	 * out of the passed Blocks. Parameters are the current World and the 4 blocks
	 * that will be used to calculate this Golem. It is okay to pass {@code null} or
	 * Air.
	 *
	 * @param level the level
	 * @param bodyBlock the block directly below the head
	 * @param bodySupportBlock the block 2 positions below the head
	 * @param leftArmBlock the block adjacent to {@code bodyBlock}
	 * @param rightArmBlock the block adjacent to {@code bodyBlock} and opposite {@code leftArmBlock}
	 * @return the constructed GolemBase instance if there is one for the passed blocks, otherwise null
	 * @see GolemContainer#matches(Block, Block, Block, Block)
	 **/
	@Nullable
	public static ResourceLocation getGolemId(Level level, Block bodyBlock, Block bodySupportBlock, Block leftArmBlock, Block rightArmBlock) {
		ResourceLocation id = null;
		final Registry<GolemContainer> registry = level.registryAccess().registryOrThrow(Keys.GOLEM_CONTAINERS);
		for (Entry<ResourceKey<GolemContainer>, GolemContainer> entry : registry.entrySet()) {
			if (entry.getValue().matches(bodyBlock, bodySupportBlock, leftArmBlock, rightArmBlock)) {
				id = entry.getKey().location();
				break;
			}
		}
		return id;
	}

	public static class Keys {

		public static final ResourceKey<Registry<GolemContainer>> GOLEM_CONTAINERS = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "golem_stats"));

		public static final ResourceKey<Registry<GolemRenderSettings>> GOLEM_MODELS = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "golem_models"));
	}
}
