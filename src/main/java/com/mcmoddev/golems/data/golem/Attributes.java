package com.mcmoddev.golems.data.golem;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.Behaviors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public class Attributes {

	private static final double MAX_HEALTH = 1024.0D;

	/**
	 * Attribute settings to apply before the container is officially loaded.
	 * Health value is very high to ensure it does not interfere with actual health.
	 **/
	public static final Attributes EMPTY = new Attributes(MAX_HEALTH, 0, 0, 0, 0, 0, false, false, false, false, false);

	public static final Codec<Attributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.optionalFieldOf("health", 100.0D).forGetter(Attributes::getHealth),
			Codec.DOUBLE.optionalFieldOf("attack", 10.0D).forGetter(Attributes::getAttack),
			Codec.DOUBLE.optionalFieldOf("speed", 0.25D).forGetter(Attributes::getSpeed),
			Codec.DOUBLE.optionalFieldOf("knockback_resistance", 0.4D).forGetter(Attributes::getKnockbackResist),
			Codec.DOUBLE.optionalFieldOf("armor", 0.0D).forGetter(Attributes::getArmor),
			Codec.DOUBLE.optionalFieldOf("attack_knockback", 0.0D).forGetter(Attributes::getAttackKnockback),
			Codec.BOOL.optionalFieldOf("immune_to_fire", false).forGetter(Attributes::hasFireImmunity),
			Codec.BOOL.optionalFieldOf("immune_to_explosions", false).forGetter(Attributes::hasExplosionImmunity),
			Codec.BOOL.optionalFieldOf("hurt_by_water", false).forGetter(Attributes::isHurtByWater),
			Codec.BOOL.optionalFieldOf("hurt_by_fall", false).forGetter(Attributes::isHurtByFall),
			Codec.BOOL.optionalFieldOf("hurt_by_heat", false).forGetter(Attributes::isHurtByHeat)
	).apply(instance, Attributes::new));
	public static final Codec<Holder<Attributes>> HOLDER_CODEC = RegistryFileCodec.create(EGRegistry.Keys.ATTRIBUTES, CODEC, true);

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

	private Attributes(double health, double attack, double speed, double knockbackResist, double armor,
					   double attackKnockback, boolean fireImmunity, boolean explosionImmunity, boolean hurtByWater, boolean hurtByFall,
					   boolean hurtByHeat) {
		this.health = Math.min(health, MAX_HEALTH);
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

	/**
	 * @return the Golem's default health
	 **/
	public double getHealth() {
		return this.health;
	}

	/**
	 * @return the Golem's default attack power
	 **/
	public double getAttack() {
		return this.attack;
	}

	/**
	 * @return the Golem's default move speed
	 **/
	public double getSpeed() {
		return this.speed;
	}

	/**
	 * @return the Golem's default attackKnockback resistance
	 **/
	public double getKnockbackResist() {
		return this.knockbackResist;
	}

	/**
	 * @return the Golem's default armor amount
	 **/
	public double getArmor() {
		return armor;
	}

	/**
	 * @return the Golem's default attack knockback power
	 **/
	public double getAttackKnockback() {
		return attackKnockback;
	}

	/**
	 * @return true if the Golem is immune to fire damage
	 **/
	public boolean hasFireImmunity() {
		return fireImmunity;
	}

	/**
	 * @return true if the Golem is immune to explosion damage
	 **/
	public boolean hasExplosionImmunity() {
		return explosionImmunity;
	}

	/**
	 * @return true if the entity takes damage when touching water
	 **/
	public boolean isHurtByWater() {
		return hurtByWater;
	}

	/**
	 * @return true if the Golem takes damage upon falling from heights
	 **/
	public boolean isHurtByFall() {
		return hurtByFall;
	}

	/**
	 * @return true if the Golem takes damage while in warm biomes
	 **/
	public boolean isHurtByHeat() {
		return hurtByHeat;
	}

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

	//// EQUALITY ////


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Attributes)) return false;
		Attributes other = (Attributes) o;
		return Double.compare(other.health, health) == 0 && Double.compare(other.attack, attack) == 0
				&& Double.compare(other.speed, speed) == 0 && Double.compare(other.knockbackResist, knockbackResist) == 0
				&& Double.compare(other.armor, armor) == 0 && Double.compare(other.attackKnockback, attackKnockback) == 0
				&& fireImmunity == other.fireImmunity && explosionImmunity == other.explosionImmunity
				&& hurtByWater == other.hurtByWater && hurtByFall == other.hurtByFall && hurtByHeat == other.hurtByHeat;
	}

	@Override
	public int hashCode() {
		return Objects.hash(health, attack, speed, knockbackResist, armor, attackKnockback, fireImmunity, explosionImmunity, hurtByWater, hurtByFall, hurtByHeat);
	}
}
