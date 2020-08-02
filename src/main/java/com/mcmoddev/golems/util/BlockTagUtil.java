package com.mcmoddev.golems.util;

import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagRegistry.NamedTag;
import net.minecraft.util.ResourceLocation;

public class BlockTagUtil {

  public static ITag.INamedTag<Block> TAG_CONCRETE;
  public static ITag.INamedTag<Block> TAG_SANDSTONE;
  public static ITag.INamedTag<Block> TAG_RED_SANDSTONE;
  public static ITag.INamedTag<Block> TAG_PRISMARINE;
  public static ITag.INamedTag<Block> TAG_TERRACOTTA;
  public static ITag.INamedTag<Block> TAG_QUARTZ;
  public static ITag.INamedTag<Block> TAG_DEAD_CORAL_BLOCKS;

  public static void loadTags() {
    TAG_CONCRETE = getTag(new ResourceLocation(ExtraGolems.MODID, "concrete"));
    TAG_SANDSTONE = getTag(new ResourceLocation(ExtraGolems.MODID, "sandstone"));
    TAG_RED_SANDSTONE = getTag(new ResourceLocation(ExtraGolems.MODID, "red_sandstone"));
    TAG_PRISMARINE = getTag(new ResourceLocation(ExtraGolems.MODID, "prismarine"));
    TAG_TERRACOTTA = getTag(new ResourceLocation(ExtraGolems.MODID, "colored_terracotta"));
    TAG_QUARTZ = getTag(new ResourceLocation(ExtraGolems.MODID, "quartz"));
    TAG_DEAD_CORAL_BLOCKS = getTag(new ResourceLocation(ExtraGolems.MODID, "dead_coral_block"));
  }

  public static ITag.INamedTag<Block> getTag(ResourceLocation path) {
    return (INamedTag<Block>) BlockTags.getCollection().getOrCreate(path);
  }
}
