package com.mcmoddev.golems.event;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.block.GolemHeadBlock;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.network.SGolemContainerPacket;
import com.mcmoddev.golems.network.SGolemModelPacket;
import com.mcmoddev.golems.network.SummonGolemCommand;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class EGForgeEvents {
  
  @SubscribeEvent
  public static void addReloadListeners(final AddReloadListenerEvent event) {
    event.addListener(ExtraGolems.GOLEM_CONTAINERS);
    // UNUSED // event.addListener(ExtraGolems.GOLEM_RENDER_SETTINGS);
  }
  
  @SubscribeEvent
  public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
    Player player = event.getPlayer();
    // reload golem containers
    if (player instanceof ServerPlayer) {
      ExtraGolems.GOLEM_CONTAINERS.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemContainerPacket(e.getKey(), c))));
      // UNUSED // ExtraGolems.GOLEM_RENDER_SETTINGS.getEntries().forEach(e -> e.getValue().ifPresent(c -> ExtraGolems.CHANNEL.send(PacketDistributor.ALL.noArg(), new SGolemModelPacket(e.getKey(), c))));
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
        && event.getWorld() instanceof Level) {
      // try to spawn a entity!
      GolemHeadBlock.trySpawnGolem(event.getEntity(), (Level) event.getWorld(), event.getPos());
    }
  }

  /**
   * Prevents mobs from targeting inert Furnace Golems
   **/
  @SubscribeEvent
  public static void onTargetEvent(final LivingSetAttackTargetEvent event) {
    if (event.getEntityLiving() instanceof Mob && event.getTarget() instanceof GolemBase) {
      Mob mob = (Mob)event.getEntityLiving();
      GolemBase target = (GolemBase)event.getTarget();
      // clear the attack target
      if(target.getContainer().hasBehavior(GolemBehaviors.USE_FUEL) && !target.hasFuel()) {
        mob.setTarget(null);
        mob.setLastHurtByMob(null);
      }
    }
  }

  @SubscribeEvent
  public static void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
    if (EGConfig.villagerSummonChance() > 0 && event.getEntityLiving() instanceof Villager && event.getEntityLiving().isEffectiveAi()
        && !event.getEntityLiving().isSleeping() && event.getEntityLiving().tickCount % 50 == 0) {
      Villager villager = (Villager) event.getEntityLiving();
      VillagerData villagerdata = villager.getVillagerData();
      // determine whether to spawn a entity this tick
      if (villagerdata != null && !villager.isBaby() && villagerdata.getProfession() != VillagerProfession.NITWIT) {
        final long time = villager.getCommandSenderWorld().getGameTime();
        final int minNumVillagers = 3;
        // here is some code that was used in VillagerEntity
        final AABB aabb = villager.getBoundingBox().inflate(10.0D);
        final List<Villager> nearbyVillagers = villager.getCommandSenderWorld().getEntitiesOfClass(Villager.class, aabb,
            v -> v.wantsToSpawnGolem(time) && v.isAlive());
        // also check if there are already nearby golems
        final List<IronGolem> nearbyGolems = villager.getCommandSenderWorld().getEntitiesOfClass(IronGolem.class, aabb.inflate(10.0D));
        if (nearbyVillagers.size() >= minNumVillagers && nearbyGolems.isEmpty()) {
          // one last check (against config) to adjust frequency
          if (villager.getRandom().nextInt(100) < EGConfig.villagerSummonChance()) {
            // summon a entity
            GolemBase golem = summonGolem(villager);
            if (golem != null) {
              ExtraGolems.LOGGER.info("Villager summoned a entity! " + golem.toString());
              nearbyVillagers.forEach(GolemSensor::checkForNearbyGolem);
            }
          }
        }
      }
    }
  }

  @Nullable
  private static GolemBase summonGolem(@Nonnull Villager villager) {
    // This is copied from the VillagerEntity summonGolem code
    final ServerLevel world = (ServerLevel) villager.getCommandSenderWorld();
    BlockPos blockpos = villager.blockPosition();

    for (int i = 0; i < 10; ++i) {
      double d0 = (double) (world.random.nextInt(16) - 8);
      double d1 = (double) (world.random.nextInt(16) - 8);
      double d2 = 6.0D;

      for (int j = 0; j >= -12; --j) {
        BlockPos blockpos1 = blockpos.offset(d0, d2 + (double) j, d1);
        if ((world.isEmptyBlock(blockpos1) || world.getBlockState(blockpos1).getMaterial().isLiquid())
            && world.getBlockState(blockpos1.below()).getMaterial().isSolidBlocking()) {
          d2 += (double) j;
          break;
        }
      }

      BlockPos blockpos2 = blockpos.offset(d0, d2, d1);
      ResourceLocation typeName = getGolemToSpawn(world, blockpos2);
      Optional<GolemContainer> type = ExtraGolems.GOLEM_CONTAINERS.get(typeName);
      if (type.isPresent()) {
        GolemBase golem = GolemBase.create(world, typeName);
        // randomize texture if applicable
        if (golem.getTextureCount() > 0) {
          golem.randomizeTexture(world, blockpos2);
        }
        // spawn the entity
        if (golem.checkSpawnRules(world, MobSpawnType.MOB_SUMMONED) && golem.checkSpawnObstruction(world)) {
          golem.setPos(blockpos2.getX(), blockpos2.getY(), blockpos2.getZ());
          world.addFreshEntity(golem);
          golem.finalizeSpawn(world, world.getCurrentDifficultyAt(blockpos2), MobSpawnType.MOB_SUMMONED, null, null);
          return golem;
        } else {
          golem.discard();;
        }
      }
    }

    return null;
  }

  @Nullable
  private static ResourceLocation getGolemToSpawn(final Level world, final BlockPos pos) {
    final List<ResourceLocation> options = EGConfig.getVillagerGolems();
    final ResourceLocation choice = options.isEmpty() ? null : options.get(world.getRandom().nextInt(options.size()));
    return choice;
  }
}
