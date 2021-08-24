package com.mcmoddev.golems.container.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.container.behavior.parameter.ChangeTexturesBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ChangeTextureGoal;

import net.minecraft.nbt.CompoundTag;

@Immutable
public class ChangeTextureBehavior extends GolemBehavior {
  
  /** Accepts texture ID as string only **/
  private final Optional<ChangeTexturesBehaviorParameter> tickTextures;
  
  private final Optional<ChangeTexturesBehaviorParameter> wetTextures;
  private final Optional<ChangeTexturesBehaviorParameter> dryTextures;
  
  /** Only used when UseFuelBehavior is present **/
  private final Optional<ChangeTexturesBehaviorParameter> fueledTextures;
  /** Only used when UseFuelBehavior is present **/
  private final Optional<ChangeTexturesBehaviorParameter> emptyTextures;
    
  public ChangeTextureBehavior(final CompoundTag tag) {
    super(tag);
    tickTextures = tag.contains("tick") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("tick"))) : Optional.empty();
    wetTextures = tag.contains("wet") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("wet"))) : Optional.empty();
    dryTextures = tag.contains("dry") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("dry"))) : Optional.empty();
    fueledTextures = tag.contains("fuel") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("fuel"))) : Optional.empty();
    emptyTextures = tag.contains("fuel_empty") ? Optional.of(new ChangeTexturesBehaviorParameter(tag.getCompound("fuel_empty"))) : Optional.empty();
  }

  @Override
  public void onRegisterGoals(final GolemBase entity) {
    if(entity.getContainer().getMultitexture().isPresent()) {
      entity.goalSelector.addGoal(1, new ChangeTextureGoal<>(entity, tickTextures, wetTextures, dryTextures, fueledTextures, emptyTextures));
    }
  }
}
