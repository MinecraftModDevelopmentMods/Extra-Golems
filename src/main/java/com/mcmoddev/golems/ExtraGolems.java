package com.mcmoddev.golems;

import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.event.EGForgeEvents;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.network.SGolemModelPacket;
import com.mcmoddev.golems.util.GenericJsonReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map.Entry;
import java.util.Optional;

@Mod(ExtraGolems.MODID)
public class ExtraGolems {

	public static final String MODID = "golems";

	public static final Logger LOGGER = LogManager.getFormatterLogger(ExtraGolems.MODID);

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static final GenericJsonReloadListener<GolemContainer> GOLEM_CONTAINERS = new GenericJsonReloadListener<>("golem_stats", GolemContainer.class, GolemContainer.CODEC,
			l -> l.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), c)))));

	public static final GenericJsonReloadListener<GolemRenderSettings> GOLEM_RENDER_SETTINGS = new GenericJsonReloadListener<>("golem_models", GolemRenderSettings.class, GolemRenderSettings.CODEC,
			l -> l.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemModelPacket(e.getKey(), c)))));

	public ExtraGolems() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// init registry
		EGRegistry.init();
		// init helper classes
		GolemBehaviors.init();
		// register event handlers
		this.registerCommonEvents();
		DistExecutor.runForDist(() -> () -> this.registerClientEvents(), () -> () -> this.registerServerEvents());
		// set up config file
		EGConfig.setupConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EGConfig.COMMON_CONFIG);
		// register messages
		ExtraGolems.LOGGER.debug(ExtraGolems.MODID + ":registerNetwork");
		int messageId = 0;
		CHANNEL.registerMessage(messageId++, SGolemContainerPacket.class, SGolemContainerPacket::toBytes, SGolemContainerPacket::fromBytes, SGolemContainerPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		CHANNEL.registerMessage(messageId++, SGolemModelPacket.class, SGolemModelPacket::toBytes, SGolemModelPacket::fromBytes, SGolemModelPacket::handlePacket, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

	}

	private void setup(final FMLCommonSetupEvent event) {
		// init addons
		AddonLoader.init();
	}

	private void enqueueIMC(final InterModEnqueueEvent event) {
		// register TheOneProbe integration
		if (ModList.get().isLoaded("theoneprobe")) {
			ExtraGolems.LOGGER.debug("Extra Golems detected TheOneProbe, registering plugin now");
			InterModComms.sendTo(MODID, "theoneprobe", "getTheOneProbe", () -> new com.mcmoddev.golems.integration.TOPDescriptionManager.GetTheOneProbe());
		}
	}

	private int registerCommonEvents() {
		ExtraGolems.LOGGER.debug(ExtraGolems.MODID + ":registerEventHandlers");
		MinecraftForge.EVENT_BUS.register(EGForgeEvents.class);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(AddonLoader::onAddPackFinders);
		return 0;
	}

	private int registerServerEvents() {
		return 1;
	}

	private int registerClientEvents() {
		MinecraftForge.EVENT_BUS.register(com.mcmoddev.golems.event.EGClientEvents.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(com.mcmoddev.golems.event.EGClientModEvents.class);
		com.mcmoddev.golems.event.EGClientEvents.addResources();
		return 2;
	}

	/**
	 * Checks all registered GolemContainers until one is found that is constructed
	 * out of the passed Blocks. Parameters are the current World and the 4 blocks
	 * that will be used to calculate this Golem. It is okay to pass {@code null} or
	 * Air.
	 *
	 * @return the constructed GolemBase instance if there is one for the passed blocks, otherwise null
	 * @see GolemContainer#matches(Block, Block, Block, Block)
	 **/
	@Nullable
	public static GolemBase getGolem(Level world, Block below1, Block below2, Block arm1, Block arm2) {
		ResourceLocation id = null;
		for (Entry<ResourceLocation, Optional<GolemContainer>> entry : GOLEM_CONTAINERS.getEntries()) {
			if (entry.getValue().isPresent() && entry.getValue().get().matches(below1, below2, arm1, arm2)) {
				id = entry.getKey();
				break;
			}
		}
		if (id == null) {
			return null;
		}
		GolemBase entity = GolemBase.create(world, id);
		return entity;
	}
}
