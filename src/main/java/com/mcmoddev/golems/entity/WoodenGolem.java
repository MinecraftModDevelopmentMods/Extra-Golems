package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public final class WoodenGolem extends GolemMultiTextured {
  
  private static final Map<ResourceKey<Biome>, Integer> BIOME_TO_TEXTURE_MAP = new HashMap<>();
  
  public static final String[] TEXTURE_NAMES = { "oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log", "dark_oak_log" };
  public static final String[] LOOT_TABLE_NAMES = { "oak", "spruce", "birch", "jungle", "acacia", "dark_oak" };

  public WoodenGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world, "minecraft", TEXTURE_NAMES, ExtraGolems.MODID, LOOT_TABLE_NAMES);
  }

  @Override
  public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
    // uses the top-middle building block of this golem to set texture.
    // defaults to a random texture.
    final Block b = body.getBlock();
    byte textureNum;
    // check the following block tags for matches
    if (b.is(BlockTags.OAK_LOGS)) {
      textureNum = 0;
    } else if (b.is(BlockTags.SPRUCE_LOGS)) {
      textureNum = 1;
    } else if (b.is(BlockTags.BIRCH_LOGS)) {
      textureNum = 2;
    } else if (b.is(BlockTags.JUNGLE_LOGS)) {
      textureNum = 3;
    } else if (b.is(BlockTags.ACACIA_LOGS)) {
      textureNum = 4;
    } else if (b.is(BlockTags.DARK_OAK_LOGS)) {
      textureNum = 5;
    } else {
      this.randomizeTexture(this.level, this.getBlockPosBelowThatAffectsMyMovement());
      return;
    }
    // set the texture num based on above
    this.setTextureNum(textureNum);
  }

  @Override
  public ItemStack getCreativeReturn(final HitResult target) {
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
  public void randomizeTexture(final Level world, final BlockPos pos) {
    // use the location to select a biome-appropriate texture
    final boolean useBiome = world.getRandom().nextBoolean();
    if (useBiome) {
      final Optional<ResourceKey<Biome>> biome = world.getBiomeName(pos);
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
  
  public static int getTextureForBiome(final Optional<ResourceKey<Biome>> biome) {
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
