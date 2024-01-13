package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.render.GolemRenderSettings;
import com.mcmoddev.golems.data.behavior.Behavior;
import com.mcmoddev.golems.data.behavior.BehaviorList;
import com.mcmoddev.golems.data.behavior.CraftMenuBehavior;
import com.mcmoddev.golems.data.behavior.FollowBehavior;
import com.mcmoddev.golems.data.behavior.SplitBehavior;
import com.mcmoddev.golems.data.behavior.TeleportBehavior;
import com.mcmoddev.golems.data.behavior.TemptBehavior;
import com.mcmoddev.golems.data.behavior.TickUpdateGolemBehavior;
import com.mcmoddev.golems.data.behavior.ItemUpdateGolemBehavior;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Model;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.data.modifier.GolemModifierList;
import com.mcmoddev.golems.data.modifier.golem.AddBlocksGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.AddRepairItemsGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.AttributesGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveBlocksGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.RemoveRepairItemsGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.GroupGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.HiddenGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.ParticleGolemModifier;
import com.mcmoddev.golems.data.modifier.golem.VariantsGolemModifier;
import com.mcmoddev.golems.data.modifier.model.AddLayersGolemModifier;
import com.mcmoddev.golems.data.modifier.model.RemoveLayersGolemModifier;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.item.GolemHeadItem;
import com.mcmoddev.golems.item.GolemSpellItem;
import com.mcmoddev.golems.item.GuideBookItem;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
	public static final DeferredRegister<Golem> GOLEMS = DeferredRegister.create(Keys.GOLEMS, ExtraGolems.MODID);
	public static final DeferredRegister<Model> MODELS = DeferredRegister.create(Keys.MODELS, ExtraGolems.MODID);
	public static final DeferredRegister<Codec<? extends Behavior>> BEHAVIOR_SERIALIZERS = DeferredRegister.create(Keys.BEHAVIOR_SERIALIZERS, ExtraGolems.MODID);
	public static final Supplier<IForgeRegistry<Codec<? extends Behavior>>> BEHAVIOR_SERIALIZERS_SUPPLIER = BEHAVIOR_SERIALIZERS.makeRegistry(() -> new RegistryBuilder<>());
	public static final DeferredRegister<Behavior> BEHAVIORS = DeferredRegister.create(Keys.BEHAVIORS, ExtraGolems.MODID);
	public static final Supplier<IForgeRegistry<Behavior>> BEHAVIORS_SUPPLIER = BEHAVIORS.makeRegistry(() -> new RegistryBuilder<>());
	public static final DeferredRegister<BehaviorList> BEHAVIOR_LISTS = DeferredRegister.create(Keys.BEHAVIOR_LISTS, ExtraGolems.MODID);

	public static final DeferredRegister<Codec<? extends GolemModifier>> GOLEM_MODIFIER_SERIALIZERS = DeferredRegister.create(Keys.GOLEM_MODIFIER_SERIALIZERS, ExtraGolems.MODID);
	public static final Supplier<IForgeRegistry<Codec<? extends GolemModifier>>> GOLEM_MODIFIER_SERIALIZERS_SUPPLIER = GOLEM_MODIFIER_SERIALIZERS.makeRegistry(() -> new RegistryBuilder<>());
	public static final DeferredRegister<GolemModifier> GOLEM_MODIFIERS = DeferredRegister.create(Keys.GOLEM_MODIFIERS, ExtraGolems.MODID);
	public static final Supplier<IForgeRegistry<GolemModifier>> GOLEM_MODIFIERS_SUPPLIER = GOLEM_MODIFIERS.makeRegistry(() -> new RegistryBuilder<>());
	public static final DeferredRegister<GolemModifierList> GOLEM_MODIFIER_LISTS = DeferredRegister.create(Keys.GOLEM_MODIFIER_LISTS, ExtraGolems.MODID);

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

		FMLJavaModLoadingContext.get().getModEventBus().addListener(EGRegistry::onNewDatapackRegistry);
	}

	private static void onNewDatapackRegistry(final DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(ExtraGolems.Keys.GOLEM_CONTAINERS, GolemContainer.CODEC, GolemContainer.CODEC);
		event.dataPackRegistry(ExtraGolems.Keys.GOLEM_MODELS, GolemRenderSettings.CODEC, GolemRenderSettings.CODEC);
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
			event.put(GOLEM.get(), GolemContainer.EMPTY.getAttributeSupplier().get().build());
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
			GOLEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}

	public static final class ModelReg {
		private static void register() {
			MODELS.register(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}

	public static final class BehaviorReg {
		private static void register() {
			BEHAVIOR_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
			BEHAVIORS.register(FMLJavaModLoadingContext.get().getModEventBus());
			BEHAVIOR_LISTS.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		// SERIALIZERS //
		public static final RegistryObject<Codec<CraftMenuBehavior>> CRAFT_MENU = BEHAVIOR_SERIALIZERS.register("craft_menu", () -> CraftMenuBehavior.CODEC);
		public static final RegistryObject<Codec<FollowBehavior>> FOLLOW = BEHAVIOR_SERIALIZERS.register("follow", () -> FollowBehavior.CODEC);
		public static final RegistryObject<Codec<ItemUpdateGolemBehavior>> ITEM_UPDATE_GOLEM = BEHAVIOR_SERIALIZERS.register("item_update_golem", () -> ItemUpdateGolemBehavior.CODEC);
		public static final RegistryObject<Codec<SplitBehavior>> SPLIT = BEHAVIOR_SERIALIZERS.register("split", () -> SplitBehavior.CODEC);
		public static final RegistryObject<Codec<TeleportBehavior>> TELEPORT = BEHAVIOR_SERIALIZERS.register("teleport", () -> TeleportBehavior.CODEC);
		public static final RegistryObject<Codec<TemptBehavior>> TEMPT = BEHAVIOR_SERIALIZERS.register("tempt", () -> TemptBehavior.CODEC);
		public static final RegistryObject<Codec<TickUpdateGolemBehavior>> TICK_UPDATE_GOLEM = BEHAVIOR_SERIALIZERS.register("tick_update_golem", () -> TickUpdateGolemBehavior.CODEC);

	}

	public static final class GolemModifierReg {
		private static void register() {
			GOLEM_MODIFIER_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
			GOLEM_MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
			GOLEM_MODIFIER_LISTS.register(FMLJavaModLoadingContext.get().getModEventBus());
		}

		// SERIALIZERS //
		// MODEL //
		public static final RegistryObject<Codec<AddLayersGolemModifier>> ADD_LAYERS = GOLEM_MODIFIER_SERIALIZERS.register("add_layers", () -> AddLayersGolemModifier.CODEC);
		public static final RegistryObject<Codec<RemoveLayersGolemModifier>> REMOVE_LAYERS = GOLEM_MODIFIER_SERIALIZERS.register("remove_layers", () -> RemoveLayersGolemModifier.CODEC);
		// GOLEM //
		public static final RegistryObject<Codec<AttributesGolemModifier>> ATTRIBUTES = GOLEM_MODIFIER_SERIALIZERS.register("attributes", () -> AttributesGolemModifier.CODEC);
		public static final RegistryObject<Codec<VariantsGolemModifier>> VARIANTS = GOLEM_MODIFIER_SERIALIZERS.register("variants", () -> VariantsGolemModifier.CODEC);
		public static final RegistryObject<Codec<HiddenGolemModifier>> HIDDEN = GOLEM_MODIFIER_SERIALIZERS.register("hidden", () -> HiddenGolemModifier.CODEC);
		public static final RegistryObject<Codec<GroupGolemModifier>> GROUP = GOLEM_MODIFIER_SERIALIZERS.register("group", () -> GroupGolemModifier.CODEC);
		public static final RegistryObject<Codec<ParticleGolemModifier>> PARTICLE = GOLEM_MODIFIER_SERIALIZERS.register("particle", () -> ParticleGolemModifier.CODEC);
		public static final RegistryObject<Codec<AddRepairItemsGolemModifier>> ADD_REPAIR_ITEMS = GOLEM_MODIFIER_SERIALIZERS.register("add_repair_items", () -> AddRepairItemsGolemModifier.CODEC);
		public static final RegistryObject<Codec<RemoveRepairItemsGolemModifier>> REMOVE_REPAIR_ITEMS = GOLEM_MODIFIER_SERIALIZERS.register("remove_repair_items", () -> RemoveRepairItemsGolemModifier.CODEC);
		public static final RegistryObject<Codec<AddBlocksGolemModifier>> ADD_BLOCKS = GOLEM_MODIFIER_SERIALIZERS.register("add_blocks", () -> AddBlocksGolemModifier.CODEC);
		public static final RegistryObject<Codec<RemoveBlocksGolemModifier>> REMOVE_BLOCKS = GOLEM_MODIFIER_SERIALIZERS.register("remove_blocks", () -> RemoveBlocksGolemModifier.CODEC);

	}


	public static final class Keys {
		public static final ResourceKey<Registry<Golem>> GOLEMS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golems"));
		public static final ResourceKey<Registry<Model>> MODELS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "models"));

		public static final ResourceKey<Registry<Codec<? extends Behavior>>> BEHAVIOR_SERIALIZERS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golem_behavior_serializers"));
		public static final ResourceKey<Registry<Behavior>> BEHAVIORS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golem_behaviors"));
		public static final ResourceKey<Registry<BehaviorList>> BEHAVIOR_LISTS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "behaviors"));

		public static final ResourceKey<Registry<Codec<? extends GolemModifier>>> GOLEM_MODIFIER_SERIALIZERS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golem_modifier_serializers"));
		public static final ResourceKey<Registry<GolemModifier>> GOLEM_MODIFIERS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "golem_modifiers"));
		public static final ResourceKey<Registry<GolemModifierList>> GOLEM_MODIFIER_LISTS = ResourceKey.createRegistryKey(new ResourceLocation(ExtraGolems.MODID, "modifiers"));


	}
}
