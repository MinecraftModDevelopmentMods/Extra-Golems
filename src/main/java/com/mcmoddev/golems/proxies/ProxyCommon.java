package com.mcmoddev.golems.proxies;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.blocks.BlockUtilityPower;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.items.ItemGolemSpell;
import com.mcmoddev.golems.items.ItemInfoBook;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class ProxyCommon {

  public void registerListeners() { }

  public void registerEntityRenders() { }

  public void registerContainerRenders() { }

  public void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
    // Register EntityTypes by iterating through each registered GolemContainer
    // Also register their attributes suppliers
    GolemRegistrar.getContainers().forEach(container -> {
      event.getRegistry().register(container.getEntityType());
      GlobalEntityTypeAttributes.put(container.getEntityType(), container.getAttributeSupplier().get().create());
    });
  }

  public void registerItems(final RegistryEvent.Register<Item> event) {
    event.getRegistry().registerAll(new BlockItem(GolemItems.GOLEM_HEAD, new Item.Properties().group(ItemGroup.MISC)) {
      @Override
      @OnlyIn(Dist.CLIENT)
      public boolean hasEffect(final ItemStack stack) {
        return true;
      }
    }.setRegistryName(GolemItems.GOLEM_HEAD.getRegistryName()), new ItemBedrockGolem().setRegistryName(ExtraGolems.MODID, "spawn_bedrock_golem"),
        new ItemGolemSpell().setRegistryName(ExtraGolems.MODID, "golem_paper"), new ItemInfoBook().setRegistryName(ExtraGolems.MODID, "info_book"));
  }

  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    event.getRegistry().registerAll(new BlockGolemHead().setRegistryName(ExtraGolems.MODID, "golem_head"),
        new BlockUtilityGlow(Material.GLASS, 1.0F).setRegistryName(ExtraGolems.MODID, "light_provider"),
        new BlockUtilityPower(15).setRegistryName(ExtraGolems.MODID, "power_provider"));
  }

  public void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
    event.getRegistry().register(GolemItems.CRAFTING_GOLEM.setRegistryName(ExtraGolems.MODID, "crafting_portable"));
    event.getRegistry().register(GolemItems.DISPENSER_GOLEM.setRegistryName(ExtraGolems.MODID, "dispenser_portable"));
  }
}
