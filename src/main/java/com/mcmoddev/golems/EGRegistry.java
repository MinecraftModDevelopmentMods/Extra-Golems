package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.item.GolemSpellItem;
import com.mcmoddev.golems.item.GuideBookItem;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.menu.PortableCraftingMenu;
import com.mcmoddev.golems.menu.PortableDispenserMenu;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryObject;

public final class EGRegistry {

	private EGRegistry() {
		//
	}

	private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtraGolems.MODID);
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtraGolems.MODID);
	private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ExtraGolems.MODID);
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExtraGolems.MODID);

	public static void init() {
		final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		// deferred registers
		BLOCKS.register(eventBus);
		ITEMS.register(eventBus);
		ENTITY_TYPES.register(eventBus);
		MENU_TYPES.register(eventBus);
		ExtraGolems.GOLEM_CONTAINERS.register(eventBus);
		ExtraGolems.GOLEM_MODELS.register(eventBus);
		// event listeners
		FMLJavaModLoadingContext.get().getModEventBus().addListener(EGRegistry::registerEntityAttributes);
	}

	////// BLOCKS //////
	public static final RegistryObject<Block> GOLEM_HEAD = BLOCKS.register("golem_head",
			() -> new GolemHeadBlock(Block.Properties.copy(Blocks.CARVED_PUMPKIN)));
	public static final RegistryObject<GlowBlock> UTILITY_LIGHT = BLOCKS.register("light_provider",
			() -> new GlowBlock(Material.GLASS, 1.0F));
	public static final RegistryObject<PowerBlock> UTILITY_POWER = BLOCKS.register("power_provider",
			() -> new PowerBlock(15));

	////// ITEM BLOCKS //////
	public static final RegistryObject<Item> GOLEM_HEAD_ITEM = ITEMS.register("golem_head",
			() -> new BlockItem(EGRegistry.GOLEM_HEAD.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)) {
				@Override
				public boolean isFoil(final ItemStack stack) {
					return true;
				}
			});

	////// ITEMS //////
	public static final RegistryObject<GolemSpellItem> GOLEM_SPELL = ITEMS.register("golem_spell", () -> new GolemSpellItem());
	public static final RegistryObject<SpawnGolemItem> SPAWN_BEDROCK_GOLEM = ITEMS.register("spawn_bedrock_golem", () -> new SpawnGolemItem());
	public static final RegistryObject<GuideBookItem> GOLEM_BOOK = ITEMS.register("info_book", () -> new GuideBookItem());


	////// ENTITIES //////
	public static final RegistryObject<? extends EntityType<GolemBase>> GOLEM = ENTITY_TYPES.register("golem", () ->
		EntityType.Builder.of(GolemBase::new, MobCategory.MISC)
				.setTrackingRange(48).setUpdateInterval(3)
				.setShouldReceiveVelocityUpdates(true)
				.sized(1.4F, 2.7F).noSummon()
				.build("golem")
	);

	////// MENU TYPES //////
	public static final RegistryObject<MenuType<AbstractContainerMenu>> CRAFTING_GOLEM_MENU = MENU_TYPES.register("crafting_portable",
			() -> new MenuType<>(PortableCraftingMenu::new));
	public static final RegistryObject<MenuType<PortableDispenserMenu>> DISPENSER_GOLEM_MENU = MENU_TYPES.register("dispenser_portable",
			() -> new MenuType<>(PortableDispenserMenu::new));

	////// EVENTS //////

	public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
		event.put(EGRegistry.GOLEM.get(), GolemContainer.EMPTY.getAttributeSupplier().get().build());
	}
}
