package com.mcmoddev.golems.data.golem;

import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.util.SoundTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.SoundType;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

@Immutable
public class Attributes {
	
	/**
	 * Attribute settings to apply before the container is officially loaded.
	 * Health value is very high to ensure it does not interfere with actual health.
	 **/
	public static final Attributes EMPTY = new Attributes(Optional.of(1024.0D), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), 
			Optional.empty(), Optional.empty(), Optional.empty(), 
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

	public static final Codec<Attributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.doubleRange(0, 1024.0D).optionalFieldOf("health").forGetter(o -> Optional.ofNullable(o.health)),
			Codec.doubleRange(0, 2048.0D).optionalFieldOf("attack").forGetter(o -> Optional.ofNullable(o.attack)),
			Codec.doubleRange(0, 8.0D).optionalFieldOf("speed").forGetter(o -> Optional.ofNullable(o.speed)),
			Codec.doubleRange(0, 1.0D).optionalFieldOf("knockback_resistance").forGetter(o -> Optional.ofNullable(o.knockbackResist)),
			Codec.doubleRange(0, 30.0D).optionalFieldOf("armor").forGetter(o -> Optional.ofNullable(o.armor)),
			Codec.doubleRange(0, 2.0D).optionalFieldOf("attack_knockback").forGetter(o -> Optional.ofNullable(o.attackKnockback)),
			Codec.BOOL.optionalFieldOf("immune_to_fire").forGetter(o -> Optional.ofNullable(o.fireImmunity)),
			Codec.BOOL.optionalFieldOf("immune_to_explosions").forGetter(o -> Optional.ofNullable(o.explosionImmunity)),
			Codec.BOOL.optionalFieldOf("immune_to_freeze").forGetter(o -> Optional.ofNullable(o.freezeImmunity)),
			Codec.BOOL.optionalFieldOf("hurt_by_water").forGetter(o -> Optional.ofNullable(o.hurtByWater)),
			Codec.BOOL.optionalFieldOf("hurt_by_fall").forGetter(o -> Optional.ofNullable(o.hurtByFall)),
			Codec.BOOL.optionalFieldOf("hurt_by_heat").forGetter(o -> Optional.ofNullable(o.hurtByHeat)),
			SwimAbility.CODEC.optionalFieldOf("swim_ability").forGetter(o -> Optional.ofNullable(o.swimAbility)),
			SoundTypeRegistry.CODEC.optionalFieldOf("sound").forGetter(o -> Optional.ofNullable(o.sound))
	).apply(instance, Attributes::new));

	private final @Nullable Double health;
	private final @Nullable Double attack;
	private final @Nullable Double speed;
	private final @Nullable Double knockbackResist;
	private final @Nullable Double armor;
	private final @Nullable Double attackKnockback;

	private final @Nullable Boolean fireImmunity;
	private final @Nullable Boolean explosionImmunity;
	private final @Nullable Boolean freezeImmunity;
	private final @Nullable Boolean hurtByWater;
	private final @Nullable Boolean hurtByFall;
	private final @Nullable Boolean hurtByHeat;

	private final @Nullable SwimAbility swimAbility;
	private final @Nullable SoundType sound;

	private Attributes(Optional<Double> health, Optional<Double> attack, Optional<Double> speed, Optional<Double> knockbackResist, Optional<Double> armor,
					   Optional<Double> attackKnockback, Optional<Boolean> fireImmunity, Optional<Boolean> explosionImmunity, Optional<Boolean> freezeImmunity,
					   Optional<Boolean> hurtByWater, Optional<Boolean> hurtByFall, Optional<Boolean> hurtByHeat, Optional<SwimAbility> swimAbility,
					   Optional<SoundType> sound) {
		this.health = health.orElse(null);
		this.attack = attack.orElse(null);
		this.speed = speed.orElse(null);
		this.knockbackResist = knockbackResist.orElse(null);
		this.armor = armor.orElse(null);
		this.attackKnockback = attackKnockback.orElse(null);
		this.fireImmunity = fireImmunity.orElse(null);
		this.explosionImmunity = explosionImmunity.orElse(null);
		this.freezeImmunity = freezeImmunity.orElse(null);
		this.hurtByWater = hurtByWater.orElse(null);
		this.hurtByFall = hurtByFall.orElse(null);
		this.hurtByHeat = hurtByHeat.orElse(null);
		this.swimAbility = swimAbility.orElse(null);
		this.sound = sound.orElse(null);
	}

	/**
	 * @return the Golem's default health
	 **/
	public double getHealth() {
		return this.health != null ? this.health : 100.0D;
	}

	/**
	 * @return the Golem's default attack power
	 **/
	public double getAttack() {
		return this.attack != null ? this.attack : 10.0D;
	}

	/**
	 * @return the Golem's default move speed
	 **/
	public double getSpeed() {
		return this.speed != null ? this.speed : 0.25D;
	}

	/**
	 * @return the Golem's default attackKnockback resistance
	 **/
	public double getKnockbackResist() {
		return this.knockbackResist != null ? this.knockbackResist : 0.4D;
	}

	/**
	 * @return the Golem's default armor amount
	 **/
	public double getArmor() {
		return armor != null ? this.armor : 0;
	}

	/**
	 * @return the Golem's default attack knockback power
	 **/
	public double getAttackKnockback() {
		return attackKnockback != null ? this.attackKnockback : 0;
	}

	/**
	 * @return true if the Golem is immune to fire damage
	 **/
	public boolean hasFireImmunity() {
		return fireImmunity != null ? this.fireImmunity : false;
	}

	/**
	 * @return true if the Golem is immune to explosion damage
	 **/
	public boolean hasExplosionImmunity() {
		return explosionImmunity != null ? this.explosionImmunity : false;
	}

	/**
	 * @return true if the Golem is immune to freeze damage
	 **/
	public boolean hasFreezeImmunity() {
		return freezeImmunity != null ? this.freezeImmunity : false;
	}

	/**
	 * @return true if the entity takes damage when touching water
	 **/
	public boolean isHurtByWater() {
		return hurtByWater != null ? this.hurtByWater : false;
	}

	/**
	 * @return true if the Golem takes damage upon falling from heights
	 **/
	public boolean isHurtByFall() {
		return hurtByFall != null ? this.hurtByFall : false;
	}

	/**
	 * @return true if the Golem takes damage while in warm biomes
	 **/
	public boolean isHurtByHeat() {
		return hurtByHeat != null ? this.hurtByHeat : false;
	}

	/**
	 * @return the swim ability of the Golem
	 */
	public SwimAbility getSwimAbility() {
		return swimAbility != null ? this.swimAbility : SwimAbility.SINK;
	}

	/**
	 * @return the sound type of the golem
	 */
	public SoundType getSoundType() {
		return sound != null ? sound : SoundType.STONE;
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
		return Objects.equals(health, other.health) && Objects.equals(attack, other.attack)
				&& Objects.equals(speed, other.speed) && Objects.equals(knockbackResist, other.knockbackResist)
				&& Objects.equals(armor, other.armor) && Objects.equals(attackKnockback, other.attackKnockback)
				&& Objects.equals(fireImmunity, other.fireImmunity) && Objects.equals(explosionImmunity, other.explosionImmunity)
				&& Objects.equals(freezeImmunity, other.freezeImmunity) && Objects.equals(hurtByWater, other.hurtByWater)
				&& Objects.equals(hurtByFall, other.hurtByFall) && Objects.equals(hurtByHeat, other.hurtByHeat)
				&& Objects.equals(sound, other.sound);
	}

	@Override
	public int hashCode() {
		return Objects.hash(health, attack, speed, knockbackResist, armor, attackKnockback, fireImmunity, explosionImmunity, freezeImmunity, hurtByWater, hurtByFall, hurtByHeat, sound);
	}


	//// CLASSES ////

	public static class Builder {
		private Optional<Double> health;
		private Optional<Double> attack;
		private Optional<Double> speed;
		private Optional<Double> knockbackResist;
		private Optional<Double> armor;
		private Optional<Double> attackKnockback;

		private Optional<Boolean> fireImmunity;
		private Optional<Boolean> explosionImmunity;
		private Optional<Boolean> freezeImmunity;
		private Optional<Boolean> hurtByWater;
		private Optional<Boolean> hurtByFall;
		private Optional<Boolean> hurtByHeat;

		private Optional<SwimAbility> swimAbility;
		private Optional<SoundType> sound;

		public Builder() {
			this.health = Optional.empty();
			this.attack = Optional.empty();
			this.speed = Optional.empty();
			this.knockbackResist = Optional.empty();
			this.armor = Optional.empty();
			this.attackKnockback = Optional.empty();

			this.fireImmunity = Optional.empty();
			this.explosionImmunity = Optional.empty();
			this.freezeImmunity = Optional.empty();
			this.hurtByWater = Optional.empty();
			this.hurtByFall = Optional.empty();
			this.hurtByHeat = Optional.empty();

			this.swimAbility = Optional.empty();
			this.sound = Optional.empty();
		}

		/**
		 * @param attributes an attributes object
		 * @return a builder with all fields set to the values of the given attributes, if they are present
		 */
		public Builder copy(final @Nullable Attributes attributes) {
			// check non null
			if(null == attributes) return this;
			// copy attributes
			if(attributes.health != null) this.health(attributes.health);
			if(attributes.attack != null) this.attack(attributes.attack);
			if(attributes.speed != null) this.speed(attributes.speed);
			if(attributes.knockbackResist != null) this.knockbackResistance(attributes.knockbackResist);
			if(attributes.armor != null) this.armor(attributes.armor);
			if(attributes.attackKnockback != null) this.attackKnockback(attributes.attackKnockback);
			if(attributes.fireImmunity != null) this.fireImmunity(attributes.fireImmunity);
			if(attributes.explosionImmunity != null) this.explosionImmunity(attributes.explosionImmunity);
			if(attributes.freezeImmunity != null) this.freezeImmunity(attributes.freezeImmunity);
			if(attributes.hurtByWater != null) this.hurtByWater(attributes.hurtByWater);
			if(attributes.hurtByFall != null) this.hurtByFall(attributes.hurtByFall);
			if(attributes.hurtByHeat != null) this.hurtByHeat(attributes.hurtByHeat);
			if(attributes.swimAbility != null) this.swimAbility(attributes.swimAbility);
			if(attributes.sound != null) this.sound(attributes.sound);
			return this;
		}

		/**
		 * @param health the health amount
		 * @return the builder instance
		 */
		public Builder health(final double health) {
			this.health = Optional.of(health);
			return this;
		}

		/**
		 * @param attack the attack value
		 * @return the builder instance
		 */
		public Builder attack(final double attack) {
			this.attack = Optional.of(attack);
			return this;
		}

		/**
		 * @param speed the movement speed value
		 * @return the builder instance
		 */
		public Builder speed(final double speed) {
			this.speed = Optional.of(speed);
			return this;
		}

		/**
		 * @param knockbackResist the knockback resistance value
		 * @return the builder instance
		 */
		public Builder knockbackResistance(final double knockbackResist) {
			this.knockbackResist = Optional.of(knockbackResist);
			return this;
		}

		/**
		 * @param armor the armor value
		 * @return the builder instance
		 */
		public Builder armor(final double armor) {
			this.armor = Optional.of(armor);
			return this;
		}

		/**
		 * @param attackKnockback the attack knockback value
		 * @return the builder instance
		 */
		public Builder attackKnockback(final double attackKnockback) {
			this.attackKnockback = Optional.of(attackKnockback);
			return this;
		}

		/**
		 * @param fireImmunity the fire immunity flag
		 * @return the builder instance
		 */
		public Builder fireImmunity(final boolean fireImmunity) {
			this.fireImmunity = Optional.of(fireImmunity);
			return this;
		}

		/**
		 * @param explosionImmunity the explosion immunity flag
		 * @return the builder instance
		 */
		public Builder explosionImmunity(final boolean explosionImmunity) {
			this.explosionImmunity = Optional.of(explosionImmunity);
			return this;
		}

		/**
		 * @param freezeImmunity the freeze immunity flag
		 * @return the builder instance
		 */
		public Builder freezeImmunity(final boolean freezeImmunity) {
			this.freezeImmunity = Optional.of(freezeImmunity);
			return this;
		}

		/**
		 * @param hurtByWater the hurt by water flag
		 * @return the builder instance
		 */
		public Builder hurtByWater(final boolean hurtByWater) {
			this.hurtByWater = Optional.of(hurtByWater);
			return this;
		}

		/**
		 * @param hurtByFall the hurt by fall flag
		 * @return the builder instance
		 */
		public Builder hurtByFall(final boolean hurtByFall) {
			this.hurtByFall = Optional.of(hurtByFall);
			return this;
		}

		/**
		 * @param hurtByHeat the hurt by heat flag
		 * @return the builder instance
		 */
		public Builder hurtByHeat(final boolean hurtByHeat) {
			this.hurtByHeat = Optional.of(hurtByHeat);
			return this;
		}

		/**
		 * @param swimAbility the swim ability enum
		 * @return the builder instance
		 */
		public Builder swimAbility(final SwimAbility swimAbility) {
			this.swimAbility = Optional.of(swimAbility);
			return this;
		}

		/**
		 * @param sound the sound type
		 * @return the builder instance
		 */
		public Builder sound(final SoundType sound) {
			this.sound = Optional.of(sound);
			return this;
		}

		/**
		 * @return a new {@link Attributes} instance
		 */
		public Attributes build() {
			return new Attributes(health, attack, speed, knockbackResist, armor, attackKnockback, fireImmunity, explosionImmunity, freezeImmunity, hurtByWater, hurtByFall, hurtByHeat, swimAbility, sound);
		}
	}
}
