package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;
import java.util.List;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IArrowShooter;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;

public class MoveToArrowsGoal<T extends GolemBase & IArrowShooter> extends Goal {
  
  protected final T entity;
  protected final double range;
  protected final double speed;

  public MoveToArrowsGoal(final T entityIn, final double rangeIn, final double speedIn) {
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    entity = entityIn;
    range = rangeIn;
    speed = speedIn;
  }

  @Override
  public boolean canUse() { return entity.tickCount % 30 == 1; }

  @Override
  public boolean canContinueToUse() { return false; }

  @Override
  public void tick() {
    // make a list of arrow itemstacks in nearby area
    final List<ItemEntity> droppedArrows = entity.level.getEntities(EntityType.ITEM, entity.getBoundingBox().inflate(range),
        e -> !e.isRemoved() && !e.getItem().isEmpty() && !e.hasPickUpDelay() && entity.wantsToPickUp(e.getItem()));
    
    if (!droppedArrows.isEmpty()) {
      // path toward the nearest arrow itemstack
      droppedArrows.sort((e1, e2) -> (int) (entity.distanceToSqr(e1) - entity.distanceToSqr(e2)));
      entity.getNavigation().moveTo(droppedArrows.get(0), speed);
    }
  }
}