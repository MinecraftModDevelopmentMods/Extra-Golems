package com.mcmoddev.golems.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.main.Config;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IRegistryDelegate;

/**
 * This class contains methods to convert from building block to the
 * appropriate golem, and also to retrieve said building block when
 * given only the golem's class. It also stores and maps each golem's
 * GolemConfigSet, rather than keeping them all as separate declarations.
 * @author skyjay1
 **/
public final class GolemLookup {

	/**
	 * Map to retrieve the Golem that is built from the given Block. This is used most.
	 **/
	private static final Map<IRegistryDelegate<Block>, Class<? extends GolemBase>> BLOCK_TO_GOLEM = new HashMap<>();
	/** Map to retrieve the preferred Block this Golem uses. Used for Golem Book. **/
	private static final Map<Class<? extends GolemBase>, IRegistryDelegate<Block>> GOLEM_TO_BLOCK = new HashMap<>();
	/** Map to retrieve the GolemConfigSet for this golem **/
	private static final Map<Class<? extends GolemBase>, GolemConfigSet> GOLEM_TO_CONFIG = new HashMap<>();
	/**  Map to retrive EntityType from Golem's unique Class **/
	private static final Map<Class<? extends GolemBase>, EntityType<?>> GOLEM_ENTITY_TYPES = new HashMap<>();
		
	private GolemLookup() {
		//
	}
	
	/**
	 * Used to associate an EntityType to associate with this Golem.
	 * @return The EntityType that was added, if successful
	 **/
	public static boolean addEntityType(@Nonnull final Class<? extends GolemBase> golemClazz, 
			@Nonnull final EntityType<?> type) {
		if(GOLEM_ENTITY_TYPES.containsKey(golemClazz)) {
			ExtraGolems.LOGGER.warn("Tried to associate Class " + golemClazz.getName()
					+ " with an EntityType but Class has already been added! Skipping.");
				return false;
		}
		GOLEM_ENTITY_TYPES.put(golemClazz, type);
		return true;
	}
	
	public static boolean hasEntityType(final Class<? extends GolemBase> golemClazz) {
		return golemClazz != null && GOLEM_ENTITY_TYPES.containsKey(golemClazz) && GOLEM_ENTITY_TYPES.get(golemClazz) != null;
	}
	
	@Nullable
	public static EntityType<?> getEntityType(final Class<? extends GolemBase> golemClazz) {
		if (golemClazz == null) {
			ExtraGolems.LOGGER.error("Can't get an EntityType from a null golem!");
			return null;
		} else if (GOLEM_ENTITY_TYPES.containsKey(golemClazz)) {
			return GOLEM_ENTITY_TYPES.get(golemClazz);
		} else {
			ExtraGolems.LOGGER.error("Tried to get an EntityType for an unknown golem: " + golemClazz.getName());
			return null;
		}
	}

	/**
	 * Used to register an additional block that can make this golem.
	 * If a golem class was already mapped with its building block,
	 * then this method can be used to associate additional Blocks
	 * with that same golem.
	 * <p>For example, if a mod adds an equivalent form of Leaves, you
	 * could call {@code addBlockAlias([Modded Leaves Block], EntityLeafGolem.class}
	 * and the modded leaves will be seen as valid golem building blocks
	 * for the Leaf Golem.
	 * @param buildingBlock The additional block to build this golem
	 * @param golemClazz The golem that should be built with this block
	 * @return if the mapping was added successfully
	 **/
	public static boolean addBlockAlias(@Nonnull final Block buildingBlock, final Class<? extends GolemBase> golemClazz) {
		return addBlockToGolemMapping(buildingBlock, golemClazz);
	}

	/**
	 *  Adds an entry to convert Block to Golem as needed. 
	 *  Supports multiple blocks being used to make a single golem.
	 *  @return if the mapping was added successfully
	 **/
	private static boolean addBlockToGolemMapping(@Nonnull final Block buildingBlock,
						      @Nonnull final Class<? extends GolemBase> golemClazz) {
		// Error check for duplicate keys
		if (BLOCK_TO_GOLEM.containsKey(buildingBlock.delegate)) {
			ExtraGolems.LOGGER.warn("Tried to associate Block " + buildingBlock
				+ " with a Golem but Block has already been added! Skipping.");
			return false;					
		}
		BLOCK_TO_GOLEM.put(buildingBlock.delegate, golemClazz);
		return true;
	}

	/**
	 * Adds an entry to the Map to return a specific block based on the golem. 
	 * Only returns one block per golem.
	 * @return if the mapping was added successfully
	 **/
	private static boolean addGolemToBlockMapping(@Nonnull final Class<? extends GolemBase> golemClazz,
						      @Nullable final Block buildingBlock) {
		// error check
		if (GOLEM_TO_BLOCK.containsKey(golemClazz)) {
			ExtraGolems.LOGGER.warn("Tried to associate Golem " + golemClazz.getName()
				+ " with a Block but Golem has already been added! Skipping.");
			return false;					
		}
		GOLEM_TO_BLOCK.put(golemClazz, buildingBlock != null ? buildingBlock.delegate : null);
		return true;
	}

	/**
	 * Adds a new Golem-Block mapping where the given block is used to make
	 * the given Golem. Multiple Blocks can be used to make the same golem - for that, 
	 * use {@link #addGolem(Class, Block[])}
	 * <b>This must be called for every Golem at some point during preInit, init, or postInit.</b>
	 * @param golemClazz the class to register
	 * @param buildingBlock a block to associate with this golem. Can be null if you
	 * don't want this golem to be built. Will also be used as the golem's
	 * "Creative Return"
	 * @return if the Golem and Blocks were successfully added
	 **/
	public static boolean addGolem(@Nonnull final Class<? extends GolemBase> golemClazz,
				       @Nullable final Block buildingBlock) {
		boolean success = buildingBlock != null && addBlockToGolemMapping(buildingBlock, golemClazz);
		success &= addGolemToBlockMapping(golemClazz, buildingBlock);

		return success;
	}
	
	/** 
	 * Adds Golem-Block mappings where multiple blocks can be used to make the golem.
	 * <b>This or {@link #addGolem(Class, Block)} must be called for every Golem at some
	 * point during preInit, init, or postInit.</b>
	 * @param golemClazz the class to register
	 * @param buildingBlocks an array of possible building blocks. Individual elements may be null,
	 * but the array cannot be null. The first element will be used as the golem's "preferred" block.
	 * @return if the Golem and Blocks were successfully added
	 **/
	public static boolean addGolem(@Nonnull final Class<? extends GolemBase> golemClazz, @Nonnull final Block[] buildingBlocks) {
		
		boolean success = false;
		if (buildingBlocks.length > 0) {
			// use the first block listed as the default building block
			success = addGolemToBlockMapping(golemClazz, buildingBlocks[0]);
			for (final Block b : buildingBlocks) {
				// add all other blocks as possible golem blocks
				success &= b != null && addBlockToGolemMapping(b, golemClazz);
			}
		}

		return success;
	}

	/**
	 * Adds a new Golem-to-GolemConfigSet mapping. 
	 * @return if the Golem and GolemConfigSet were successfully added
	 **/
	public static boolean addConfig(final Class<? extends GolemBase> golemClazz,
					final GolemConfigSet config) {
		// error check
		if (GOLEM_TO_CONFIG.containsKey(golemClazz)) {
			ExtraGolems.LOGGER.warn("Tried to add a Config for " + golemClazz.getName()
				+ " but Golem already has one! Skipping.");
			return false;					
		}
		
		GOLEM_TO_CONFIG.put(golemClazz, config);
		return true;
	}

	/**
	 * Used to get a Golem instance based on the block given
	 * @param world The entity world to spawn in.
	 * @param block The block used to build this golem.
	 * @return The Golem associated with this block, or null if none is found.
	 **/
	@Nullable
	public static GolemBase getGolem(final World world, final Block block) {
		
		Class<? extends GolemBase> clazz = getGolemClass(block);
		if(clazz != null && GOLEM_ENTITY_TYPES.containsKey(clazz)) {
			// try to make a new instance of the golem
			return (GolemBase) GOLEM_ENTITY_TYPES.get(clazz).create(world);
		}
		return null;
	}
	
	/**
	 * Used to get a Golem instance based on the Class given
	 * @param world The entity world to spawn in.
	 * @param clazz The type of Golem to spawn
	 * @return The Golem instance, or null if none is found.
	 **/
	@Nullable
	public static GolemBase getGolem(final World world, final Class<? extends GolemBase> clazz) {
		
		if(clazz != null && GOLEM_ENTITY_TYPES.containsKey(clazz)) {
			// try to make a new instance of the golem
			return (GolemBase) GOLEM_ENTITY_TYPES.get(clazz).create(world);
		}
		return null;
	}

	/**
	 * Used to get a Golem instance based on the given block
	 * @param block The block used to build a golem
	 * @return The Golem associated with this block, or null if none is found.
	 **/
	@Nullable
	private static Class<? extends GolemBase> getGolemClass(final Block block) {

		if(block == null) {
			ExtraGolems.LOGGER.error("Can't make a golem with a null block!");
			return null;
		} else if (BLOCK_TO_GOLEM.containsKey(block.delegate)) {
			return BLOCK_TO_GOLEM.get(block.delegate);
		} else {
			// The block itself is not registered, try matching the OreDict name instead
			if(Config.getUseOreDictBlocks()) {
				/*
				for(Block b : getOreDictMatches(block)) {
					if(b != null && BLOCK_TO_GOLEM.containsKey(b.delegate)) {
						return BLOCK_TO_GOLEM.get(b.delegate);
					}
				}
				*/
			} else {
				ExtraGolems.LOGGER.error("Tried to make a golem with an unknown block: " + block.getRegistryName());
			}
		}	
		return null;
	}
	
	/**
	 * Used to retrieve the building block for the given Golem.
	 * @param golemClazz The golem
	 * @return the Block used to make this golem, or null if there is none.
	 **/
	@Nullable
	public static Block getBuildingBlock(final Class<? extends GolemBase> golemClazz) {
		if (golemClazz == null) {
			ExtraGolems.LOGGER.error("Can't get a block from a null golem!");
			return null;
		} else if (GOLEM_TO_BLOCK.containsKey(golemClazz)) {
			return GOLEM_TO_BLOCK.get(golemClazz) != null ? GOLEM_TO_BLOCK.get(golemClazz).get() : null;
		} else {
			ExtraGolems.LOGGER.error("Tried to get a block for an unknown golem: " + golemClazz.getName());
			return null;
		}
	}

	/** @return if this block can be used to build a golem **/
	public static boolean isBuildingBlock(final Block block) {
		if(block != null && BLOCK_TO_GOLEM.containsKey(block.delegate) && BLOCK_TO_GOLEM.get(block.delegate) != null) {
			return true;
		} else if(Config.getUseOreDictBlocks()){			
			// search the OreDictionary for golem blocks under the given block's OreDict name
			final Collection<Block> matches = getMatches(block);
			for(Block b : matches) {
				if(b != null && BLOCK_TO_GOLEM.containsKey(b.delegate) && BLOCK_TO_GOLEM.get(b.delegate) != null) {
					return true;
				}
			}
		}
		return false;
	}

	/** @return if there are any valid building blocks for the given golem **/
	public static boolean hasBuildingBlock(final Class<? extends GolemBase> golemClazz) {
		return golemClazz != null && GOLEM_TO_BLOCK.containsKey(golemClazz) && GOLEM_TO_BLOCK.get(golemClazz) != null;
	}
	
	/**
	 * Used to retrieve the GolemConfigSet for the given Golem.
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

	/** @return if the given golem is mapped to a GolemConfigSet **/
	public static boolean hasConfig(final Class<? extends GolemBase> golemClazz) {
		return golemClazz != null && GOLEM_TO_CONFIG.containsKey(golemClazz) && GOLEM_TO_CONFIG.get(golemClazz) != null;
	}

	/** @return all valid Blocks to build a golem **/
	public static Set<IRegistryDelegate<Block>> getBlockSet() {
		return BLOCK_TO_GOLEM.keySet();
	}

	/** @return all Golem classes that have been mapped **/
	public static Set<Class<? extends GolemBase>> getGolemSet() {
		return GOLEM_TO_BLOCK.keySet();
	}
	
	/**  @return all EntityEntry objects **/
	public static List<EntityType<?>> getEntityTypes() {
		Set<Class<? extends GolemBase>> keys = GOLEM_ENTITY_TYPES.keySet();
		List<EntityType<?>> values = new ArrayList<>();
		for(Class<? extends GolemBase> clazz : keys) {
			values.add(GOLEM_ENTITY_TYPES.get(clazz));
		}
		return values;
	}

	/**
	 * @return a List containing default instances of each Golem, sorted by attack power. 
	 * They do not exist in the world. 
	 **/
	public static List<GolemBase> getDummyGolemList(final World world) {
		final List<GolemBase> list = new LinkedList<>();
		// for each entity, find out if it's a golem and add it to the list
		for (EntityType<?> entry : net.minecraftforge.registries.ForgeRegistries.ENTITIES) {
			if (GolemBase.class.isAssignableFrom(entry.getEntityClass())) {
				list.add((GolemBase) entry.create(world));
			}
		}		
		return list;
	}
	
	/**
	 * Searches the OreDictionary for entries registered under the same
	 * name as the passed block. Used in case the given block is considered
	 * the same as a block we've already registered and should therefore
	 * build the same golem.
	 * @param original a block whose OreDict entries we should search for.
	 * Passing null will result in an empty array.
	 * @return an array of all valid golem blocks with this OreDict name,
	 * or an empty array if none are found.
	 **/
	@Nonnull
	private static Collection<Block> getMatches(@Nullable final Block original) {
		if(original == null) {
			return Collections.emptySet();
		}
		Collection<ResourceLocation> registeredTags = BlockTags.getCollection().getRegisteredTags();
		for(ResourceLocation rl : registeredTags) {
			Tag<Block> tag = BlockTags.getCollection().get(rl);
			if(tag != null && tag.contains(original)) {
				return tag.getAllElements();
			}
		}
		/*
		final int[] ids = OreDictionary.getOreIDs(new ItemStack(original));
		if(ids != null && ids.length > 0) {
			for(final int id : ids) {
				final String oreName = OreDictionary.getOreName(id);
				if(!"Unknown".equals(oreName)) {
					// the name is valid, now look up the blocks for that name
					final List<ItemStack> matches = OreDictionary.getOres(oreName);
					final List<Block> blocks = new ArrayList(matches.size());
					for(ItemStack stack : matches) {
						if(stack != null && stack.getItem() instanceof ItemBlock) {
							final Block b = ((ItemBlock)stack.getItem()).getBlock();
							// check if the OreDict-supplied block builds a golem
							if(b != null && BLOCK_TO_GOLEM.containsKey(b) && BLOCK_TO_GOLEM.get(b) != null) {
								blocks.add(b);
							}
						}
					}
					// return all golem-building blocks that are registered in OreDict under this name
					return !blocks.isEmpty() ? blocks.toArray(new Block[blocks.size()]) : new Block[] {};
				}
			}
		}
		*/
		return Collections.emptySet();
	}
	/*
	public static boolean matchesOreDict(Block block, String toCheck) {
		if (OreDictionary.doesOreNameExist(toCheck)) {
			ItemStack passedBlock = new ItemStack(block);
			List<ItemStack> matches = OreDictionary.getOres(toCheck);
			return !matches.isEmpty() && OreDictionary.itemMatches(passedBlock, matches.get(0), true);
		} else {
			return false;
		}
	}
	*/
}