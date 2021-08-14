package com.mcmoddev.golems.entity;

import java.util.function.Predicate;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;

public final class BookshelfGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

  public BookshelfGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    if (this.getConfigBool(ALLOW_SPECIAL)) {
      final MobEffect[] goodEffects = { MobEffects.FIRE_RESISTANCE, MobEffects.REGENERATION, MobEffects.DAMAGE_BOOST, MobEffects.ABSORPTION, MobEffects.LUCK,
          MobEffects.HEAL, MobEffects.DAMAGE_RESISTANCE, MobEffects.INVISIBILITY, MobEffects.MOVEMENT_SPEED, MobEffects.JUMP };
      final Predicate<GolemBase> effectPred = g -> g.getActiveEffects().isEmpty() && g.getCommandSenderWorld().getRandom().nextInt(40) == 0;
      for (final MobEffect e : goodEffects) {
        this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, e, 200, 700, 0, 1, effectPred));
      }
    }
  }
}
