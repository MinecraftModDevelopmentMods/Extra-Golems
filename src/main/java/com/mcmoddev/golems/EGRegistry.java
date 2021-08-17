package com.mcmoddev.golems;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.menu.DispenserGolemMenu;
import com.mcmoddev.golems.menu.PortableCraftingMenu;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
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
  @ObjectHolder("entity")
  public static final EntityType<GolemBase> GOLEM = null;

  ////// OTHER //////
  @ObjectHolder("crafting_portable")
  public static final MenuType<PortableCraftingMenu> CRAFTING_GOLEM = new MenuType<>(PortableCraftingMenu::new);

  @ObjectHolder("dispenser_portable")
  public static final MenuType<DispenserGolemMenu> DISPENSER_GOLEM = new MenuType<>(DispenserGolemMenu::new);
}
