package com.mcmoddev.golems.entity.goal;

import java.util.EnumSet;
import java.util.Optional;

import com.mcmoddev.golems.container.behavior.parameter.ChangeTexturesBehaviorParameter;
import com.mcmoddev.golems.entity.IFuelConsumer;
import com.mcmoddev.golems.entity.IMultitextured;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;


public class ChangeTextureGoal<T extends MobEntity & IMultitextured & IFuelConsumer> extends Goal {
  
  protected T entity;
  private final Optional<ChangeTexturesBehaviorParameter> tickTextures;
  private final Optional<ChangeTexturesBehaviorParameter> wetTextures;
  private final Optional<ChangeTexturesBehaviorParameter> dryTextures;
  private final Optional<ChangeTexturesBehaviorParameter> fueledTextures;
  private final Optional<ChangeTexturesBehaviorParameter> emptyTextures;
  /** True if at least one ChangeTexturesBehaviorParameter is present **/
  protected final boolean useable;
  
  protected int prevFuel;

  public ChangeTextureGoal(final T entity, Optional<ChangeTexturesBehaviorParameter> tickTextures,
  Optional<ChangeTexturesBehaviorParameter> wetTextures, Optional<ChangeTexturesBehaviorParameter> dryTextures,
  Optional<ChangeTexturesBehaviorParameter> fueledTextures, Optional<ChangeTexturesBehaviorParameter> emptyTextures) {
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
    final int textureId = entity.getTextureId();
    int updateTextureId = textureId;
    // update only if fuel has changed
    final int fuel = entity.getFuel();
    if(fueledTextures.isPresent() && emptyTextures.isPresent() && fuel != prevFuel) {
      Optional<ChangeTexturesBehaviorParameter> op = (fuel > 0) ? fueledTextures : emptyTextures;
      if(entity.world.getRandom().nextFloat() < op.get().getChance()) {
        updateTextureId = op.get().getTextureId(String.valueOf(textureId), textureId);
      }
      prevFuel = fuel;
    }
    // update each tick based on wet/dry and current texture
    if(wetTextures.isPresent() && dryTextures.isPresent()) {
      Optional<ChangeTexturesBehaviorParameter> op = entity.isInWaterRainOrBubbleColumn() ? wetTextures : dryTextures;
      if(entity.world.getRandom().nextFloat() < op.get().getChance()) {
        updateTextureId = op.get().getTextureId(String.valueOf(textureId), textureId);
      }
    }
    // update each tick based on current texture
    if(tickTextures.isPresent() && entity.world.getRandom().nextFloat() < tickTextures.get().getChance()) {
      // update tick parameter
      updateTextureId = tickTextures.get().getTextureId(String.valueOf(textureId), textureId);
    }
    
    // attempt to update texture ID
    if(updateTextureId != textureId) {
      entity.setTextureId((byte) updateTextureId);
    }
  }
}
