package com.mcmoddev.golems.container;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AttributeSettings {
  
  public static final AttributeSettings EMPTY = new AttributeSettings(1024, 0, 0, 0, 0, 0, false, false, false, false, false);
  
  public static final Codec<AttributeSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.DOUBLE.optionalFieldOf("health", 100.0D).forGetter(AttributeSettings::getHealth),
      Codec.DOUBLE.optionalFieldOf("attack", 10.0D).forGetter(AttributeSettings::getAttack),
      Codec.DOUBLE.optionalFieldOf("speed", 0.25D).forGetter(AttributeSettings::getSpeed),
      Codec.DOUBLE.optionalFieldOf("knockback_resistance", 0.4D).forGetter(AttributeSettings::getKnockbackResist),
      Codec.DOUBLE.optionalFieldOf("armor", 0.0D).forGetter(AttributeSettings::getArmor),
      Codec.DOUBLE.optionalFieldOf("attack_knockback", 0.0D).forGetter(AttributeSettings::getAttackKnockback),
      Codec.BOOL.optionalFieldOf("immune_to_fire", false).forGetter(AttributeSettings::hasFireImmunity),
      Codec.BOOL.optionalFieldOf("immune_to_explosions", false).forGetter(AttributeSettings::hasExplosionImmunity),
      Codec.BOOL.optionalFieldOf("hurt_by_water", false).forGetter(AttributeSettings::isHurtByWater),
      Codec.BOOL.optionalFieldOf("hurt_by_fall", false).forGetter(AttributeSettings::isHurtByFall),
      Codec.BOOL.optionalFieldOf("hurt_by_heat", false).forGetter(AttributeSettings::isHurtByHeat)
    ).apply(instance, AttributeSettings::new));
  
  private final double health;
  private final double attack;
  private final double speed;
  private final double knockbackResist;
  private final double armor;
  private final double attackKnockback;

  private final boolean fireImmunity;
  private final boolean explosionImmunity;
  private final boolean hurtByWater;
  private final boolean hurtByFall;
  private final boolean hurtByHeat;

  private AttributeSettings(double health, double attack, double speed, double knockbackResist, double armor,
      double attackKnockback, boolean fireImmunity, boolean explosionImmunity, boolean hurtByWater, boolean hurtByFall,
      boolean hurtByHeat) {
    this.health = health;
    this.attack = attack;
    this.speed = speed;
    this.knockbackResist = knockbackResist;
    this.armor = armor;
    this.attackKnockback = attackKnockback;
    this.fireImmunity = fireImmunity;
    this.explosionImmunity = explosionImmunity;
    this.hurtByWater = hurtByWater;
    this.hurtByFall = hurtByFall;
    this.hurtByHeat = hurtByHeat;
  }
  
  /** @return the Golem's default health **/
  public double getHealth() { return this.health; }

  /** @return the Golem's default attack power **/
  public double getAttack() { return this.attack; }

  /** @return the Golem's default move speed **/
  public double getSpeed() { return this.speed; }

  /** @return the Golem's default attackKnockback resistance **/
  public double getKnockbackResist() { return this.knockbackResist; }

  /** @return the Golem's default armor amount **/
  public double getArmor() { return armor; }

  /** @return the Golem's default attack knockback power **/
  public double getAttackKnockback() { return attackKnockback; }
  
  /** @return true if the Golem is immune to fire damage **/
  public boolean hasFireImmunity() { return fireImmunity; }

  /** @return true if the Golem is immune to explosion damage **/
  public boolean hasExplosionImmunity() { return explosionImmunity; }

  /** @return true if the entity takes damage when touching water **/
  public boolean isHurtByWater() { return hurtByWater; }
  
  /** @return true if the Golem takes damage upon falling from heights **/
  public boolean isHurtByFall() { return hurtByFall; }
  
  /** @return true if the Golem takes damage while in warm biomes **/
  public boolean isHurtByHeat() { return hurtByHeat; }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("AttributeSettings: ");
    b.append("health[").append(health).append("] ");
    b.append("attack[").append(attack).append("] ");
    b.append("speed[").append(speed).append("] ");
    b.append("resist[").append(knockbackResist).append("] ");
    b.append("armor[").append(armor).append("] ");
    b.append("knockback[").append(attackKnockback).append("] ");
    b.append("fire_immune[").append(fireImmunity).append("] ");
    b.append("explode_immune[").append(explosionImmunity).append("] ");
    b.append("hurt_by_water[").append(hurtByWater).append("] ");
    b.append("hurt_by_fall[").append(hurtByFall).append("] ");
    b.append("hurt_by_heat[").append(hurtByHeat).append("] ");
    return b.toString();
  }
}
