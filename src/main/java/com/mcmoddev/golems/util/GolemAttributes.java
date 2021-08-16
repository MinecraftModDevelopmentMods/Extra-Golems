package com.mcmoddev.golems.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GolemAttributes {
  
  public static final GolemAttributes EMPTY = new GolemAttributes(0, 0, 0, 0, 0, 0, false, false, false, false);
  
  public static final Codec<GolemAttributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
      Codec.DOUBLE.optionalFieldOf("health", 100.0D).forGetter(GolemAttributes::getHealth),
      Codec.DOUBLE.optionalFieldOf("attack", 7.0D).forGetter(GolemAttributes::getAttack),
      Codec.DOUBLE.optionalFieldOf("speed", 0.25D).forGetter(GolemAttributes::getSpeed),
      Codec.DOUBLE.optionalFieldOf("knockback_resistance", 0.4D).forGetter(GolemAttributes::getKnockbackResist),
      Codec.DOUBLE.optionalFieldOf("armor", 0.0D).forGetter(GolemAttributes::getArmor),
      Codec.DOUBLE.optionalFieldOf("attack_knockback", 0.0D).forGetter(GolemAttributes::getAttackKnockback),
      Codec.BOOL.optionalFieldOf("immune_to_fire", false).forGetter(GolemAttributes::hasFireImmunity),
      Codec.BOOL.optionalFieldOf("immune_to_explosions", false).forGetter(GolemAttributes::hasExplosionImmunity),
      Codec.BOOL.optionalFieldOf("hurt_by_water", false).forGetter(GolemAttributes::isHurtByWater),
      Codec.BOOL.optionalFieldOf("hurt_by_fall", false).forGetter(GolemAttributes::isHurtByFall)
    ).apply(instance, GolemAttributes::new));
  
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
  
  private GolemAttributes(double health, double attack, double speed, double knockbackResist, double armor,
      double attackKnockback, boolean fireImmunity, boolean explosionImmunity, boolean hurtByWater, boolean hurtByFall) {
    super();
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

  /** @return true if the golem takes damage when touching water **/
  public boolean isHurtByWater() { return hurtByWater; }
  
  /** @return true if the Golem takes damage upon falling from heights **/
  public boolean isHurtByFall() { return hurtByFall; }
}
