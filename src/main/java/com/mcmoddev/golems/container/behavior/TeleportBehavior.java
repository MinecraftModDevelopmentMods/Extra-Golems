package com.mcmoddev.golems.container.behavior;

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.RandomTeleportGoal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.LivingEntity;

@Immutable
public class TeleportBehavior extends GolemBehavior {
  
  protected final double range;
  protected final double chanceOnIdle;
  protected final double chanceOnHurt;
  protected final double chanceOnTarget;
  
  public TeleportBehavior(CompoundTag tag) {
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
      if (source.getEntity() instanceof LivingEntity) {
        LivingEntity target = (LivingEntity)source.getEntity();
        entity.setLastHurtByMob(target);
        entity.setTarget(target);
      }
      // attempt random teleport
      for (int i = 0; i < 16; ++i) {
        if (entity.teleportRandomly(entity, range)) {
          return;
        }
      }
    } else {
      // if damage was something else, entity MIGHT teleport away if it passes a random chance OR has no attack target
      if (entity.getRandom().nextDouble() < chanceOnHurt || (entity.getTarget() == null && entity.getRandom().nextBoolean())
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
  public void onAddDescriptions(List<Component> list) {
    list.add(new TranslatableComponent("entitytip.teleport").withStyle(ChatFormatting.LIGHT_PURPLE));
  }
}
