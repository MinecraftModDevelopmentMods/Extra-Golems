package com.mcmoddev.golems.golem_stats.behavior.parameter;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;


@Immutable
public class SummonEntityBehaviorParameter extends BehaviorParameter {

  private final Target target;
  private final Target targetPos;
  private final ResourceLocation entityId;
  private final CompoundNBT compoundTag;
  private final double chance;
  private final double bonusChanceInRain;
  private final ITextComponent component;
  
  public SummonEntityBehaviorParameter(final CompoundNBT tag) {
    super();
    this.target = Target.getByName(tag.getString("target"));
    this.targetPos = tag.contains("summon_pos") ? Target.getByName(tag.getString("summon_pos")) : this.target;
    this.compoundTag = tag.getCompound("entity");
    this.entityId = new ResourceLocation(this.compoundTag.getString("id"));
    this.chance = tag.getDouble("chance");
    this.bonusChanceInRain = tag.getDouble("bonus_chance_in_rain");
    this.component = new TranslationTextComponent("entitytip.summon_x",
        new TranslationTextComponent("entity." + entityId.getNamespace() + "." + entityId.getPath()));
  }
  
  public Target getTarget() { return target; }
  
  public Target getSummonPos() { return targetPos; }
  
  public ResourceLocation getEntityId() { return entityId; }
  
  public double getChance() { return chance; }
  
  public double getBonusChanceInRain() { return bonusChanceInRain; }
  
  public CompoundNBT getCompoundNBT() { return compoundTag; }
  
  public ITextComponent getDescription() { return component; }
  
  public void apply(final GolemBase self, @Nullable final Entity angerTarget) {
    // determine the random chance of trigger, taking rain into account
    double chanceApply = chance;
    if(self.world.isRainingAt(self.getPosition())) {
      chanceApply += bonusChanceInRain;
    }
    if(!self.world.isRemote() && self.world.getRandom().nextFloat() < chanceApply) {
      // create an entity to add to the level
      Optional<Entity> entity = EntityType.loadEntityUnchecked(compoundTag, self.world);
      if(entity.isPresent() && self.world instanceof ServerWorld) {
        // read entity from compound tag
        entity.get().read(compoundTag);
        // determine spawn position for the entity
        Vector3d pos = (targetPos == Target.SELF || angerTarget == null ? self.getPositionVec() : angerTarget.getPositionVec());
        entity.get().setPosition(pos.x, pos.y, pos.z);
        // add the entity to the level
        self.world.addEntity(entity.get());
        // process mob entity
        if(entity.get() instanceof MobEntity) {
          ((MobEntity)entity.get()).onInitialSpawn((ServerWorld)self.world, self.world.getDifficultyForLocation(new BlockPos(pos)), SpawnReason.MOB_SUMMONED, null, null);
          if(target == Target.ENEMY && angerTarget != null) {
            ((MobEntity)entity.get()).setAttackTarget(self.getAttackTarget());
          }
        }
        // process NeutralMob
        if(entity.get() instanceof IAngerable && target == Target.ENEMY && angerTarget != null) {
          ((IAngerable)entity.get()).setAngerTarget(angerTarget.getUniqueID());
          ((IAngerable)entity.get()).func_230258_H__();
        }
      } else {
        ExtraGolems.LOGGER.warn("GolemBehavior failed to create entity of type " + entityId);
      }
    }
  }
}
