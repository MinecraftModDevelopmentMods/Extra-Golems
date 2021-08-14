package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public final class LeafGolem extends GolemBase {
  
  private int color = 0x5F904A;

  public static final String ALLOW_SPECIAL = "Allow Special: Regeneration";

  public LeafGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    if (this.getConfigBool(ALLOW_SPECIAL)) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, MobEffects.REGENERATION, 200, 360, 0, 1,
          PassiveEffectsGoal.doesNotHaveEffect(MobEffects.REGENERATION).and(g -> g.getCommandSenderWorld().getRandom().nextInt(40) == 0)));
    }
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void aiStep() {
    super.aiStep();

    // update color
    if (this.tickCount % 10 == 2 && this.level.isClientSide && !this.isEffectiveAi()) {
      // this.world.getBiomeManager().getBiome(BlockPos)
      Biome biome = this.level.getBiome(this.getBlockPosBelowThatAffectsMyMovement().above(2));
      int color = biome.getFoliageColor();
      this.setColor(color);
    }

    // slow falling for this entity
    if (this.getDeltaMovement().y < -0.05D) {
      this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.75D, 1.0D));
    }
  }
  
  /**
   * Updates {@code #color} 
   * Note: normal render class cannot handle an alpha value here, the
   * actual texture image must be saved with transparency instead.
   **/
  public void setColor(final int toSet) {
    this.color = toSet;
  }

  /**
   * @return the full color number currently applied.
   **/
  public int getColor() {
    return this.color;
  }
}
