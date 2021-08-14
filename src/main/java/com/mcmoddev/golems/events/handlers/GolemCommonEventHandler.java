package com.mcmoddev.golems.events.handlers;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.entity.FurnaceGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.IMultiTexturedGolem;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
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
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class GolemCommonEventHandler {

  /**
   * Checks if a Carved Pumpkin was placed and, if so, attempts to spawn a golem
   * at that location where enabled by the config.
   **/
  @SubscribeEvent
  public void onPlacePumpkin(final BlockEvent.EntityPlaceEvent event) {
    // if the config allows it, and the block is a CARVED pumpkin...
    if (!event.isCanceled() && ExtraGolemsConfig.pumpkinBuildsGolems() && event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN
        && event.getWorld() instanceof Level) {
      // try to spawn a golem!
      BlockGolemHead.trySpawnGolem((Level) event.getWorld(), event.getPos());
    }
  }

  /**
   * Prevents mobs from targeting inert Furnace Golems
   **/
  @SubscribeEvent
  public void onTargetEvent(final LivingSetAttackTargetEvent event) {
    if (event.getEntityLiving() instanceof Mob && event.getTarget() instanceof FurnaceGolem && !((FurnaceGolem) event.getTarget()).hasFuel()) {
      // clear the attack target
      ((Mob) event.getEntityLiving()).setTarget(null);
      ((Mob) event.getEntityLiving()).setLastHurtByMob(null);
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
    if (ExtraGolemsConfig.villagerSummonChance() > 0 && event.getEntityLiving() instanceof Villager && event.getEntityLiving().isEffectiveAi()
        && !event.getEntityLiving().isSleeping() && event.getEntityLiving().tickCount % 50 == 0) {
      Villager villager = (Villager) event.getEntityLiving();
      VillagerData villagerdata = villager.getVillagerData();
      // determine whether to spawn a golem this tick
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
          if (villager.getRandom().nextInt(100) < ExtraGolemsConfig.villagerSummonChance()) {
            // summon a golem
            GolemBase golem = summonGolem(villager);
            if (golem != null) {
              ExtraGolems.LOGGER.info("Villager summoned a golem! " + golem.toString());
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
      EntityType<? extends GolemBase> type = getGolemToSpawn(world, blockpos2);
      GolemBase golem = type != null
          ? type.create(world, (CompoundTag) null, (Component) null, (Player) null, blockpos2, MobSpawnType.MOB_SUMMONED, false, false)
          : null;
      if (golem != null) {
        // randomize texture if applicable
        if (golem instanceof IMultiTexturedGolem) {
          ((IMultiTexturedGolem<?>) golem).randomizeTexture(world, blockpos2);
        }
        // spawn the golem
        if (golem.checkSpawnRules(world, MobSpawnType.MOB_SUMMONED) && golem.checkSpawnObstruction(world)) {
          world.addFreshEntity(golem);
          return golem;
        } else {
          golem.discard();;
        }
      }
    }

    return null;
  }

  @Nullable
  private static EntityType<? extends GolemBase> getGolemToSpawn(final Level world, final BlockPos pos) {
    final List<GolemContainer> options = ExtraGolemsConfig.getVillagerGolems();
    final GolemContainer choice = options.isEmpty() ? null : options.get(world.getRandom().nextInt(options.size()));
    return choice != null ? choice.getEntityType() : null;
  }
}
