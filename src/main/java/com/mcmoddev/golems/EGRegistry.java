package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.item.GolemSpellItem;
import com.mcmoddev.golems.item.GuideBookItem;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.menu.DispenserGolemMenu;
import com.mcmoddev.golems.menu.PortableCraftingMenu;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ExtraGolems.MODID)
public final class EGRegistry {

  private EGRegistry() {
    //
  }

  ////// ITEMS //////
  @ObjectHolder("golem_paper")
  public static final Item GOLEM_SPELL = null;

  @ObjectHolder("spawn_bedrock_golem")
  public static final Item SPAWN_BEDROCK_GOLEM = null;

  @ObjectHolder("info_book")
  public static final Item GOLEM_BOOK = null;

  ////// BLOCKS //////
  @ObjectHolder("golem_head")
  public static final Block GOLEM_HEAD = null;

  @ObjectHolder("light_provider")
  public static final Block UTILITY_LIGHT = null;

  @ObjectHolder("power_provider")
  public static final Block UTILITY_POWER = null;
  
  ////// ENTITIES //////
  @ObjectHolder("golem")
  public static final EntityType<GolemBase> GOLEM = null;

  ////// OTHER //////
  @ObjectHolder("crafting_portable")
  public static final MenuType<PortableCraftingMenu> CRAFTING_GOLEM = new MenuType<>(PortableCraftingMenu::new);

  @ObjectHolder("dispenser_portable")
  public static final MenuType<DispenserGolemMenu> DISPENSER_GOLEM = new MenuType<>(DispenserGolemMenu::new);
  
  // EVENT HANDLERS //
  
  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntities");
    EntityType.Builder<GolemBase> builder = EntityType.Builder.of(GolemBase::new, MobCategory.MISC)
        .setTrackingRange(48).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).sized(1.4F, 2.9F).noSummon();
    EntityType<GolemBase> entityType = builder.build("golem");
    event.getRegistry().register(entityType.setRegistryName(ExtraGolems.MODID, "golem"));
  }
  
  @SubscribeEvent
  public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntityAttributes");
    event.put(EGRegistry.GOLEM, GolemContainer.EMPTY.getAttributeSupplier().get().build());
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerItems");
    event.getRegistry().registerAll(new BlockItem(EGRegistry.GOLEM_HEAD, new Item.Properties().tab(CreativeModeTab.TAB_MISC)) {
      @Override
      @OnlyIn(Dist.CLIENT)
      public boolean isFoil(final ItemStack stack) {
        return true;
      }
    }.setRegistryName(EGRegistry.GOLEM_HEAD.getRegistryName()), new SpawnGolemItem().setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"),
        new GolemSpellItem().setRegistryName(ExtraGolems.MODID, "golem_paper"), new GuideBookItem().setRegistryName(ExtraGolems.MODID, "info_book"));
  }

  @SubscribeEvent
  public static void registerBlocks(final RegistryEvent.Register<Block> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerBlocks");
    event.getRegistry().registerAll(new GolemHeadBlock().setRegistryName(ExtraGolems.MODID, "golem_head"),
        new GlowBlock(Material.GLASS, 1.0F).setRegistryName(ExtraGolems.MODID, "light_provider"),
        new PowerBlock(15).setRegistryName(ExtraGolems.MODID, "power_provider"));
  }

  @SubscribeEvent
  public static void registerContainers(final RegistryEvent.Register<MenuType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerContainers");
    event.getRegistry().register(EGRegistry.CRAFTING_GOLEM.setRegistryName(ExtraGolems.MODID, "crafting_portable"));
    event.getRegistry().register(EGRegistry.DISPENSER_GOLEM.setRegistryName(ExtraGolems.MODID, "dispenser_portable"));
  }
}
