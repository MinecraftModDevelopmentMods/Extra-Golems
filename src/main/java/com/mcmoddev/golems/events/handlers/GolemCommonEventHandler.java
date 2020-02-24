package com.mcmoddev.golems.events.handlers;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mcmoddev.golems.blocks.BlockGolemHead;
import com.mcmoddev.golems.entity.FurnaceGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.IMultiTexturedGolem;
import com.mcmoddev.golems.items.ItemBedrockGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
    if (!event.isCanceled() && ExtraGolemsConfig.pumpkinBuildsGolems() && event.getPlacedBlock().getBlock() == Blocks.CARVED_PUMPKIN
        && event.getWorld() instanceof World) {
      // try to spawn a golem!
      BlockGolemHead.trySpawnGolem((World) event.getWorld(), event.getPos());
    }
  }

  /**
   * Prevents mobs from targeting inert Furnace Golems
   **/
  @SubscribeEvent
  public void onTargetEvent(final LivingSetAttackTargetEvent event) {
    if (event.getEntityLiving() instanceof MobEntity && event.getTarget() instanceof FurnaceGolem && !((FurnaceGolem) event.getTarget()).hasFuel()) {
      // clear the attack target
      ((MobEntity) event.getEntityLiving()).setAttackTarget(null);
    }
  }

  /**
   * Allow healing of Iron Golems
   **/
  @SubscribeEvent
  public void onEntityInteract(final PlayerInteractEvent.EntityInteractSpecific event) {
    if (ExtraGolemsConfig.enableHealGolems() && event.getTarget() instanceof IronGolemEntity
        && new ItemStack(Blocks.IRON_BLOCK).isItemEqual(event.getItemStack())) {
      // heal the golem and reduce the itemstack
      final IronGolemEntity golem = (IronGolemEntity) event.getTarget();
      if (golem.getHealth() < golem.getMaxHealth()) {
        golem.heal(golem.getMaxHealth() * 0.25F);
        event.getItemStack().shrink(1);
        // if currently attacking this player, stop
        if (golem.getAttackTarget() == event.getPlayer()) {
          golem.setRevengeTarget(null);
          golem.setAttackTarget(null);
        }
        // spawn particles and play sound
        final Vec3d pos = golem.getPositionVec();
        ItemBedrockGolem.spawnParticles(golem.getEntityWorld(), pos.x, pos.y + golem.getHeight() / 2.0D, pos.z, 0.12D, ParticleTypes.HAPPY_VILLAGER,
            20);
        golem.playSound(SoundEvents.BLOCK_STONE_PLACE, 0.85F, 1.1F + golem.getRNG().nextFloat() * 0.2F);
      }
    }
  }

  @SubscribeEvent
  public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
    if (ExtraGolemsConfig.villagerSummonChance() > 0 && event.getEntityLiving() instanceof VillagerEntity && event.getEntityLiving().isServerWorld()
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
          if (villager.getRNG().nextInt(100) < ExtraGolemsConfig.villagerSummonChance()) {
            // summon a golem
            GolemBase golem = summonGolem(villager);
            if (golem != null) {
              ExtraGolems.LOGGER.info("Villager summoned a golem! " + golem.toString());
            }
          }
          // reset brain
          nearbyVillagers.forEach(v -> v.getBrain().setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, time));
        }
      }
    }
  }

  @Nullable
  private static GolemBase summonGolem(@Nonnull VillagerEntity villager) {
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
      GolemBase golem = type != null
          ? type.create(world, (CompoundNBT) null, (ITextComponent) null, (PlayerEntity) null, blockpos2, SpawnReason.MOB_SUMMONED, false, false)
          : null;
      if (golem != null) {
        // randomize texture if applicable
        if (golem instanceof IMultiTexturedGolem) {
          ((IMultiTexturedGolem<?>) golem).randomizeTexture(world, blockpos2);
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
    final List<GolemContainer> options = ExtraGolemsConfig.getVillagerGolems();
    final GolemContainer choice = options.isEmpty() ? null : options.get(world.getRandom().nextInt(options.size()));
    return choice != null ? choice.getEntityType() : null;
  }
}
