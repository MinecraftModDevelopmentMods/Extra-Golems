package com.mcmoddev.golems.golem_stats.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.RandomTeleportGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;


/**
 * This behavior allows an entity to teleport randomly,
 * when hurt, or when attacking.
 **/
@Immutable
public class TeleportBehavior extends GolemBehavior {
  
  /** The maximum distance the entity can teleport **/
  protected final double range;
  /** The percent chance [0,1] to apply when the entity is doing nothing **/
  protected final double chanceOnIdle;
  /** The percent chance [0,1] to apply when the entity is hurt **/
  protected final double chanceOnHurt;
  /** The percent chance [0,1] to apply each tick that the entity has an attack target **/
  protected final double chanceOnTarget;
  
  public TeleportBehavior(CompoundNBT tag) {
    super(tag);
    range = tag.getDouble("range");
    chanceOnIdle = tag.getDouble("chance_on_idle");
    chanceOnHurt = tag.getDouble("chance_on_hurt");
    chanceOnTarget = tag.getDouble("chance_on_target");
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    entity.goalSelector.addGoal(1, new RandomTeleportGoal<>(entity, range, chanceOnIdle, chanceOnTarget));
  }
  
  @Override
  public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
    if (source instanceof IndirectEntityDamageSource) {
      // if damage was projectile, remember the indirect entity and set as target
      if (source.getTrueSource() instanceof LivingEntity) {
        LivingEntity target = (LivingEntity)source.getTrueSource();
        entity.setLastAttackedEntity(target);
        entity.setAttackTarget(target);
      }
      // attempt random teleport
      for (int i = 0; i < 16; ++i) {
        if (entity.teleportRandomly(entity, range)) {
          return;
        }
      }
    } else {
      // if damage was something else, entity MIGHT teleport away if it passes a random chance OR has no attack target
      if (entity.world.getRandom().nextDouble() < chanceOnHurt || (entity.getAttackTarget() == null && entity.world.getRandom().nextBoolean())
          || (entity.getContainer().getAttributes().isHurtByWater() && source == DamageSource.DROWN)) {
        // attempt random teleport
        for (int i = 0; i < 16; ++i) {
          if (entity.teleportRandomly(entity, range)) {
            return;
          }
        }
      }
    }
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    list.add(new TranslationTextComponent("entitytip.teleport").mergeStyle(TextFormatting.LIGHT_PURPLE));
  }
}
