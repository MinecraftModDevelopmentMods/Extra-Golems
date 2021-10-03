package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;
import java.util.Optional;

import com.mcmoddev.golems.entity.IFuelConsumer;
import com.mcmoddev.golems.entity.IMultitextured;
import com.mcmoddev.golems.golem_stats.behavior.parameter.ChangeIdBehaviorParameter;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;


public class ChangeTextureGoal<T extends MobEntity & IMultitextured & IFuelConsumer> extends Goal {
  
  protected T entity;
  private final Optional<ChangeIdBehaviorParameter> tickTextures;
  private final Optional<ChangeIdBehaviorParameter> wetTextures;
  private final Optional<ChangeIdBehaviorParameter> dryTextures;
  private final Optional<ChangeIdBehaviorParameter> fueledTextures;
  private final Optional<ChangeIdBehaviorParameter> emptyTextures;
  /** True if at least one ChangeTexturesBehaviorParameter is present **/
  protected final boolean useable;
  
  protected int prevFuel;

  public ChangeTextureGoal(final T entity, Optional<ChangeIdBehaviorParameter> tickTextures,
						   Optional<ChangeIdBehaviorParameter> wetTextures, Optional<ChangeIdBehaviorParameter> dryTextures,
						   Optional<ChangeIdBehaviorParameter> fueledTextures, Optional<ChangeIdBehaviorParameter> emptyTextures) {
    super();
    this.setMutexFlags(EnumSet.noneOf(Goal.Flag.class));
    this.entity = entity;
    this.tickTextures = tickTextures;
    this.wetTextures = wetTextures;
    this.dryTextures = dryTextures;
    this.fueledTextures = fueledTextures;
    this.emptyTextures = emptyTextures;
    this.useable = (tickTextures.isPresent() || (wetTextures.isPresent() && dryTextures.isPresent()) 
        || (fueledTextures.isPresent() && emptyTextures.isPresent()));
  }

  @Override
  public boolean shouldExecute() {
    return useable;
  }

  @Override
  public boolean shouldContinueExecuting() {
    return true;
  }
  
  @Override
  public void startExecuting() {
    prevFuel = entity.getFuel();
  }

  @Override
  public void tick() {
    final String textureId = String.valueOf(entity.getTextureId());
    String updateTextureId = textureId;
    // update only if fuel has changed
    final int fuel = entity.getFuel();
    if(fueledTextures.isPresent() && emptyTextures.isPresent() && fuel != prevFuel) {
      Optional<ChangeIdBehaviorParameter> op = (fuel > 0) ? fueledTextures : emptyTextures;
      if(entity.world.getRandom().nextFloat() < op.get().getChance()) {
        updateTextureId = op.get().getId(textureId, textureId);
      }
      prevFuel = fuel;
    }
    // update each tick based on wet/dry and current texture
    if(wetTextures.isPresent() && dryTextures.isPresent()) {
      Optional<ChangeIdBehaviorParameter> op = entity.isInWaterRainOrBubbleColumn() ? wetTextures : dryTextures;
      if(entity.world.getRandom().nextFloat() < op.get().getChance()) {
        updateTextureId = op.get().getId(textureId, textureId);
      }
    }
    // update each tick based on current texture
    if(tickTextures.isPresent() && entity.world.getRandom().nextFloat() < tickTextures.get().getChance()) {
      // update tick parameter
      updateTextureId = tickTextures.get().getId(textureId, textureId);
    }
    
    // attempt to update texture ID
    if(updateTextureId != null && !updateTextureId.isEmpty() && !textureId.equals(updateTextureId)) {
      entity.setTextureId(Byte.parseByte(updateTextureId));
    }
  }
}
