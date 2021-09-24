package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;

public class MoveToItemGoal extends Goal {
  
  protected final MobEntity entity;
  protected final double range;
  protected final double interval;
  protected final double speed;

  public MoveToItemGoal(final MobEntity entityIn, final double rangeIn, final int intervalIn, final double speedIn) {
    this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    entity = entityIn;
    range = rangeIn;
    interval = Math.max(intervalIn, 1);
    speed = speedIn;
  }

  @Override
  public boolean shouldExecute() { return entity.ticksExisted % interval == 0; }

  @Override
  public boolean shouldContinueExecuting() { return false; }

  @Override
  public void tick() {
    // make a list of arrow itemstacks in nearby area
    final List<ItemEntity> items = entity.world.getEntitiesWithinAABB(EntityType.ITEM, entity.getBoundingBox().grow(range),
        e -> e.isAlive() && !e.getItem().isEmpty() && !e.cannotPickup() && entity.canPickUpItem(e.getItem()));
    
    if (!items.isEmpty()) {
      // path toward the nearest arrow itemstack
      items.sort((e1, e2) -> (int) (entity.getDistanceSq(e1) - entity.getDistanceSq(e2)));
      entity.getNavigator().tryMoveToEntityLiving(items.get(0), speed);
    }
  }
}
