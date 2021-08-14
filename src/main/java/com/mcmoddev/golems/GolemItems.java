package com.mcmoddev.golems;

import com.mcmoddev.golems.container.ContainerDispenserGolem;
import com.mcmoddev.golems.container.ContainerPortableWorkbench;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ExtraGolems.MODID)
public final class GolemItems {

  private GolemItems() {
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

  ////// OTHER //////
  @ObjectHolder("crafting_portable")
  public static final MenuType<ContainerPortableWorkbench> CRAFTING_GOLEM = new MenuType<>(ContainerPortableWorkbench::new);

  @ObjectHolder("dispenser_portable")
  public static final MenuType<ContainerDispenserGolem> DISPENSER_GOLEM = new MenuType<>(ContainerDispenserGolem::new);
}
