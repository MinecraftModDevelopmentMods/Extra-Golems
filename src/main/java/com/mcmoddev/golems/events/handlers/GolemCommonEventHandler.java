package com.mcmoddev.golems.events.handlers;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.*;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles events added specifically from this mod.
 **/
public class GolemCommonEventHandler {
	
	/*
	// TODO find out when we can handle this
	@SubscribeEvent
	public void onPopulateChunk(PopulateChunkEvent.Post event) {
		////// Spawn some basic golems in villages //////
		// percent chance that each chunk will contain a golem
		final int GOLEM_CHANCE = Config.getVillageGolemSpawnChance();
		final int DIMID = event.getWorld().provider.getDimension();
		if(DIMID == 0 && GOLEM_CHANCE > 0 && event.isHasVillageGenerated() 
				&& event.getRand().nextInt(100) < GOLEM_CHANCE) {
			// Make sure this is near a village and try to spawn a Golem
			BlockPos pos = new BlockPos(event.getChunkX() * 16, 100, event.getChunkZ() * 16);
			Village village = event.getWorld().villageCollection.getNearestVillage(pos, 32);
			if(village != null) {
				// spawn a golem based on the village biome
				Class<? extends GolemBase> golemClazz = getGolemForBiome(event.getWorld().getBiome(pos).getCategory(), event.getRand());
				GolemBase golemInstance = golemClazz != null ? GolemLookup.getGolem(event.getWorld(), golemClazz) : null;
				if(golemInstance != null) {	
					BlockPos spawn = getSafeSpawnPos(golemInstance, pos.add(8, 0, 8));
					if(spawn != null) {
						// spawn the golem
						golemInstance.setPosition(spawn.getX(), spawn.getY(), spawn.getZ());
						golemInstance.setPlayerCreated(false);
						event.getWorld().spawnEntity(golemInstance);
						// randomize texture if applicable
						if(golemInstance instanceof GolemMultiTextured) {
							byte texture = (byte)event.getRand().nextInt(((GolemMultiTextured)golemInstance).getNumTextures());
							((GolemMultiTextured)golemInstance).setTextureNum(texture);
						} else if(golemInstance instanceof GolemColorizedMultiTextured) {
							byte texture = (byte)event.getRand().nextInt(((GolemColorizedMultiTextured)golemInstance).getColorArray().length);
							((GolemColorizedMultiTextured)golemInstance).setTextureNum(texture);
						}
					}
				}
			}
		}
	}
	*/
//	private static BlockPos getSafeSpawnPos(final EntityLivingBase entity, final BlockPos near) {
//		final int radius = 6;
//		final int maxTries = 24;
//		BlockPos testing;
//		for(int i = 0; i < maxTries; i++) {
//			// get a random position near the passed BlockPos
//			int x = near.getX() + entity.getEntityWorld().rand.nextInt(radius * 2) - radius;
//			int z = near.getZ() + entity.getEntityWorld().rand.nextInt(radius * 2) - radius;
//			int y = 128;
//			testing = new BlockPos(x, y, z);
//			// make sure to end up with a solid block
//			while(entity.getEntityWorld().isAirBlock(testing) && testing.getY() > 0) {
//				testing = testing.down(1);
//			}
//			// check if golem can spawn there
//			BlockState BlockState = entity.getEntityWorld().getBlockState(testing);
//			if(BlockState.canEntitySpawn(entity)) {
//				return testing.up(1);
//			}
//		}
//		
//		return null;
//	}

//	/**
//	 * This method makes a list of golems to pick from based on the biome passed,
//	 * then returns a random member of that list.
//	 * @param biome The biome that this golem is in.
//	 * @param rand the random number generator.
//	 * @return a Golem Class based on the biome and random chance.
//	 */
//	private static Class<? extends GolemBase> getGolemForBiome(final Biome.Category biome, final Random rand) {
//		List<Class<? extends GolemBase>> options = new ArrayList<>();
//		// the following will be added to the options in certain biomes:
//		if(biome == Biome.Category.DESERT) {
//			// use the config to get desert-type golems
//			options.addAll(Config.getDesertGolems());
//		} else if(biome == Biome.Category.PLAINS || biome == Biome.Category.SAVANNA
//				|| biome == Biome.Category.TAIGA) {
//			// use the config to get plains-type golems
//			options.addAll(Config.getPlainsGolems());
//		} else if(biome == Biome.Category.MESA) {
//			// mesa-type golems
//			////options.add(EntityHardenedClayGolem.class);
//			////options.add(EntityStainedClayGolem.class);
//		} else if(biome == Biome.Category.JUNGLE) {
//			// jungle-type golems
//			////options.add(EntityLeafGolem.class);
//		} else if(biome == Biome.Category.ICY) {
//			// snow-type golems
//			options.add(EntityIceGolem.class);
//			////options.add(EntityWoolGolem.class);
//		} else if(biome == Biome.Category.SWAMP) {
//			// swamp-type golems
//			////options.add(EntityWoodenGolem.class);
//			options.add(EntitySlimeGolem.class);
//			////options.add(EntityLeafGolem.class);
//			options.add(EntityClayGolem.class);
//		}
//		// add some rare and semi-rare golems
//		final int clay = 3, crafting = 3, obsidian = 6, glowstone = 5, books = 4;
//		if(rand.nextInt(clay) == 0) {
//			options.add(EntityClayGolem.class);
//		}
//		if(rand.nextInt(crafting) == 0) {
//			options.add(EntityCraftingGolem.class);
//		}
//		if(rand.nextInt(obsidian) == 0) {
//			options.add(EntityObsidianGolem.class);
//		}
//		if(rand.nextInt(glowstone) == 0) {
//			options.add(EntityGlowstoneGolem.class);
//		}
//		if(rand.nextInt(books) == 0) {
//			options.add(EntityBookshelfGolem.class);
//		}
//		// choose a random golem from the list
//		return options.isEmpty() ? null : options.get(rand.nextInt(options.size()));
//	}
//


	/**
	 * This method makes a list of golems to pick from based on the biome passed,
	 * then returns a random member of that list.
	 * @param biome The biome that this golem is in.
	 * @param rand the random number generator.
	 * @return a Golem Class based on the biome and random chance. May be null.
	 */
//	@Nullable
//	private static Class<? extends GolemBase> getGolemForBiome(final Biome biome, final Random rand) {
//		List<Class<? extends GolemBase>> options = new ArrayList();
//		
//		// the following will be added to the options in certain biomes:
//		if(biome instanceof BiomeDesert) {
//			// use the config to get desert-type golems
//			options.addAll(Config.getDesertGolems());
//		} else if(biome instanceof BiomePlains || biome instanceof BiomeSavanna
//				|| biome instanceof BiomeTaiga) {
//			// use the config to get plains-type golems
//			options.addAll(Config.getPlainsGolems());
//		} else if(biome instanceof BiomeMesa) {
//			// mesa-type golems
//			options.add(EntityHardenedClayGolem.class);
//			options.add(EntityStainedClayGolem.class);
//		} else if(biome instanceof BiomeJungle) {
//			// jungle-type golems
//			options.add(EntityWoodenGolem.class);
//			options.add(EntityLeafGolem.class);
//		} else if(biome instanceof BiomeSnow) {
//			// snow-type golems
//			options.add(EntityIceGolem.class);
//			options.add(EntityWoolGolem.class);
//			options.add(EntityQuartzGolem.class);
//		} else if(biome instanceof BiomeSwamp) {
//			// swamp-type golems
//			options.add(EntityWoodenGolem.class);
//			options.add(EntitySlimeGolem.class);
//			options.add(EntityLeafGolem.class);
//			options.add(EntityClayGolem.class);
//		}
//		// add some rare and semi-rare golems
//		final int clay = 3, crafting = 3, obsidian = 6, glowstone = 5, books = 4;
//		if(rand.nextInt(clay) == 0) {
//			options.add(EntityClayGolem.class);
//		}
//		if(rand.nextInt(crafting) == 0) {
//			options.add(EntityCraftingGolem.class);
//		}
//		if(rand.nextInt(obsidian) == 0) {
//			options.add(EntityObsidianGolem.class);
//		}
//		if(rand.nextInt(glowstone) == 0) {
//			options.add(EntityGlowstoneGolem.class);
//		}
//		if(rand.nextInt(books) == 0) {
//			options.add(EntityBookshelfGolem.class);
//		}
//		// choose a random golem from the list, or null
//		return options.isEmpty() ? null : options.get(rand.nextInt(options.size()));
//	}

	/**
	 * Checks if a Carved Pumpkin was placed and, if so, attempts to
	 * spawn a golem at that location where enabled by the config.
	 **/
	@SubscribeEvent
	public void onPlacePumpkin(final BlockEvent.EntityPlaceEvent event) {
		// if the config allows it, and the block is a CARVED pumpkin...
		if (!event.isCanceled() && ExtraGolemsConfig.pumpkinBuildsGolems()
			&& event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN
			&& event.getWorld() instanceof World) {
			// try to spawn a golem!
			BlockGolemHead.trySpawnGolem((World) event.getWorld(), event.getPos());
		}
	}

	@SubscribeEvent
	public void onLivingSpawned(final EntityJoinWorldEvent event) {
		// add custom 'attack golem' AI to hostile mobs. They already have this for regular iron golems
		if (event.getEntity() instanceof MobEntity) {
			final MobEntity creature = (MobEntity) event.getEntity();
			if (mobAttacksGolems(creature)) {
				creature.targetSelector.addGoal(3, new EntityAIAttackGolem(creature));
			}
		}
	}

	/**
	 * Returns true if this entity is an EntityCreature AND normally attacks Iron Golems
	 **/
	private static boolean mobAttacksGolems(MobEntity e) {
		return e instanceof AbstractSkeletonEntity || e instanceof SpiderEntity
			|| e instanceof AbstractIllagerEntity || e instanceof SlimeEntity
			|| (e instanceof ZombieEntity && !(e instanceof ZombiePigmanEntity));
	}

	private static final class EntityAIAttackGolem extends NearestAttackableTargetGoal<GolemBase> {
		private EntityAIAttackGolem(final MobEntity creature) {
			super(creature, GolemBase.class, true);
		}
	}
}
