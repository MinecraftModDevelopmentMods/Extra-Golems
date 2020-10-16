package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public final class WoodenGolem extends GolemMultiTextured {
  
  private static final Map<RegistryKey<Biome>, Integer> BIOME_TO_TEXTURE_MAP = new HashMap<>();
  
  public static final String[] TEXTURE_NAMES = { "oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log" };
  public static final String[] LOOT_TABLE_NAMES = { "oak", "spruce", "birch", "jungle", "acacia", "dark_oak" };

  public WoodenGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, ExtraGolems.MODID, TEXTURE_NAMES, LOOT_TABLE_NAMES);
  }

  @Override
  public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
    // uses the top-middle building block of this golem to set texture.
    // defaults to a random texture.
    final Block b = body.getBlock();
    byte textureNum;
    // check the following block tags for matches
    if (b.isIn(BlockTags.OAK_LOGS)) {
      textureNum = 0;
    } else if (b.isIn(BlockTags.SPRUCE_LOGS)) {
      textureNum = 1;
    } else if (b.isIn(BlockTags.BIRCH_LOGS)) {
      textureNum = 2;
    } else if (b.isIn(BlockTags.JUNGLE_LOGS)) {
      textureNum = 3;
    } else if (b.isIn(BlockTags.ACACIA_LOGS)) {
      textureNum = 4;
    } else if (b.isIn(BlockTags.DARK_OAK_LOGS)) {
      textureNum = 5;
    } else {
      this.randomizeTexture(this.world, this.getPositionUnderneath());
      return;
    }
    // set the texture num based on above
    this.setTextureNum(textureNum);
  }

  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    switch (this.getTextureNum()) {
    case 0:
      return new ItemStack(Blocks.OAK_LOG);
    case 1:
      return new ItemStack(Blocks.SPRUCE_LOG);
    case 2:
      return new ItemStack(Blocks.BIRCH_LOG);
    case 3:
      return new ItemStack(Blocks.JUNGLE_LOG);
    case 4:
      return new ItemStack(Blocks.ACACIA_LOG);
    case 5:
      return new ItemStack(Blocks.DARK_OAK_LOG);
    default:
      return ItemStack.EMPTY;
    }
  }

  @Override
  public void randomizeTexture(final World world, final BlockPos pos) {
    // use the location to select a biome-appropriate texture
    final boolean useBiome = world.getRandom().nextBoolean();
    if (useBiome) {
      final Optional<RegistryKey<Biome>> biome = world.func_242406_i(pos);
      byte texture = (byte)getTextureForBiome(biome);
      setTextureNum(texture);
      return;
    }
    super.randomizeTexture(world, pos);
  }

  @Override
  public Map<Block, Byte> getTextureBytes() {
    return new HashMap<>();
  }
  
  public static int getTextureForBiome(final Optional<RegistryKey<Biome>> biome) {
    if(BIOME_TO_TEXTURE_MAP.isEmpty()) {
      initLogMap();
    }
    return (biome.flatMap(b -> Optional.ofNullable(BIOME_TO_TEXTURE_MAP.get(b))).orElse(0));
  }
  
  private static void initLogMap() {
    BIOME_TO_TEXTURE_MAP.clear();
    // Acacia biomes
    BIOME_TO_TEXTURE_MAP.put(Biomes.SAVANNA, 4);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SAVANNA_PLATEAU, 4);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SHATTERED_SAVANNA, 4);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SHATTERED_SAVANNA_PLATEAU, 4);
    // Birch biomes
    BIOME_TO_TEXTURE_MAP.put(Biomes.BIRCH_FOREST, 2);
    BIOME_TO_TEXTURE_MAP.put(Biomes.BIRCH_FOREST_HILLS, 2);
    BIOME_TO_TEXTURE_MAP.put(Biomes.TALL_BIRCH_FOREST, 2);
    BIOME_TO_TEXTURE_MAP.put(Biomes.TALL_BIRCH_HILLS, 2);
    // Dark Oak biomes
    BIOME_TO_TEXTURE_MAP.put(Biomes.DARK_FOREST, 5);
    BIOME_TO_TEXTURE_MAP.put(Biomes.DARK_FOREST_HILLS, 5);
    // Jungle biomes
    BIOME_TO_TEXTURE_MAP.put(Biomes.JUNGLE, 3);
    BIOME_TO_TEXTURE_MAP.put(Biomes.JUNGLE_EDGE, 3);
    BIOME_TO_TEXTURE_MAP.put(Biomes.JUNGLE_HILLS, 3);
    BIOME_TO_TEXTURE_MAP.put(Biomes.BAMBOO_JUNGLE_HILLS, 3);
    BIOME_TO_TEXTURE_MAP.put(Biomes.BAMBOO_JUNGLE, 3);
    BIOME_TO_TEXTURE_MAP.put(Biomes.MODIFIED_JUNGLE, 3);
    BIOME_TO_TEXTURE_MAP.put(Biomes.MODIFIED_JUNGLE_EDGE, 3);
    // Spruce biomes
    BIOME_TO_TEXTURE_MAP.put(Biomes.TAIGA, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.TAIGA_HILLS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.TAIGA_MOUNTAINS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.GIANT_SPRUCE_TAIGA, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.GIANT_TREE_TAIGA, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.GIANT_TREE_TAIGA_HILLS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.MOUNTAINS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.WOODED_MOUNTAINS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.GRAVELLY_MOUNTAINS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SNOWY_BEACH, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SNOWY_MOUNTAINS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SNOWY_TAIGA, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SNOWY_TAIGA_HILLS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SNOWY_TAIGA_MOUNTAINS, 1);
    BIOME_TO_TEXTURE_MAP.put(Biomes.SNOWY_TUNDRA, 1);
  }
}
