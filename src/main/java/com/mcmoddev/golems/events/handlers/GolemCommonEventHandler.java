package com.mcmoddev.golems.events.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiColorized;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles events added specifically from this mod.
 **/
public class GolemCommonEventHandler {

	/**
	 * Checks if a Carved Pumpkin was placed and, if so, attempts to spawn a golem
	 * at that location where enabled by the config.
	 **/
	@SubscribeEvent
	public void onPlacePumpkin(final BlockEvent.EntityPlaceEvent event) {
		// if the config allows it, and the block is a CARVED pumpkin...
		if (!event.isCanceled() && ExtraGolemsConfig.pumpkinBuildsGolems()
				&& event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN && event.getWorld() instanceof World) {
			// try to spawn a golem!
			BlockGolemHead.trySpawnGolem((World) event.getWorld(), event.getPos());
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
		if (event.getEntityLiving().getType() == EntityType.VILLAGER) {
			VillagerEntity villager = (VillagerEntity) event.getEntityLiving();
			VillagerData villagerdata = villager.getVillagerData();
			// determine whether to spawn a golem this tick
			if (!villager.isChild() && villagerdata.getProfession() != VillagerProfession.NITWIT) {
				final long time = villager.getEntityWorld().getGameTime();
				final int minNumVillagers = 2;
				// here is some code that was used in VillagerEntity
				final AxisAlignedBB aabb = villager.getBoundingBox().grow(10.0D, 10.0D, 10.0D);
				List<VillagerEntity> list = villager.getEntityWorld().getEntitiesWithinAABB(VillagerEntity.class, aabb);
				List<VillagerEntity> list1 = list.stream().filter(v -> v.func_223350_a(time)).limit(5L)
						.collect(Collectors.toList());
				if (list1.size() >= minNumVillagers) {
					// one last check (against config) to adjust frequency
					if(villager.getEntityWorld().getRandom().nextInt(100) > ExtraGolemsConfig.villagerSummonChance()) {
						return;
					}
					// summon a golem
					GolemBase golem = summonGolem(villager);
					ExtraGolems.LOGGER.info("Villager summoned a golem! " + golem.toString());
					if (golem != null) {
						list.forEach(v -> v.getBrain().setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, time));
					}
				}
			}
		}
	}

	@Nullable
	private static GolemBase summonGolem(VillagerEntity villager) {
		// This is copied from the VillagerEntity summonGolem code
		final World world = villager.getEntityWorld();
		BlockPos blockpos = new BlockPos(villager);

		for (int i = 0; i < 10; ++i) {
			double d0 = (double) (world.rand.nextInt(16) - 8);
			double d1 = (double) (world.rand.nextInt(16) - 8);
			double d2 = 6.0D;

			for (int j = 0; j >= -12; --j) {
				BlockPos blockpos1 = blockpos.add(d0, d2 + (double) j, d1);
				if ((world.isAirBlock(blockpos1) || world.getBlockState(blockpos1).getMaterial().isLiquid())
						&& world.getBlockState(blockpos1.down()).getMaterial().isOpaque()) {
					d2 += (double) j;
					break;
				}
			}

			BlockPos blockpos2 = blockpos.add(d0, d2, d1);
			EntityType<? extends GolemBase> type = getGolemToSpawn(world, blockpos2);
			GolemBase golem = type != null ? type.create(world, (CompoundNBT) null, (ITextComponent) null,
					(PlayerEntity) null, blockpos2, SpawnReason.MOB_SUMMONED, false, false) : null;
			if (golem != null) {
				// randomize texture if applicable
				if (golem instanceof GolemMultiTextured) {
					byte texture = (byte) world.getRandom().nextInt(((GolemMultiTextured) golem).getNumTextures());
					((GolemMultiTextured) golem).setTextureNum(texture);
				} else if (golem instanceof GolemMultiColorized) {
					byte texture = (byte) world.getRandom().nextInt(((GolemMultiColorized) golem).getColorArray().length);
					((GolemMultiColorized) golem).setTextureNum(texture);
				}
				// spawn the golem
				if (golem.canSpawn(world, SpawnReason.MOB_SUMMONED) && golem.isNotColliding(world)) {
					world.addEntity(golem);
					return golem;
				} else {
					golem.remove();
				}			
			}
		}

		return null;
	}

	@Nullable
	private static EntityType<? extends GolemBase> getGolemToSpawn(final World world, final BlockPos pos) {
		// make a list of golem names that might be chosen
		final List<String> options = new ArrayList<>();
		options.add(GolemNames.STRAW_GOLEM);
		options.add(GolemNames.WOODEN_GOLEM);
		// add some rare and semi-rare golems
		if(world.getRandom().nextInt(100) < 30) { options.add(GolemNames.CLAY_GOLEM); }
		if(world.getRandom().nextInt(100) < 50) { options.add(GolemNames.CRAFTING_GOLEM); }
		if(world.getRandom().nextInt(100) < 20) { options.add(GolemNames.OBSIDIAN_GOLEM); }
		if(world.getRandom().nextInt(100) < 20) { options.add(GolemNames.GLOWSTONE_GOLEM); }
		if(world.getRandom().nextInt(100) < 50) { options.add(GolemNames.BOOKSHELF_GOLEM); }
		if(world.getRandom().nextInt(100) < 30) { options.add(GolemNames.COAL_GOLEM); }
		// use the biome type to add more golems to the list
		final Biome.Category biome = world.getBiome(pos).getCategory();
		switch(biome) {
		// MUSHROOM types (only one)
		case MUSHROOM:
			options.clear();
			options.add(GolemNames.MUSHROOM_GOLEM);
			break;
		// FOREST types
		case EXTREME_HILLS:
		case FOREST:
		case TAIGA:
			options.add(GolemNames.WOODEN_GOLEM);
			break;
		// JUNGLE types
		case JUNGLE:
			options.add(GolemNames.WOODEN_GOLEM);
			options.add(GolemNames.LEAF_GOLEM);
			break;
		// SWAMP types
		case SWAMP:
			options.add(GolemNames.SLIME_GOLEM);
			options.add(GolemNames.CLAY_GOLEM);
			break;			
		// MESA types
		case MESA:
			options.add(GolemNames.TERRACOTTA_GOLEM);
			options.add(GolemNames.STAINEDTERRACOTTA_GOLEM);
			break;
		// DESERT or BEACH types
		case DESERT:
		case BEACH:
			options.add(GolemNames.BONE_GOLEM);
			options.add(GolemNames.SANDSTONE_GOLEM);
			options.add(GolemNames.REDSANDSTONE_GOLEM);
			break;
		// PLAINS or SAVANNA types			
		case PLAINS:
		case SAVANNA:
			options.add(GolemNames.WOODEN_GOLEM);
			options.add(GolemNames.MELON_GOLEM);
			options.add(GolemNames.WOOL_GOLEM);
			break;
		// ICY types
		case ICY:
			options.add(GolemNames.ICE_GOLEM);
			options.add(GolemNames.WOOL_GOLEM);
			options.add(GolemNames.QUARTZ_GOLEM);
			break;
		default: break;
		}

		// pick a random element of the list and find its EntityType to return
		final String name = options.isEmpty() ? "" : options.get(world.getRandom().nextInt(options.size()));
		final GolemContainer container = GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, name));
		return container != null ? container.getEntityType() : null;
	}
}
