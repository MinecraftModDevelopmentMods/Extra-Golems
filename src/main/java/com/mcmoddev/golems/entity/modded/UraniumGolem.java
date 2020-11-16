package com.mcmoddev.golems.entity.modded;

import java.util.List;
import java.util.function.Predicate;

import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityPredicates;
import net.minecraft.world.World;
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
	
	protected Predicate<? super Entity> CAN_POISON = EntityPredicates.NOT_SPECTATING.and(EntityPredicates.IS_ALIVE)
	    .and(e -> !(e instanceof UraniumGolem) && !(e instanceof PlayerEntity && ((PlayerEntity)e).isCreative()));

	public UraniumGolem(final EntityType<? extends GolemBase> entityType, final World world) {
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
      EffectInstance POISON_EFFECT = new EffectInstance(Effects.POISON, poisonLen, poisonAmp);
      List<Entity> entityList = world.getEntitiesInAABBexcluding(this,
          this.getBoundingBox().grow(poisonAOEFactor, poisonAOEFactor * 0.75D, poisonAOEFactor), CAN_POISON);
      entityList.forEach(e -> {
        if (e instanceof LivingEntity) {
          ((LivingEntity)e).addPotionEffect(POISON_EFFECT);
        }
      });
    }
  }

  @Override
  public boolean isPotionApplicable(EffectInstance potioneffectIn) {
    if (potioneffectIn.getPotion() == Effects.POISON) {
      PotionEvent.PotionApplicableEvent event = new PotionEvent.PotionApplicableEvent(this, potioneffectIn);
      MinecraftForge.EVENT_BUS.post(event);
      return event.getResult() == Event.Result.ALLOW;
    }
    return super.isPotionApplicable(potioneffectIn);
  }
}
