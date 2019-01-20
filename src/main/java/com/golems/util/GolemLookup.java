package com.golems.util;

import com.golems.entity.*;
import com.golems.main.ExtraGolems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class contains methods to convert from building block to the
 * appropriate golem, and also to retrieve said building block when
 * given only the golem's class. It also stores and maps each golem's
 * GolemConfigSet, rather than keeping them all as separate declarations.
 *
 * @author skyjay1
 **/
public class GolemLookup {

    /**
     * Map to determine which Golem is built from the given Block. This is used most.
     **/
    private static final Map<Block, Class<? extends GolemBase>> BLOCK_TO_GOLEM = new HashMap();
    /**
     * Map to determine what Block is preferred to build a Golem. Used for Golem Book.
     **/
    private static final Map<Class<? extends GolemBase>, Block> GOLEM_TO_BLOCK = new HashMap();
    /**
     * Map to retrieve the GolemConfigSet for this golem
     **/
    private static final Map<Class<? extends GolemBase>, GolemConfigSet> GOLEM_TO_CONFIG = new HashMap();

    private static boolean freezeConfig = false;

    // Comparator that sorts Golem Entries by attack power
    private static final Comparator<GolemBase> SORTER = (arg0, arg1) -> {
        float attack0 = arg0.getBaseAttackDamage();
        float attack1 = arg1.getBaseAttackDamage();
        return Float.compare(attack0, attack1);
    };

    /**
     * Be VERY CAREFUL calling this after maps have been populated. You'll break things.
     **/
    public static void clear() {
        BLOCK_TO_GOLEM.clear();
        GOLEM_TO_BLOCK.clear();
        GOLEM_TO_CONFIG.clear();
    }

    /**
     * Adds new GolemConfigSets for each golem in this mod
     **/
    public static void initGolemConfigSets(final Configuration config) {
        // only allow this method to be called ONCE during init
        if (freezeConfig) {
            return;
        }
        addConfig(EntityBedrockGolem.class, new GolemConfigSet(config, "Bedrock Golem", 999.0D, 32.0F));
        addConfig(EntityBoneGolem.class, new GolemConfigSet(config, "Bone Golem", 54.0D, 9.5F));
        addConfig(EntityBookshelfGolem.class, new GolemConfigSet(config, "Bookshelf Golem", 28.0D, 1.5F)
                .addKey(EntityBookshelfGolem.ALLOW_SPECIAL, true, "Whether this golem can give itself potion effects"));
        addConfig(EntityClayGolem.class, new GolemConfigSet(config, "Clay Golem", 20.0D, 2.0F));
        addConfig(EntityCoalGolem.class, new GolemConfigSet(config, "Coal Golem", 14.0D, 2.5F)
                .addKey(EntityCoalGolem.ALLOW_SPECIAL, false, "Whether this golem can inflict blindness"));
        addConfig(EntityCraftingGolem.class, new GolemConfigSet(config, "Crafting Golem", 24.0D, 2.0F)
                .addKey(EntityCraftingGolem.ALLOW_SPECIAL, true, "Whether this golem can open a crafting grid"));
        addConfig(EntityDiamondGolem.class, new GolemConfigSet(config, "Diamond Golem", 220.0D, 20.0F));
        addConfig(EntityEmeraldGolem.class, new GolemConfigSet(config, "Emerald Golem", 190.0D, 18.0F));
        addConfig(EntityEndstoneGolem.class, new GolemConfigSet(config, "Endstone Golem", 50.0D, 8.0F)
                .addKey(EntityEndstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can teleport")
                .addKey(EntityEndstoneGolem.ALLOW_WATER_HURT, true, "Whether the Endstone Golem takes damage from water"));
        addConfig(EntityGlassGolem.class, new GolemConfigSet(config, "Glass Golem", 8.0D, 13.0F));
        addConfig(EntityGlowstoneGolem.class, new GolemConfigSet(config, "Glowstone Golem", 8.0D, 12.0F)
                .addKey(EntityGlowstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can place light sources randomly")
                .addKey(EntityGlowstoneGolem.FREQUENCY, 2, 1, 24000, "Number of ticks between placing light sources"));
        addConfig(EntityGoldGolem.class, new GolemConfigSet(config, "Gold Golem", 80.0D, 8.0F));
        addConfig(EntityHardenedClayGolem.class, new GolemConfigSet(config, "Hardened Clay Golem", 22.0D, 4.0F));
        addConfig(EntityIceGolem.class, new GolemConfigSet(config, "Ice Golem", 18.0D, 6.0F)
                .addKey(EntityIceGolem.ALLOW_SPECIAL, true, "Whether this golem can freeze water and cool lava nearby")
                .addKey(EntityIceGolem.CAN_USE_REGULAR_ICE, true, "When true, the Ice Golem can be built with regular ice as well as packed ice")
                .addKey(EntityIceGolem.AOE, 3, 1, 8, "Radial distance at which this golem can freeze / cool liquids"));
        addConfig(EntityLapisGolem.class, new GolemConfigSet(config, "Lapis Lazuli Golem", 50.0D, 1.5F)
                .addKey(EntityLapisGolem.ALLOW_SPECIAL, true, "Whether this golem can inflict harmful potion effects"));
        addConfig(EntityLeafGolem.class, new GolemConfigSet(config, "Leaf Golem", 6.0D, 0.5F)
                .addKey(EntityLeafGolem.ALLOW_SPECIAL, true, "Whether this golem can heal itself"));
        addConfig(EntityMagmaGolem.class, new GolemConfigSet(config, "Magma Golem", 22.0D, 4.5F)
                .addKey(EntityMagmaGolem.ALLOW_LAVA_SPECIAL, true, "Whether this golem can slowly melt cobblestone")
                .addKey(EntityMagmaGolem.MELT_DELAY, 240, 1, 24000, "Number of ticks it takes to melt cobblestone if enabled (12 sec * 20 t/sec = 240 t)")
                .addKey(EntityMagmaGolem.ALLOW_FIRE_SPECIAL, false, "Whether this golem can light creatures on fire"));
        addConfig(EntityMelonGolem.class, new GolemConfigSet(config, "Melon Golem", 18.0D, 1.5F)
                .addKey(EntityMelonGolem.ALLOW_SPECIAL, true, "Whether this golem can plant flowers randomly")
                .addKey(EntityMelonGolem.FREQUENCY, 240, 1, 24000, "Average number of ticks between planting flowers"));
        addConfig(EntityMushroomGolem.class, new GolemConfigSet(config, "Mushroom Golem", 30.0D, 3.0F)
                .addKey(EntityMushroomGolem.ALLOW_SPECIAL, true, "Whether this golem can plant mushrooms randomly")
                .addKey(EntityMushroomGolem.FREQUENCY, 420, 1, 24000, "Average number of ticks between planting mushrooms"));
        addConfig(EntityNetherBrickGolem.class, new GolemConfigSet(config, "Nether Brick Golem", 25.0D, 6.5F)
                .addKey(EntityNetherBrickGolem.ALLOW_FIRE_SPECIAL, true, "Whether this golem can light creatures on fire"));
        addConfig(EntityNetherWartGolem.class, new GolemConfigSet(config, "Nether Wart Golem", 22.0D, 1.5F)
                .addKey(EntityNetherWartGolem.ALLOW_SPECIAL, true, "Whether this golem can plant netherwart randomly")
                .addKey(EntityNetherWartGolem.FREQUENCY, 880, 1, 24000, "Average number of ticks between planting nether wart if enabled"));
        addConfig(EntityObsidianGolem.class, new GolemConfigSet(config, "Obsidian Golem", 120.0D, 18.0F));
        addConfig(EntityPrismarineGolem.class, new GolemConfigSet(config, "Prismarine Golem", 24.0D, 8.0F));
        addConfig(EntityQuartzGolem.class, new GolemConfigSet(config, "Quartz Golem", 85.0D, 8.5F));
        addConfig(EntityRedSandstoneGolem.class, new GolemConfigSet(config, "Red Sandstone Golem", 15.0D, 4.0F));
        addConfig(EntityRedstoneGolem.class, new GolemConfigSet(config, "Redstone Golem", 18.0D, 2.0F)
                .addKey(EntityRedstoneGolem.ALLOW_SPECIAL, true, "Whether this golem can emit redstone power"));
        addConfig(EntitySandstoneGolem.class, new GolemConfigSet(config, "Sandstone Golem", 15.0D, 4.0F));
        addConfig(EntitySeaLanternGolem.class, new GolemConfigSet(config, "Sea Lantern Golem", 24.0D, 6.0F)
                .addKey(EntitySeaLanternGolem.ALLOW_SPECIAL, true, "Whether this golem can place light sources")
                .addKey(EntitySeaLanternGolem.FREQUENCY, 5, 1, 24000, "Number of ticks between placing light sources"));
        addConfig(EntitySlimeGolem.class, new GolemConfigSet(config, "Slime Golem", 85.0D, 2.5F)
                .addKey(EntitySlimeGolem.ALLOW_SPECIAL, true, "Whether this golem can apply extra knockback when attacking")
                .addKey(EntitySlimeGolem.KNOCKBACK, 2.0012F, 0.001F, 10.0F, "How powerful the Slime Golem knockback is (Higher Value = Further Knockback)"));
        addConfig(EntitySpongeGolem.class, new GolemConfigSet(config, "Sponge Golem", 20.0D, 1.5F)
                .addKey(EntitySpongeGolem.ALLOW_SPECIAL, true, "Whether this golem can absorb water")
                .addKey(EntitySpongeGolem.PARTICLES, true, "Whether this golem should always drip water")
                .addKey(EntitySpongeGolem.RANGE, 4, 2, 8, "Radial distance at which this golem can absorb water (Warning: larger values cause lag)")
                .addKey(EntitySpongeGolem.INTERVAL, 80, 1, 24000, "Number of ticks between each water-check; increase to reduce lag"));
        addConfig(EntityStainedClayGolem.class, new GolemConfigSet(config, "Stained Clay Golem", 26.0D, 3.0F));
        addConfig(EntityStainedGlassGolem.class, new GolemConfigSet(config, "Stained Glass Golem", 9.0D, 12.0F));
        addConfig(EntityStrawGolem.class, new GolemConfigSet(config, "Straw Golem", 10.0D, 1.0F));
        addConfig(EntityTNTGolem.class, new GolemConfigSet(config, "TNT Golem", 14.0D, 2.5F)
                .addKey(EntityTNTGolem.ALLOW_SPECIAL, true, "Whether this golem can explode when fighting or dying"));
        addConfig(EntityWoodenGolem.class, new GolemConfigSet(config, "Wooden Golem", 20.0D, 3.0F));
        addConfig(EntityWoolGolem.class, new GolemConfigSet(config, "Wool Golem", 10.0D, 1.0F));
        freezeConfig = true;
    }

    /**
     * Adds an entry to convert Block to Golem as needed.
     * Supports multiple blocks being used to make a single golem.
     *
     * @return if the mapping was added successfully
     **/
    private static boolean addBlockToGolemMapping(@Nonnull final Block buildingBlock, final Class<? extends GolemBase> golemClazz) {
        // Error check for duplicate keys
        if (BLOCK_TO_GOLEM.containsKey(buildingBlock)) {
            ExtraGolems.LOGGER.warn("Tried to associate Block " + buildingBlock
                    + " with a Golem but Block has already been added! Skipping.");
            return false;
        }
        BLOCK_TO_GOLEM.put(buildingBlock, golemClazz);
        return true;
    }

    /**
     * Adds an entry to the Map to return a specific golem.
     * Only returns one block per golem.
     *
     * @return if the mapping was added successfully
     **/
    private static boolean addGolemToBlockMapping(@Nonnull final Class<? extends GolemBase> golemClazz, final Block buildingBlock) {
        // error check
        if (GOLEM_TO_BLOCK.containsKey(golemClazz)) {
            ExtraGolems.LOGGER.warn("Tried to associate Golem " + golemClazz.getName()
                    + " with a Block but Golem has already been added! Skipping.");
            return false;
        }
        //String clazzOut = (golemClazz != null) ? golemClazz.toString() : "null";
        //String blockOut = buildingBlock != null ? buildingBlock.getRegistryName().toString() : "null";
        GOLEM_TO_BLOCK.put(golemClazz, buildingBlock);
        return true;
    }

    /**
     * Adds a new Golem-Block mapping. Very important!
     *
     * @return if the Golem and Block were successfully added
     **/
    public static boolean addGolem(@Nonnull final Class<? extends GolemBase> golemClazz,
                                   @Nullable final Block buildingBlock) {
        boolean success = buildingBlock == null || addBlockToGolemMapping(buildingBlock, golemClazz);
        success &= addGolemToBlockMapping(golemClazz, buildingBlock);

        return success;
    }

    /**
     * Adds Golem-Block mappings where multiple blocks can be used to make the golem.
     *
     * @param golemClazz     the class to register
     * @param buildingBlocks an array of possible building blocks. Individual elements may be null,
     *                       but the array cannot be null
     * @return if the Golem and Blocks were successfully added
     **/
    public static boolean addGolem(@Nonnull final Class<? extends GolemBase> golemClazz, final Block[] buildingBlocks) {

        boolean success = false;
        if (buildingBlocks.length > 0) {
            // use the first block listed as the default building block
            success = addGolemToBlockMapping(golemClazz, buildingBlocks[0]);
            for (Block b : buildingBlocks) {
                // add all other blocks as possible golem blocks
                success &= b == null || addBlockToGolemMapping(b, golemClazz);
            }
        }

        return success;
    }

    /**
     * Adds a new Golem-to-GolemConfigSet mapping.
     *
     * @return if the Golem and Blocks were successfully added
     **/
    public static boolean addConfig(final Class<? extends GolemBase> golemClazz,
                                    final GolemConfigSet config) {
        // error check
        if (GOLEM_TO_CONFIG.containsKey(golemClazz)) {
            ExtraGolems.LOGGER.warn("Tried to add a Config for " + golemClazz.getName()
                    + " but Golem has already been added! Skipping.");
            return false;
        }

        GOLEM_TO_CONFIG.put(golemClazz, config);
        return true;
    }

    /**
     * Used to get a Golem instance based on the
     *
     * @param world The entity world to spawn in.
     * @param block The block used to build this golem.
     * @return The Golem associated with this block, or null if none is found.
     **/
    @Nullable
    public static GolemBase getGolem(final World world, final Block block) {

        Class<? extends GolemBase> clazz = getGolemClass(block);
        if (clazz != null) {
            // try to make a new instance of the golem
            return (GolemBase) EntityList.newEntity(clazz, world);
        }
        return null;
    }

    /**
     * Used to get a Golem instance based on the
     *
     * @param block The block used to build this golem.
     * @return The Golem associated with this block, or null if none is found.
     **/
    @Nullable
    private static Class<? extends GolemBase> getGolemClass(final Block block) {

        if (block == null) {
            ExtraGolems.LOGGER.error("Can't make a golem with a null block!");
            return null;
        } else if (BLOCK_TO_GOLEM.containsKey(block)) {
            return BLOCK_TO_GOLEM.get(block);
        } else {
            ExtraGolems.LOGGER.error("Tried to make a golem with an unknown block!");
            return null;
        }
    }

    /**
     * Used to retrieve the building block for the given Golem.
     *
     * @param golemClazz The golem
     * @return the Block used to make this golem, or null if there is none.
     **/
    @Nullable
    public static Block getBuildingBlock(final Class<? extends GolemBase> golemClazz) {
        if (golemClazz == null) {
            ExtraGolems.LOGGER.error("Can't get a block from a null golem!");
            return null;
        } else if (GOLEM_TO_BLOCK.containsKey(golemClazz)) {
            return GOLEM_TO_BLOCK.get(golemClazz);
        } else {
            ExtraGolems.LOGGER.error("Tried to get a block for an unknown golem!");
            return null;
        }
    }

    /**
     * @return if this block can be used to build a golem
     **/
    public static boolean isBuildingBlock(final Block block) {
        return block != null && BLOCK_TO_GOLEM.containsKey(block) && BLOCK_TO_GOLEM.get(block) != null;
    }

    /**
     * @return if there are any valid building blocks for the given golem
     **/
    public static boolean hasBuildingBlock(final Class<? extends GolemBase> golemClazz) {
        return golemClazz != null && GOLEM_TO_BLOCK.containsKey(golemClazz) && GOLEM_TO_BLOCK.get(golemClazz) != null;
    }

    /**
     * Used to retrieve the GolemConfigSet for the given Golem.
     *
     * @param golemClazz The golem
     * @return the GolemConfigSet associated with the golem, or null if there is none.
     **/
    @Nullable
    public static GolemConfigSet getConfig(final Class<? extends GolemBase> golemClazz) {
        if (golemClazz == null) {
            ExtraGolems.LOGGER.error("Can't get config file for a null golem!");
            return null;
        } else if (GOLEM_TO_CONFIG.containsKey(golemClazz)) {
            return GOLEM_TO_CONFIG.get(golemClazz);
        } else {
            ExtraGolems.LOGGER.error("Tried to get config file for unknown golem!");
            return null;
        }
    }

    /**
     * @return if the given golem is mapped to a GolemConfigSet
     **/
    public static boolean hasConfig(final Class<? extends GolemBase> golemClazz) {
        return golemClazz != null && GOLEM_TO_CONFIG.containsKey(golemClazz) && GOLEM_TO_CONFIG.get(golemClazz) != null;
    }

    /**
     * @return all valid Blocks to build a golem
     **/
    public static Set<Block> getBlockSet() {
        return BLOCK_TO_GOLEM.keySet();
    }

    /**
     * @return all Golem classes that have been mapped
     **/
    public static Set<Class<? extends GolemBase>> getGolemSet() {
        return GOLEM_TO_BLOCK.keySet();
    }

    /**
     * @return a List containing default instances of each Golem. They do not exist in the world.
     **/
    public static List<GolemBase> getDummyGolemList(final World world) {
        final List<GolemBase> list = new LinkedList<>();
        // for each entity, find out if it's a golem and add it to the list
        //final Set<ResourceLocation> set = EntityList.getEntityNameList();
        for (EntityEntry entry : ForgeRegistries.ENTITIES) {
            if (GolemBase.class.isAssignableFrom(entry.getEntityClass())) {
                list.add((GolemBase) entry.newInstance(world));
            }
        }
        // sort the list
        Collections.sort(list, SORTER);

        return list;
    }
}
