package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.data.behavior.EffectBehavior;
import com.mcmoddev.golems.data.behavior.AoeDryBehavior;
import com.mcmoddev.golems.data.behavior.AoeFreezeBehavior;
import com.mcmoddev.golems.data.behavior.AoeGrowBehavior;
import com.mcmoddev.golems.data.behavior.Behavior;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.behavior.BurnInSunBehavior;
import com.mcmoddev.golems.data.behavior.CraftMenuBehavior;
import com.mcmoddev.golems.data.behavior.LightBehavior;
import com.mcmoddev.golems.data.behavior.ExplodeBehavior;
import com.mcmoddev.golems.data.behavior.FollowBehavior;
import com.mcmoddev.golems.data.behavior.PlaceBlockBehavior;
import com.mcmoddev.golems.data.behavior.PowerBehavior;
import com.mcmoddev.golems.data.behavior.SetFireBehavior;
import com.mcmoddev.golems.data.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.data.behavior.ShootFireballsBehavior;
import com.mcmoddev.golems.data.behavior.ShootShulkerBulletsBehavior;
import com.mcmoddev.golems.data.behavior.ShootSnowballsBehavior;
import com.mcmoddev.golems.data.behavior.SplitBehavior;
import com.mcmoddev.golems.data.behavior.SummonBehavior;
import com.mcmoddev.golems.data.behavior.TeleportBehavior;
import com.mcmoddev.golems.data.behavior.TemptBehavior;
import com.mcmoddev.golems.data.behavior.ItemUpdateGolemBehavior;
import com.mcmoddev.golems.data.behavior.UpdateGolemBehavior;
import com.mcmoddev.golems.data.behavior.UseFuelBehavior;
import com.mcmoddev.golems.data.behavior.WearBannerBehavior;
import com.mcmoddev.golems.data.golem.Attributes;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.LayerList;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.data.modifier.ModifierList;
import com.mcmoddev.golems.data.modifier.golem.AddBehaviorModifier;
import com.mcmoddev.golems.data.modifier.golem.AddBlocksModifier;
import com.mcmoddev.golems.data.modifier.golem.AddDescriptionModifier;
import com.mcmoddev.golems.data.modifier.golem.AddRepairItemsModifier;
import com.mcmoddev.golems.data.modifier.golem.AttributesModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveBehaviorModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveBlocksModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveDescriptionModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveRepairItemsModifier;
import com.mcmoddev.golems.data.modifier.golem.GroupModifier;
import com.mcmoddev.golems.data.modifier.golem.HiddenModifier;
import com.mcmoddev.golems.data.modifier.golem.ParticleModifier;
import com.mcmoddev.golems.data.modifier.golem.VariantsModifier;
import com.mcmoddev.golems.data.modifier.model.AddLayersModifier;
import com.mcmoddev.golems.data.modifier.model.RemoveLayersModifier;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.item.GolemHeadItem;
import com.mcmoddev.golems.item.GolemSpellItem;
import com.mcmoddev.golems.item.GuideBookItem;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.menu.GolemInventoryMenu;
import com.mcmoddev.golems.util.SoundTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public final class EGRegistry {
	private EGRegistry() {
		//
	}

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK,
			ExtraGolems.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM,
			ExtraGolems.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
			.create(BuiltInRegistries.ENTITY_TYPE, ExtraGolems.MODID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU,
			ExtraGolems.MODID);
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
			.create(BuiltInRegistries.CREATIVE_MODE_TAB, ExtraGolems.MODID);

	//// CUSTOM REGISTRIES ////
	public static final DeferredRegister<Golem> GOLEM = DeferredRegister.create(Keys.GOLEM, ExtraGolems.MODID);
	public static final DeferredRegister<LayerList> MODEL = DeferredRegister.create(Keys.MODEL, ExtraGolems.MODID);
	public static final DeferredRegister<MapCodec<? extends Behavior>> BEHAVIOR_SERIALIZER = DeferredRegister
			.create(Keys.BEHAVIOR_SERIALIZER, ExtraGolems.MODID);
	public static final Registry<MapCodec<? extends Behavior>> BEHAVIOR_SERIALIZER_SUPPLIER = BEHAVIOR_SERIALIZER
			.makeRegistry((net.neoforged.neoforge.registries.RegistryBuilder<MapCodec<? extends Behavior>> builder) -> {
			});
	public static final DeferredRegister<Behavior> BEHAVIOR = DeferredRegister.create(Keys.BEHAVIOR, ExtraGolems.MODID);
	public static final DeferredRegister<BehaviorList> BEHAVIOR_LIST = DeferredRegister.create(Keys.BEHAVIOR_LIST,
			ExtraGolems.MODID);

	public static final DeferredRegister<MapCodec<? extends Modifier>> GOLEM_MODIFIER_SERIALIZER = DeferredRegister
			.create(Keys.MODIFIER_SERIALIZER, ExtraGolems.MODID);
	public static final Registry<MapCodec<? extends Modifier>> GOLEM_MODIFIER_SERIALIZER_SUPPLIER = GOLEM_MODIFIER_SERIALIZER
			.makeRegistry((net.neoforged.neoforge.registries.RegistryBuilder<MapCodec<? extends Modifier>> builder) -> {
			});
	public static final DeferredRegister<Modifier> GOLEM_MODIFIER = DeferredRegister.create(Keys.MODIFIER,
			ExtraGolems.MODID);
	public static final DeferredRegister<ModifierList> GOLEM_MODIFIER_LIST = DeferredRegister.create(Keys.MODIFIER_LIST,
			ExtraGolems.MODID);

	public static void register(IEventBus modEventBus) {
		// built in registries
		EntityReg.register(modEventBus);
		BlockReg.register(modEventBus);
		ItemReg.register(modEventBus);
		CreativeTabReg.register(modEventBus);
		MenuReg.register(modEventBus);
		// custom registries
		GolemReg.register(modEventBus);
		ModelReg.register(modEventBus);
		BehaviorReg.register(modEventBus);
		GolemModifierReg.register(modEventBus);
		// non-registry registries
		SoundTypeRegistry.register();

		modEventBus.addListener(EGRegistry::onNewDatapackRegistry);
	}

	private static void onNewDatapackRegistry(final DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(Keys.GOLEM, Golem.CODEC, Golem.CODEC);
		event.dataPackRegistry(Keys.MODEL, LayerList.CODEC, LayerList.CODEC);
		event.dataPackRegistry(Keys.BEHAVIOR_LIST, BehaviorList.CODEC, BehaviorList.CODEC);
		event.dataPackRegistry(Keys.MODIFIER_LIST, ModifierList.CODEC, ModifierList.CODEC);
	}

	public static final class BlockReg {
		private static void register(IEventBus modEventBus) {
			BLOCKS.register(modEventBus);
		}

		public static final DeferredHolder<Block, Block> GOLEM_HEAD = BLOCKS.register("golem_head",
				() -> new GolemHeadBlock(Block.Properties.ofFullCopy(Blocks.CARVED_PUMPKIN)));
		public static final DeferredHolder<Block, GlowBlock> LIGHT_PROVIDER = BLOCKS.register("light_provider",
				() -> new GlowBlock(Blocks.GLASS, 1.0F));
		public static final DeferredHolder<Block, PowerBlock> POWER_PROVIDER = BLOCKS.register("power_provider",
				() -> new PowerBlock(15));
	}

	public static final class ItemReg {
		private static void register(IEventBus modEventBus) {
			ITEMS.register(modEventBus);
		}

		public static final DeferredHolder<Item, GolemSpellItem> GOLEM_SPELL = ITEMS.register("golem_spell",
				() -> new GolemSpellItem(new Item.Properties()));
		public static final DeferredHolder<Item, SpawnGolemItem> SPAWN_BEDROCK_GOLEM = ITEMS
				.register("spawn_bedrock_golem", () -> new SpawnGolemItem(new Item.Properties()));
		public static final DeferredHolder<Item, GuideBookItem> GUIDE_BOOK = ITEMS.register("guide_book",
				() -> new GuideBookItem(new Item.Properties().stacksTo(1)));

		public static final DeferredHolder<Item, Item> GOLEM_HEAD = ITEMS.register("golem_head",
				() -> new GolemHeadItem(BlockReg.GOLEM_HEAD.get(), new Item.Properties()));
	}

	public static final class CreativeTabReg {
		private static void register(IEventBus modEventBus) {
			CREATIVE_MODE_TABS.register(modEventBus);
			modEventBus.addListener(EGRegistry.CreativeTabReg::onBuildTabContents);
		}

		private static void onBuildTabContents(final BuildCreativeModeTabContentsEvent event) {
			if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
				event.accept(ItemReg.GOLEM_SPELL.get());
				event.accept(ItemReg.GUIDE_BOOK.get());
				event.accept(ItemReg.GOLEM_HEAD.get());
			}
			if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
				event.accept(ItemReg.GOLEM_HEAD.get());
			}
			if (event.getTabKey().equals(CreativeModeTabs.NATURAL_BLOCKS)) {
				// insert golem head item after jack o lantern
				event.accept(ItemReg.GOLEM_HEAD.get());
			}
			if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
				event.accept(ItemReg.SPAWN_BEDROCK_GOLEM.get());
			}
		}
	}

	public static final class EntityReg {
		private static void register(IEventBus modEventBus) {
			ENTITY_TYPES.register(modEventBus);
			modEventBus.addListener(EGRegistry.EntityReg::registerEntityAttributes);
		}

		public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
			event.put(GOLEM.get(), GolemBase.golemAttributes().build());
		}

		public static final DeferredHolder<EntityType<?>, EntityType<GolemBase>> GOLEM = ENTITY_TYPES.register("golem",
				() -> EntityType.Builder.of(GolemBase::new, MobCategory.MISC)
						.setTrackingRange(48).setUpdateInterval(3)
						.setShouldReceiveVelocityUpdates(true)
						.sized(1.4F, 2.7F)
						.build("golem"));
	}

	public static final class MenuReg {
		private static void register(IEventBus modEventBus) {
			MENU_TYPES.register(modEventBus);
		}

		public static final DeferredHolder<MenuType<?>, MenuType<GolemInventoryMenu>> GOLEM_INVENTORY = MENU_TYPES
				.register("golem_inventory",
						() -> new MenuType<>(GolemInventoryMenu::new, FeatureFlagSet.of()));
	}

	public static final class GolemReg {
		private static void register(IEventBus modEventBus) {
			GOLEM.register(modEventBus);
		}
	}

	public static final class ModelReg {
		private static void register(IEventBus modEventBus) {
			MODEL.register(modEventBus);
		}
	}

	public static final class BehaviorReg {
		private static void register(IEventBus modEventBus) {
			BEHAVIOR_SERIALIZER.register(modEventBus);
			BEHAVIOR.register(modEventBus);
			BEHAVIOR_LIST.register(modEventBus);
		}

		// SERIALIZERS //
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<EffectBehavior>> EFFECT = BEHAVIOR_SERIALIZER
				.register("effect", () -> EffectBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<AoeDryBehavior>> AOE_DRY = BEHAVIOR_SERIALIZER
				.register("aoe_dry", () -> AoeDryBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<AoeFreezeBehavior>> AOE_FREEZE = BEHAVIOR_SERIALIZER
				.register("aoe_freeze", () -> AoeFreezeBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<AoeGrowBehavior>> AOE_GROW = BEHAVIOR_SERIALIZER
				.register("aoe_grow", () -> AoeGrowBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<WearBannerBehavior>> WEAR_BANNER = BEHAVIOR_SERIALIZER
				.register("wear_banner", () -> WearBannerBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<BurnInSunBehavior>> BURN_IN_SUN = BEHAVIOR_SERIALIZER
				.register("burn_in_sun", () -> BurnInSunBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<CraftMenuBehavior>> CRAFT_MENU = BEHAVIOR_SERIALIZER
				.register("craft_menu", () -> CraftMenuBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<ExplodeBehavior>> EXPLODE = BEHAVIOR_SERIALIZER
				.register("explode", () -> ExplodeBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<FollowBehavior>> FOLLOW = BEHAVIOR_SERIALIZER
				.register("follow", () -> FollowBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<ItemUpdateGolemBehavior>> ITEM_UPDATE_GOLEM = BEHAVIOR_SERIALIZER
				.register("item_update_golem", () -> ItemUpdateGolemBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<LightBehavior>> LIGHT = BEHAVIOR_SERIALIZER
				.register("light", () -> LightBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<PlaceBlockBehavior>> PLACE = BEHAVIOR_SERIALIZER
				.register("place", () -> PlaceBlockBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<PowerBehavior>> POWER = BEHAVIOR_SERIALIZER
				.register("power", () -> PowerBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<ShootArrowsBehavior>> SHOOT_ARROWS = BEHAVIOR_SERIALIZER
				.register("shoot_arrows", () -> ShootArrowsBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<ShootFireballsBehavior>> SHOOT_FIREBALLS = BEHAVIOR_SERIALIZER
				.register("shoot_fireballs", () -> ShootFireballsBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<ShootSnowballsBehavior>> SHOOT_SNOWBALLS = BEHAVIOR_SERIALIZER
				.register("shoot_snowballs", () -> ShootSnowballsBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<ShootShulkerBulletsBehavior>> SHOOT_SHULKER_BULLETS = BEHAVIOR_SERIALIZER
				.register("shoot_shulker_bullets", () -> ShootShulkerBulletsBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<SplitBehavior>> SPLIT = BEHAVIOR_SERIALIZER
				.register("split", () -> SplitBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<SetFireBehavior>> SET_FIRE = BEHAVIOR_SERIALIZER
				.register("set_fire", () -> SetFireBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<SummonBehavior>> SUMMON = BEHAVIOR_SERIALIZER
				.register("summon", () -> SummonBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<TeleportBehavior>> TELEPORT = BEHAVIOR_SERIALIZER
				.register("teleport", () -> TeleportBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<TemptBehavior>> TEMPT = BEHAVIOR_SERIALIZER
				.register("tempt", () -> TemptBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<UpdateGolemBehavior>> UPDATE_GOLEM = BEHAVIOR_SERIALIZER
				.register("update_golem", () -> UpdateGolemBehavior.CODEC);
		public static final DeferredHolder<MapCodec<? extends Behavior>, MapCodec<UseFuelBehavior>> USE_FUEL = BEHAVIOR_SERIALIZER
				.register("use_fuel", () -> UseFuelBehavior.CODEC);

	}

	public static final class GolemModifierReg {
		private static void register(IEventBus modEventBus) {
			GOLEM_MODIFIER_SERIALIZER.register(modEventBus);
			GOLEM_MODIFIER.register(modEventBus);
			GOLEM_MODIFIER_LIST.register(modEventBus);
		}

		// SERIALIZERS //
		// MODEL //
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<AddLayersModifier>> ADD_LAYERS = GOLEM_MODIFIER_SERIALIZER
				.register("add_layers", () -> AddLayersModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<RemoveLayersModifier>> REMOVE_LAYERS = GOLEM_MODIFIER_SERIALIZER
				.register("remove_layers", () -> RemoveLayersModifier.CODEC);
		// GOLEM //
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<AttributesModifier>> ATTRIBUTES = GOLEM_MODIFIER_SERIALIZER
				.register("attributes", () -> AttributesModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<VariantsModifier>> VARIANTS = GOLEM_MODIFIER_SERIALIZER
				.register("variants", () -> VariantsModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<HiddenModifier>> HIDDEN = GOLEM_MODIFIER_SERIALIZER
				.register("hidden", () -> HiddenModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<GroupModifier>> GROUP = GOLEM_MODIFIER_SERIALIZER
				.register("group", () -> GroupModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<AddDescriptionModifier>> ADD_DESCRIPTION = GOLEM_MODIFIER_SERIALIZER
				.register("add_description", () -> AddDescriptionModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<RemoveDescriptionModifier>> REMOVE_DESCRIPTION = GOLEM_MODIFIER_SERIALIZER
				.register("remove_description", () -> RemoveDescriptionModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<ParticleModifier>> PARTICLE = GOLEM_MODIFIER_SERIALIZER
				.register("particle", () -> ParticleModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<AddRepairItemsModifier>> ADD_REPAIR_ITEMS = GOLEM_MODIFIER_SERIALIZER
				.register("add_repair_items", () -> AddRepairItemsModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<RemoveRepairItemsModifier>> REMOVE_REPAIR_ITEMS = GOLEM_MODIFIER_SERIALIZER
				.register("remove_repair_items", () -> RemoveRepairItemsModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<AddBlocksModifier>> ADD_BLOCKS = GOLEM_MODIFIER_SERIALIZER
				.register("add_blocks", () -> AddBlocksModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<RemoveBlocksModifier>> REMOVE_BLOCKS = GOLEM_MODIFIER_SERIALIZER
				.register("remove_blocks", () -> RemoveBlocksModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<AddBehaviorModifier>> ADD_BEHAVIOR = GOLEM_MODIFIER_SERIALIZER
				.register("add_behavior", () -> AddBehaviorModifier.CODEC);
		public static final DeferredHolder<MapCodec<? extends Modifier>, MapCodec<RemoveBehaviorModifier>> REMOVE_BEHAVIOR = GOLEM_MODIFIER_SERIALIZER
				.register("remove_behavior", () -> RemoveBehaviorModifier.CODEC);

	}

	public static final class Keys {
		public static final ResourceKey<Registry<Golem>> GOLEM = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "golem"));
		public static final ResourceKey<Registry<LayerList>> MODEL = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "model"));

		public static final ResourceKey<Registry<MapCodec<? extends Behavior>>> BEHAVIOR_SERIALIZER = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "behavior_serializer"));
		public static final ResourceKey<Registry<Behavior>> BEHAVIOR = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "behavior"));
		public static final ResourceKey<Registry<BehaviorList>> BEHAVIOR_LIST = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "behavior_list"));

		public static final ResourceKey<Registry<MapCodec<? extends Modifier>>> MODIFIER_SERIALIZER = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "modifier_serializer"));
		public static final ResourceKey<Registry<Modifier>> MODIFIER = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "modifier"));
		public static final ResourceKey<Registry<ModifierList>> MODIFIER_LIST = ResourceKey
				.createRegistryKey(ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "golem_modifier"));

	}
}
