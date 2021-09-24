package com.mcmoddev.golems;

import com.mcmoddev.golems.block.GlowBlock;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.block.PowerBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.item.GolemSpellItem;
import com.mcmoddev.golems.item.GuideBookItem;
import com.mcmoddev.golems.item.SpawnGolemItem;
import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mcmoddev.golems.menu.PortableCraftingMenu;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
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
  public static final EntityType<GolemBase> GOLEM = EntityType.Builder.create(GolemBase::new, EntityClassification.MISC).setTrackingRange(48).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).size(1.4F, 2.9F).disableSummoning().build("golem");

  ////// OTHER //////
  @ObjectHolder("crafting_portable")
  public static final ContainerType<PortableCraftingMenu> CRAFTING_GOLEM = new ContainerType<>(PortableCraftingMenu::new);

  @ObjectHolder("dispenser_portable")
  public static final ContainerType<PortableDispenserMenu> DISPENSER_GOLEM = new ContainerType<>(PortableDispenserMenu::new);
  
  // EVENT HANDLERS //
  
  @SubscribeEvent
  public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntities");
    event.getRegistry().register(GOLEM.setRegistryName(ExtraGolems.MODID, "golem"));
  }
  
  @SubscribeEvent
  public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerEntityAttributes");
    event.put(EGRegistry.GOLEM, GolemContainer.EMPTY.getAttributeSupplier().get().create());
  }

  @SubscribeEvent
  public static void registerItems(final RegistryEvent.Register<Item> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerItems");
    event.getRegistry().registerAll(new BlockItem(EGRegistry.GOLEM_HEAD, new Item.Properties().group(ItemGroup.MISC)) {
      @Override
      @OnlyIn(Dist.CLIENT)
      public boolean hasEffect(final ItemStack stack) {
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
  public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    ExtraGolems.LOGGER.info(ExtraGolems.MODID + ":registerContainers");
    event.getRegistry().register(EGRegistry.CRAFTING_GOLEM.setRegistryName(ExtraGolems.MODID, "crafting_portable"));
    event.getRegistry().register(EGRegistry.DISPENSER_GOLEM.setRegistryName(ExtraGolems.MODID, "dispenser_portable"));
  }
}
