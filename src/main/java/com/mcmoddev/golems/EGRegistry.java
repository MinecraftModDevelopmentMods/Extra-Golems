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
import com.mcmoddev.golems.data.behavior.SplitBehavior;
import com.mcmoddev.golems.data.behavior.SummonEntityBehavior;
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
import com.mcmoddev.golems.data.modifier.golem.AddRepairItemsModifier;
import com.mcmoddev.golems.data.modifier.golem.AttributesModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveBehaviorModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveBlocksModifier;
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
import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mcmoddev.golems.util.SoundTypeRegistry;
import com.mojang.serialization.Codec;
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
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class EGRegistry {
	private EGRegistry() {
		//
	}

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtraGolems.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtraGolems.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExtraGolems.MODID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExtraGolems.MODID);
	private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ExtraGolems.MODID);

	//// CUSTOM REGISTRIES ////
	public static final DeferredRegister<Golem> GOLEM = DeferredRegister.create(Keys.GOLEM, ExtraGolems.MODID);
	public static final DeferredRegister<LayerList> MODEL = DeferredRegister.create(Keys.MODEL, ExtraGolems.MODID);
	public static final DeferredRegister<Codec<? extends Behavior>> BEHAVIOR_SERIALIZER = DeferredRegister.create(Keys.BEHAVIOR_SERIALIZER, ExtraGolems.MODID);
	public static final Supplier<IForgeRegistry<Codec<? extends Behavior>>> BEHAVIOR_SERIALIZER_SUPPLIER = BEHAVIOR_SERIALIZER.makeRegistry(() -> new RegistryBuilder<>());
	public static final DeferredRegister<Behavior> BEHAVIOR = DeferredRegister.create(Keys.BEHAVIOR, ExtraGolems.MODID);
	public static final DeferredRegister<BehaviorList> BEHAVIOR_LIST = DeferredRegister.create(Keys.BEHAVIOR_LIST, ExtraGolems.MODID);

	public static final DeferredRegister<Codec<? extends Modifier>> GOLEM_MODIFIER_SERIALIZER = DeferredRegister.create(Keys.MODIFIER_SERIALIZER, ExtraGolems.MODID);
	public static final Supplier<IForgeRegistry<Codec<? extends Modifier>>> GOLEM_MODIFIER_SERIALIZER_SUPPLIER = GOLEM_MODIFIER_SERIALIZER.makeRegistry(() -> new RegistryBuilder<>());
	public static final DeferredRegister<Modifier> GOLEM_MODIFIER = DeferredRegister.create(Keys.MODIFIER, ExtraGolems.MODID);
	public static final DeferredRegister<ModifierList> GOLEM_MODIFIER_LIST = DeferredRegister.create(Keys.MODIFIER_LIST, ExtraGolems.MODID);

	public static void register() {
		// built in registries
		EntityReg.register();
		BlockReg.register();
		ItemReg.register();
		CreativeTabReg.register();
		MenuReg.register();
		// custom registries
		GolemReg.register();
		ModelReg.register();
		BehaviorReg.register();
		GolemModifierReg.register();
		// non-registry registries
		SoundTypeRegistry.register();
		EntityDataSerializersReg.register();

		FMLJavaModLoadingContext.get().getModEventBus().addListener(EGRegistry::onNewDatapackRegistry);
	}

	private static void onNewDatapackRegistry(final DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(Keys.GOLEM, Golem.CODEC, Golem.CODEC);
		event.dataPackRegistry(Keys.MODEL, LayerList.CODEC, LayerList.CODEC);
		event.dataPackRegistry(Keys.BEHAVIOR, Behavior.DIRECT_CODEC, Behavior.DIRECT_CODEC);
		event.dataPackRegistry(Keys.BEHAVIOR_LIST, BehaviorList.CODEC, BehaviorList.CODEC);
		event.dataPackRegistry(Keys.MODIFIER_LIST, ModifierList.CODEC, ModifierList.CODEC);
	}

	public static final class BlockReg {
		private static void register() {
			BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		public static final RegistryObject<Block> GOLEM_HEAD = BLOCKS.register("golem_head",
				() -> new GolemHeadBlock(Block.Properties.copy(Blocks.CARVED_PUMPKIN)));
		public static final RegistryObject<GlowBlock> LIGHT_PROVIDER = BLOCKS.register("light_provider",
				() -> new GlowBlock(Blocks.GLASS, 1.0F));
		public static final RegistryObject<PowerBlock> POWER_PROVIDER = BLOCKS.register("power_provider",
				() -> new PowerBlock(15));
	}

	public static final class ItemReg {
		private static void register() {
			ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		public static final RegistryObject<GolemSpellItem> GOLEM_SPELL = ITEMS.register("golem_spell", () -> new GolemSpellItem(new Item.Properties()));
		public static final RegistryObject<SpawnGolemItem> SPAWN_BEDROCK_GOLEM = ITEMS.register("spawn_bedrock_golem", () -> new SpawnGolemItem(new Item.Properties()));
		public static final RegistryObject<GuideBookItem> GUIDE_BOOK = ITEMS.register("guide_book", () -> new GuideBookItem(new Item.Properties().stacksTo(1)));

		public static final RegistryObject<Item> GOLEM_HEAD = ITEMS.register("golem_head", () -> new GolemHeadItem(BlockReg.GOLEM_HEAD.get(), new Item.Properties()));
	}

	public static final class CreativeTabReg {
		private static void register() {
			CREATIVE_MODE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
			FMLJavaModLoadingContext.get().getModEventBus().addListener(EGRegistry.CreativeTabReg::onBuildTabContents);
		}

		private static void onBuildTabContents(final BuildCreativeModeTabContentsEvent event) {
			if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
				event.accept(ItemReg.GOLEM_SPELL);
				event.accept(ItemReg.GUIDE_BOOK);
				event.accept(ItemReg.GOLEM_HEAD);
			}
			if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
				event.accept(ItemReg.GOLEM_HEAD);
			}
			if(event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
				// insert golem head item after jack o lantern
				event.getEntries().putAfter(Items.JACK_O_LANTERN.getDefaultInstance(), ItemReg.GOLEM_HEAD.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			}
		}
	}

	public static final class EntityReg {
		private static void register() {
			ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
			FMLJavaModLoadingContext.get().getModEventBus().addListener(EGRegistry.EntityReg::registerEntityAttributes);
		}

		public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
			event.put(GOLEM.get(), Attributes.EMPTY.getAttributeSupplier().get().build());
		}

		public static final RegistryObject<? extends EntityType<GolemBase>> GOLEM = ENTITY_TYPES.register("golem", () ->
				EntityType.Builder.of(GolemBase::new, MobCategory.MISC)
						.setTrackingRange(48).setUpdateInterval(3)
						.setShouldReceiveVelocityUpdates(true)
						.sized(1.4F, 2.7F).noSummon()
						.build("golem")
		);
	}

	public static final class MenuReg {
		private static void register() {
			MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		public static final RegistryObject<MenuType<PortableDispenserMenu>> DISPENSER_GOLEM_MENU = MENU_TYPES.register("dispenser_portable",
				() -> new MenuType<>(PortableDispenserMenu::new, FeatureFlagSet.of()));
	}

	public static final class GolemReg {
		private static void register() {
			GOLEM.register(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}

	public static final class ModelReg {
		private static void register() {
			MODEL.register(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}

	public static final class BehaviorReg {
		private static void register() {
			BEHAVIOR_SERIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
			BEHAVIOR.register(FMLJavaModLoadingContext.get().getModEventBus());
			BEHAVIOR_LIST.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		// SERIALIZERS //
		public static final RegistryObject<Codec<EffectBehavior>> EFFECT = BEHAVIOR_SERIALIZER.register("effect", () -> EffectBehavior.CODEC);
		public static final RegistryObject<Codec<AoeDryBehavior>> AOE_DRY = BEHAVIOR_SERIALIZER.register("aoe_dry", () -> AoeDryBehavior.CODEC);
		public static final RegistryObject<Codec<AoeFreezeBehavior>> AOE_FREEZE = BEHAVIOR_SERIALIZER.register("aoe_freeze", () -> AoeFreezeBehavior.CODEC);
		public static final RegistryObject<Codec<AoeGrowBehavior>> AOE_GROW = BEHAVIOR_SERIALIZER.register("aoe_grow", () -> AoeGrowBehavior.CODEC);
		public static final RegistryObject<Codec<WearBannerBehavior>> WEAR_BANNER = BEHAVIOR_SERIALIZER.register("wear_banner", () -> WearBannerBehavior.CODEC);
		public static final RegistryObject<Codec<BurnInSunBehavior>> BURN_IN_SUN = BEHAVIOR_SERIALIZER.register("burn_in_sun", () -> BurnInSunBehavior.CODEC);
		public static final RegistryObject<Codec<CraftMenuBehavior>> CRAFT_MENU = BEHAVIOR_SERIALIZER.register("craft_menu", () -> CraftMenuBehavior.CODEC);
		public static final RegistryObject<Codec<ExplodeBehavior>> EXPLODE = BEHAVIOR_SERIALIZER.register("explode", () -> ExplodeBehavior.CODEC);
		public static final RegistryObject<Codec<FollowBehavior>> FOLLOW = BEHAVIOR_SERIALIZER.register("follow", () -> FollowBehavior.CODEC);
		public static final RegistryObject<Codec<ItemUpdateGolemBehavior>> ITEM_UPDATE_GOLEM = BEHAVIOR_SERIALIZER.register("item_update_golem", () -> ItemUpdateGolemBehavior.CODEC);
		public static final RegistryObject<Codec<LightBehavior>> LIGHT = BEHAVIOR_SERIALIZER.register("light", () -> LightBehavior.CODEC);
		public static final RegistryObject<Codec<PlaceBlockBehavior>> PLACE = BEHAVIOR_SERIALIZER.register("place", () -> PlaceBlockBehavior.CODEC);
		public static final RegistryObject<Codec<PowerBehavior>> POWER = BEHAVIOR_SERIALIZER.register("power", () -> PowerBehavior.CODEC);
		public static final RegistryObject<Codec<ShootArrowsBehavior>> SHOOT_ARROWS = BEHAVIOR_SERIALIZER.register("shoot_arrows", () -> ShootArrowsBehavior.CODEC);
		public static final RegistryObject<Codec<SplitBehavior>> SPLIT = BEHAVIOR_SERIALIZER.register("split", () -> SplitBehavior.CODEC);
		public static final RegistryObject<Codec<SetFireBehavior>> SET_FIRE = BEHAVIOR_SERIALIZER.register("set_fire", () -> SetFireBehavior.CODEC);
		public static final RegistryObject<Codec<SummonEntityBehavior>> SUMMON = BEHAVIOR_SERIALIZER.register("summon", () -> SummonEntityBehavior.CODEC);
		public static final RegistryObject<Codec<TeleportBehavior>> TELEPORT = BEHAVIOR_SERIALIZER.register("teleport", () -> TeleportBehavior.CODEC);
		public static final RegistryObject<Codec<TemptBehavior>> TEMPT = BEHAVIOR_SERIALIZER.register("tempt", () -> TemptBehavior.CODEC);
		public static final RegistryObject<Codec<UpdateGolemBehavior>> UPDATE_GOLEM = BEHAVIOR_SERIALIZER.register("update_golem", () -> UpdateGolemBehavior.CODEC);
		public static final RegistryObject<Codec<UseFuelBehavior>> USE_FUEL = BEHAVIOR_SERIALIZER.register("use_fuel", () -> UseFuelBehavior.CODEC);

	}

	public static final class GolemModifierReg {
		private static void register() {
			GOLEM_MODIFIER_SERIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
			GOLEM_MODIFIER.register(FMLJavaModLoadingContext.get().getModEventBus());
			GOLEM_MODIFIER_LIST.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		// SERIALIZERS //
		// MODEL //
		public static final RegistryObject<Codec<AddLayersModifier>> ADD_LAYERS = GOLEM_MODIFIER_SERIALIZER.register("add_layers", () -> AddLayersModifier.CODEC);
		public static final RegistryObject<Codec<RemoveLayersModifier>> REMOVE_LAYERS = GOLEM_MODIFIER_SERIALIZER.register("remove_layers", () -> RemoveLayersModifier.CODEC);
		// GOLEM //
		public static final RegistryObject<Codec<AttributesModifier>> ATTRIBUTES = GOLEM_MODIFIER_SERIALIZER.register("attributes", () -> AttributesModifier.CODEC);
		public static final RegistryObject<Codec<VariantsModifier>> VARIANTS = GOLEM_MODIFIER_SERIALIZER.register("variants", () -> VariantsModifier.CODEC);
		public static final RegistryObject<Codec<HiddenModifier>> HIDDEN = GOLEM_MODIFIER_SERIALIZER.register("hidden", () -> HiddenModifier.CODEC);
		public static final RegistryObject<Codec<GroupModifier>> GROUP = GOLEM_MODIFIER_SERIALIZER.register("group", () -> GroupModifier.CODEC);
		public static final RegistryObject<Codec<ParticleModifier>> PARTICLE = GOLEM_MODIFIER_SERIALIZER.register("particle", () -> ParticleModifier.CODEC);
		public static final RegistryObject<Codec<AddRepairItemsModifier>> ADD_REPAIR_ITEMS = GOLEM_MODIFIER_SERIALIZER.register("add_repair_items", () -> AddRepairItemsModifier.CODEC);
		public static final RegistryObject<Codec<RemoveRepairItemsModifier>> REMOVE_REPAIR_ITEMS = GOLEM_MODIFIER_SERIALIZER.register("remove_repair_items", () -> RemoveRepairItemsModifier.CODEC);
		public static final RegistryObject<Codec<AddBlocksModifier>> ADD_BLOCKS = GOLEM_MODIFIER_SERIALIZER.register("add_blocks", () -> AddBlocksModifier.CODEC);
		public static final RegistryObject<Codec<RemoveBlocksModifier>> REMOVE_BLOCKS = GOLEM_MODIFIER_SERIALIZER.register("remove_blocks", () -> RemoveBlocksModifier.CODEC);
		public static final RegistryObject<Codec<AddBehaviorModifier>> ADD_BEHAVIOR = GOLEM_MODIFIER_SERIALIZER.register("add_behavior", () -> AddBehaviorModifier.CODEC);
		public static final RegistryObject<Codec<RemoveBehaviorModifier>> REMOVE_BEHAVIOR = GOLEM_MODIFIER_SERIALIZER.register("remove_behavior", () -> RemoveBehaviorModifier.CODEC);

	}

	public static final class EntityDataSerializersReg {
		private static void register() {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(EntityDataSerializersReg::onCommonSetup);
		}

		private static void onCommonSetup(final FMLCommonSetupEvent event) {
			event.enqueueWork(() -> EntityDataSerializers.registerSerializer(IExtraGolem.OPTIONAL_RESOURCE_LOCATION));
		}
	}


	public static final class Keys {
		public static final ResourceKey<Registry<Golem>> GOLEM = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golem"));
		public static final ResourceKey<Registry<LayerList>> MODEL = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "model"));

		public static final ResourceKey<Registry<Codec<? extends Behavior>>> BEHAVIOR_SERIALIZER = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "behavior_serializer"));
		public static final ResourceKey<Registry<Behavior>> BEHAVIOR = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "behavior"));
		public static final ResourceKey<Registry<BehaviorList>> BEHAVIOR_LIST = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "behavior_list"));

		public static final ResourceKey<Registry<Codec<? extends Modifier>>> MODIFIER_SERIALIZER = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "modifier_serializer"));
		public static final ResourceKey<Registry<Modifier>> MODIFIER = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "modifier"));
		public static final ResourceKey<Registry<ModifierList>> MODIFIER_LIST = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golem_modifier"));


	}
}
