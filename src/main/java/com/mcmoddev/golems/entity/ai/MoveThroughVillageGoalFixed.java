package com.mcmoddev.golems.entity.ai;

import java.util.function.BooleanSupplier;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.pathfinding.GroundPathNavigator;

public class MoveThroughVillageGoalFixed extends MoveThroughVillageGoal {

  public MoveThroughVillageGoalFixed(final CreatureEntity entity, final double speedIn, 
      final boolean nocturnal, final int maxDistanceIn, final BooleanSupplier booleanSupplierIn) {
    super(entity, speedIn, nocturnal, maxDistanceIn, booleanSupplierIn);
  }
  
  @Override
  public boolean shouldExecute() {
    // The original implementation does an unsafe cast,
    // so our implementation checks that the cast will be possible
    // before allowing the task to continue
    if(this.entity.getNavigator() instanceof GroundPathNavigator) {
      return super.shouldExecute();
    }
    return false;
  }

}
