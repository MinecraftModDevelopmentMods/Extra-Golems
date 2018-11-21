package com.golems.proxies;

import com.golems.blocks.BlockGolemHead;
import com.golems.entity.*;
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
		CommonProxy.golemEntityCount = 0;
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
//		register(EntityRedstoneGolem.class, "golem_redstone");
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
						.setRegistryName(ExtraGolems.MODID, "golem_head"));
	}

}
