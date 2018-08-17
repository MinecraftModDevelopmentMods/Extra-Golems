package com.golems.proxies;

import com.golems.blocks.BlockGolemHead;
import com.golems.blocks.BlockLightProvider;
import com.golems.blocks.BlockPowerProvider;
import com.golems.blocks.TileEntityMovingLightSource;
import com.golems.blocks.TileEntityMovingPowerSource;
import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.EntityBoneGolem;
import com.golems.entity.EntityBookshelfGolem;
import com.golems.entity.EntityClayGolem;
import com.golems.entity.EntityCoalGolem;
import com.golems.entity.EntityCraftingGolem;
import com.golems.entity.EntityDiamondGolem;
import com.golems.entity.EntityEmeraldGolem;
import com.golems.entity.EntityEndstoneGolem;
import com.golems.entity.EntityGlassGolem;
import com.golems.entity.EntityGlowstoneGolem;
import com.golems.entity.EntityGoldGolem;
import com.golems.entity.EntityHardenedClayGolem;
import com.golems.entity.EntityIceGolem;
import com.golems.entity.EntityLapisGolem;
import com.golems.entity.EntityLeafGolem;
import com.golems.entity.EntityMagmaGolem;
import com.golems.entity.EntityMelonGolem;
import com.golems.entity.EntityMushroomGolem;
import com.golems.entity.EntityNetherBrickGolem;
import com.golems.entity.EntityNetherWartGolem;
import com.golems.entity.EntityObsidianGolem;
import com.golems.entity.EntityPrismarineGolem;
import com.golems.entity.EntityQuartzGolem;
import com.golems.entity.EntityRedSandstoneGolem;
import com.golems.entity.EntityRedstoneGolem;
import com.golems.entity.EntitySandstoneGolem;
import com.golems.entity.EntitySeaLanternGolem;
import com.golems.entity.EntitySlimeGolem;
import com.golems.entity.EntitySpongeGolem;
import com.golems.entity.EntityStainedClayGolem;
import com.golems.entity.EntityStainedGlassGolem;
import com.golems.entity.EntityStrawGolem;
import com.golems.entity.EntityTNTGolem;
import com.golems.entity.EntityWoodenGolem;
import com.golems.entity.EntityWoolGolem;
import com.golems.entity.GolemBase;
import com.golems.events.handlers.GolemCommonEventHandler;
import com.golems.items.ItemBedrockGolem;
import com.golems.items.ItemGolemPaper;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class CommonProxy {

	// private static Map<String, Block> blocks = new ConcurrentHashMap<>();
	// private static Map<String, Item> items = new ConcurrentHashMap<>();

	protected static int golemEntityCount;

	public void preInitRenders() {
		// Unused
	}

	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new GolemCommonEventHandler());
	}

	public void registerEntities() {
		golemEntityCount = 0;
		register(EntityBedrockGolem.class, "golem_bedrock");
		register(EntityBoneGolem.class, "golem_bone");
		register(EntityBookshelfGolem.class, "golem_bookshelf");
		register(EntityClayGolem.class, "golem_clay");
		register(EntityCoalGolem.class, "golem_coal");
		register(EntityCraftingGolem.class, "golem_crafting");
		register(EntityDiamondGolem.class, "golem_diamond");
		register(EntityEmeraldGolem.class, "golem_emerald");
		register(EntityEndstoneGolem.class, "golem_end_stone");
		register(EntityGlassGolem.class, "golem_glass");
		register(EntityGlowstoneGolem.class, "golem_glowstone");
		register(EntityGoldGolem.class, "golem_gold");
		register(EntityHardenedClayGolem.class, "golem_hardened_clay");
		register(EntityIceGolem.class, "golem_ice");
		register(EntityLapisGolem.class, "golem_lapis");
		register(EntityLeafGolem.class, "golem_leaves");
		register(EntityMagmaGolem.class, "golem_magma");
		register(EntityMelonGolem.class, "golem_melon");
		register(EntityMushroomGolem.class, "golem_shroom");
		register(EntityNetherBrickGolem.class, "golem_nether_brick");
		register(EntityNetherWartGolem.class, "golem_nether_wart");
		register(EntityObsidianGolem.class, "golem_obsidian");
		register(EntityPrismarineGolem.class, "golem_prismarine");
		register(EntityQuartzGolem.class, "golem_quartz");
		register(EntityRedSandstoneGolem.class, "golem_red_sandstone");
		register(EntityRedstoneGolem.class, "golem_redstone");
		register(EntitySandstoneGolem.class, "golem_sandstone");
		register(EntitySeaLanternGolem.class, "golem_sea_lantern");
		register(EntitySlimeGolem.class, "golem_slime");
		register(EntitySpongeGolem.class, "golem_sponge");
		register(EntityStainedClayGolem.class, "golem_stained_clay");
		register(EntityStainedGlassGolem.class, "golem_stained_glass");
		register(EntityStrawGolem.class, "golem_straw");
		register(EntityTNTGolem.class, "golem_tnt");
		register(EntityWoodenGolem.class, "golem_wooden");
		register(EntityWoolGolem.class, "golem_wool");
	}

	/** registers the entity. **/
	protected static void register(final Class<? extends GolemBase> entityClass, final String name) {

		EntityRegistry.registerModEntity(
				new ResourceLocation(ExtraGolems.MODID + ":textures/entity/" + name + ".png"),
				entityClass, ExtraGolems.MODID + "." + name, ++golemEntityCount,
				ExtraGolems.instance, 16 * 4, 3, true);
	}

	// TODO: Reimplement old version
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new ItemBlock(GolemItems.golemHead) {

			@Override
			@SideOnly(Side.CLIENT)
			public boolean hasEffect(final ItemStack stack) {
				return Config.itemGolemHeadHasGlint;
			}
		}.setRegistryName(GolemItems.golemHead.getRegistryName()));

		event.getRegistry()
				.register(new ItemBedrockGolem().setTranslationKey("spawn_bedrock_golem")
						.setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"));

		event.getRegistry().register(new ItemGolemPaper().setTranslationKey("golem_paper")
				.setRegistryName(ExtraGolems.MODID, "golem_paper"));
	}

	// TODO: Reimplement old version
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		// TODO: Clean up
		event.getRegistry().registerAll(
				new BlockGolemHead().setTranslationKey("golem_head")
						.setRegistryName(ExtraGolems.MODID, "golem_head"),
				new BlockLightProvider().setTranslationKey("light_provider_full")
						.setRegistryName(ExtraGolems.MODID, "light_provider_full"),
				new BlockPowerProvider().setTranslationKey("power_provider_all")
						.setRegistryName(ExtraGolems.MODID, "power_provider_all"));
		TileEntity.register(ExtraGolems.MODID + "_TileEntityMovingLightSource",
				TileEntityMovingLightSource.class);
		TileEntity.register(ExtraGolems.MODID + "_TileEntityMovingPowerSource",
				TileEntityMovingPowerSource.class);
	}

}
