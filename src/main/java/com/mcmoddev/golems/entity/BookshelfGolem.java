package com.mcmoddev.golems.entity;

import java.util.function.Predicate;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public final class BookshelfGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Potion Effects";

  public BookshelfGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    if (this.getConfigBool(ALLOW_SPECIAL)) {
      final Effect[] goodEffects = { Effects.FIRE_RESISTANCE, Effects.REGENERATION, Effects.STRENGTH, Effects.ABSORPTION, Effects.LUCK,
          Effects.INSTANT_HEALTH, Effects.RESISTANCE, Effects.INVISIBILITY, Effects.SPEED, Effects.JUMP_BOOST };
      final Predicate<GolemBase> effectPred = g -> g.getActivePotionEffects().isEmpty() && g.getEntityWorld().getRandom().nextInt(40) == 0;
      for (final Effect e : goodEffects) {
        this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, e, 200, 700, 0, 1, effectPred));
      }
    }
  }
}
