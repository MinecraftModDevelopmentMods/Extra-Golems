package com.mcmoddev.golems.entity.modded;

import java.util.List;
import java.util.function.Predicate;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.Event;

public class UraniumGolem extends GolemBase {

  public static final String ALLOW_POISON = "Allow Special: Poison Creatures";
	public static final String AOE = "Poison Area of Effect";
	public static final String DURATION = "Poison Duration";
	public static final String AMPLIFIER = "Poison Amplifier";
	
	protected double poisonAOEFactor;
	protected int poisonLen;
	protected int poisonAmp;
	protected boolean allowPoison;
	
	protected Predicate<? super Entity> CAN_POISON = EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE)
	    .and(e -> !(e instanceof UraniumGolem) && !(e instanceof Player && ((Player)e).isCreative()));

	public UraniumGolem(final EntityType<? extends GolemBase> entityType, final Level world) {
		super(entityType, world);
		this.poisonAOEFactor = getConfigDouble(AOE);
		this.poisonLen = getConfigInt(DURATION);
		this.poisonAmp = getConfigInt(AMPLIFIER);
		this.allowPoison = getConfigBool(ALLOW_POISON);
	}

	@Override
  public void tick() {
    super.tick();
    if (allowPoison) {
      MobEffectInstance POISON_EFFECT = new MobEffectInstance(MobEffects.POISON, poisonLen, poisonAmp);
      List<Entity> entityList = level.getEntities(this,
          this.getBoundingBox().inflate(poisonAOEFactor, poisonAOEFactor * 0.75D, poisonAOEFactor), CAN_POISON);
      entityList.forEach(e -> {
        if (e instanceof LivingEntity) {
          ((LivingEntity)e).addEffect(POISON_EFFECT);
        }
      });
    }
  }

  @Override
  public boolean canBeAffected(MobEffectInstance potioneffectIn) {
    if (potioneffectIn.getEffect() == MobEffects.POISON) {
      PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, potioneffectIn);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == Event.Result.ALLOW;
    }
    return super.canBeAffected(potioneffectIn);
  }
}
