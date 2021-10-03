package com.mcmoddev.golems.event;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.golem_stats.GolemContainer;
import com.mcmoddev.golems.golem_stats.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.network.SGolemModelPacket;
import com.mcmoddev.golems.network.SummonGolemCommand;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.sensor.GolemLastSeenSensor;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;


public class EGForgeEvents {
  
  @SubscribeEvent
  public static void addReloadListeners(final AddReloadListenerEvent event) {
    event.addListener(ExtraGolems.GOLEM_CONTAINERS);
    event.addListener(ExtraGolems.GOLEM_RENDER_SETTINGS);
  }
  
  @SubscribeEvent
  public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
    PlayerEntity player = event.getPlayer();
    // reload golem containers
    if (player instanceof ServerPlayerEntity) {
      ExtraGolems.GOLEM_CONTAINERS.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), c))));
      ExtraGolems.GOLEM_RENDER_SETTINGS.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemModelPacket(e.getKey(), c))));
    }
  }

  @SubscribeEvent
  public static void onAddCommands(final RegisterCommandsEvent event) {
    SummonGolemCommand.register(event.getDispatcher());
  }

  /**
   * Checks if a Carved Pumpkin was placed and, if so, attempts to spawn a entity
   * at that location where enabled by the config.
   **/
  @SubscribeEvent
  public static void onPlacePumpkin(final BlockEvent.EntityPlaceEvent event) {
    // if the config allows it, and the block is a CARVED pumpkin...
    if (!event.isCanceled() && EGConfig.pumpkinBuildsGolems() && event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN
        && event.getWorld() instanceof World) {
      // try to spawn an entity!
      GolemHeadBlock.trySpawnGolem(event.getEntity(), (World) event.getWorld(), event.getPos());
    }
  }

  /**
   * Prevents mobs from targeting inert Furnace Golems
   **/
  @SubscribeEvent
  public static void onTargetEvent(final LivingSetAttackTargetEvent event) {
    if (event.getEntityLiving() instanceof MobEntity && event.getTarget() instanceof GolemBase) {
      MobEntity mob = (MobEntity)event.getEntityLiving();
      GolemBase target = (GolemBase)event.getTarget();
      // clear the attack target
      if(target.getContainer().hasBehavior(GolemBehaviors.USE_FUEL) && !target.hasFuel()) {
        mob.setAttackTarget(null);
        mob.setRevengeTarget(null);
      }
    }
  }

  @SubscribeEvent
  public static void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
	if (EGConfig.villagerSummonChance() > 0 && event.getEntityLiving() instanceof VillagerEntity && event.getEntityLiving().isServerWorld()
			&& !event.getEntityLiving().isSleeping() && event.getEntityLiving().ticksExisted % 50 == 0) {
	  VillagerEntity villager = (VillagerEntity) event.getEntityLiving();
	  VillagerData villagerdata = villager.getVillagerData();
	  // determine whether to spawn a golem this tick
	  if (villagerdata != null && !villager.isChild() && villagerdata.getProfession() != VillagerProfession.NITWIT) {
		final long time = villager.getEntityWorld().getGameTime();
		final int minNumVillagers = 3;
		// here is some code that was used in VillagerEntity
		final AxisAlignedBB aabb = villager.getBoundingBox().grow(10.0D);
		final List<VillagerEntity> nearbyVillagers = villager.getEntityWorld().getEntitiesWithinAABB(VillagerEntity.class, aabb,
				v -> v.canSpawnGolems(time) && v.isAlive());
		// also check if there are already nearby golems
		final List<IronGolemEntity> nearbyGolems = villager.getEntityWorld().getEntitiesWithinAABB(IronGolemEntity.class, aabb.grow(10.0D));
		if (nearbyVillagers.size() >= minNumVillagers && nearbyGolems.isEmpty()) {
		  // one last check (against config) to adjust frequency
		  if (villager.getRNG().nextInt(100) < EGConfig.villagerSummonChance()) {
			// summon a golem
			GolemBase golem = summonGolem(villager);
			if (golem != null) {
			  ExtraGolems.LOGGER.info("Villager summoned a golem! " + golem.toString());
			  nearbyVillagers.forEach(GolemLastSeenSensor::update);
			}
		  }
		}
	  }
	}
  }

  @Nullable
  private static GolemBase summonGolem(@Nonnull VillagerEntity villager) {
    // This is copied from the VillagerEntity summonGolem code
    final ServerWorld world = (ServerWorld) villager.getEntityWorld();
    BlockPos blockpos = villager.getPosition();

    for (int i = 0; i < 10; ++i) {
      double d0 = (double) (world.getRandom().nextInt(16) - 8);
      double d1 = (double) (world.getRandom().nextInt(16) - 8);
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
      ResourceLocation typeName = getGolemToSpawn(world, blockpos2);
      Optional<GolemContainer> type = ExtraGolems.GOLEM_CONTAINERS.get(typeName);
      if (type.isPresent()) {
        GolemBase golem = GolemBase.create(world, typeName);
        // randomize texture if applicable
        if (golem.getTextureCount() > 0) {
          golem.randomizeTexture(world, blockpos2);
        }
        // spawn the entity
        if (golem.canSpawn(world, SpawnReason.MOB_SUMMONED) && golem.isNotColliding(world)) {
          golem.setPosition(blockpos2.getX(), blockpos2.getY(), blockpos2.getZ());
          world.addEntity(golem);
          golem.onInitialSpawn(world, world.getDifficultyForLocation(blockpos2), SpawnReason.MOB_SUMMONED, null, null);
          return golem;
        } else {
          golem.remove();;
        }
      }
    }

    return null;
  }

  @Nullable
  private static ResourceLocation getGolemToSpawn(final World world, final BlockPos pos) {
    final List<ResourceLocation> options = EGConfig.getVillagerGolems();
    final ResourceLocation choice = options.isEmpty() ? null : options.get(world.getRandom().nextInt(options.size()));
    return choice;
  }
}
