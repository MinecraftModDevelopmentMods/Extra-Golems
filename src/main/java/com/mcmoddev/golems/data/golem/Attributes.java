package com.mcmoddev.golems.data.golem;

import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mcmoddev.golems.util.SoundTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
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
			Optional.empty(), Optional.empty());

	public static final Codec<Attributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.doubleRange(0, 1024.0D).optionalFieldOf("health").forGetter(o -> Optional.ofNullable(o.health)),
			Codec.doubleRange(0, 2048.0D).optionalFieldOf("attack").forGetter(o -> Optional.ofNullable(o.attack)),
			Codec.doubleRange(0, 8.0D).optionalFieldOf("speed").forGetter(o -> Optional.ofNullable(o.speed)),
			Codec.doubleRange(0, 1.0D).optionalFieldOf("knockback_resistance").forGetter(o -> Optional.ofNullable(o.knockbackResistance)),
			Codec.doubleRange(0, 30.0D).optionalFieldOf("armor").forGetter(o -> Optional.ofNullable(o.armor)),
			Codec.doubleRange(0, 2.0D).optionalFieldOf("attack_knockback").forGetter(o -> Optional.ofNullable(o.attackKnockback)),
			DeferredHolderSet.codec(Registries.DAMAGE_TYPE).optionalFieldOf("immune").forGetter(o -> Optional.ofNullable(o.damageImmune)),
			DeferredHolderSet.codec(Registries.DAMAGE_TYPE).optionalFieldOf("weak").forGetter(o -> Optional.ofNullable(o.damageWeak)),
			Codec.BOOL.optionalFieldOf("invulnerable").forGetter(o -> Optional.ofNullable(o.invulnerable)),
			SwimAbility.CODEC.optionalFieldOf("swim_ability").forGetter(o -> Optional.ofNullable(o.swimAbility)),
			SoundTypeRegistry.CODEC.optionalFieldOf("sound").forGetter(o -> Optional.ofNullable(o.sound))
	).apply(instance, Attributes::new));

	private final @Nullable Double health;
	private final @Nullable Double attack;
	private final @Nullable Double speed;
	private final @Nullable Double knockbackResistance;
	private final @Nullable Double armor;
	private final @Nullable Double attackKnockback;

	private final @Nullable DeferredHolderSet<DamageType> damageImmune;
	private final @Nullable DeferredHolderSet<DamageType> damageWeak;
	private final @Nullable Boolean invulnerable;

	private final @Nullable SwimAbility swimAbility;
	private final @Nullable SoundType sound;

	private Attributes(Optional<Double> health, Optional<Double> attack, Optional<Double> speed, Optional<Double> knockbackResistance, Optional<Double> armor,
					   Optional<Double> attackKnockback, Optional<DeferredHolderSet<DamageType>> damageImmune, Optional<DeferredHolderSet<DamageType>> damageWeak,
					   Optional<Boolean> invulnerable, Optional<SwimAbility> swimAbility, Optional<SoundType> sound) {
		this.health = health.orElse(null);
		this.attack = attack.orElse(null);
		this.speed = speed.orElse(null);
		this.knockbackResistance = knockbackResistance.orElse(null);
		this.armor = armor.orElse(null);
		this.attackKnockback = attackKnockback.orElse(null);
		this.damageImmune = damageImmune.orElse(null);
		this.damageWeak = damageWeak.orElse(null);
		this.invulnerable = invulnerable.orElse(null);
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
	public double getKnockbackResistance() {
		return this.knockbackResistance != null ? this.knockbackResistance : 0.4D;
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
	 * @param registryAccess the registry access
	 * @param damageSource the damage source
	 * @return true if the Golem is immune to the given damage source
	 */
	public boolean isImmuneTo(final RegistryAccess registryAccess, final DamageSource damageSource) {
		final Registry<DamageType> registry = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
		return damageImmune != null && damageImmune.get(registry).contains(damageSource.typeHolder())
				&& (null == damageWeak || !damageWeak.get(registry).contains(damageSource.typeHolder()));
	}

	/**
	 * @param registryAccess the registry access
	 * @param damageSource the damage source
	 * @return true if the Golem is weak to the given damage source
	 */
	public boolean isWeakTo(final RegistryAccess registryAccess, final DamageSource damageSource) {
		final Registry<DamageType> registry = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
		return damageWeak != null && damageWeak.get(registry).contains(damageSource.typeHolder())
				&& (null == damageImmune || !damageImmune.get(registry).contains(damageSource.typeHolder()));
	}

	public DeferredHolderSet<DamageType> getDamageImmune() {
		return damageImmune != null ? damageImmune : DeferredHolderSet.empty();
	}

	public DeferredHolderSet<DamageType> getDamageWeak() {
		return damageWeak != null ? damageWeak : DeferredHolderSet.empty();
	}

	public boolean isInvulnerable() {
		return invulnerable != null ? invulnerable : false;
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
		b.append("resist[").append(knockbackResistance).append("] ");
		b.append("armor[").append(armor).append("] ");
		b.append("knockback[").append(attackKnockback).append("] ");
		return b.toString();
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Attributes)) return false;
		Attributes other = (Attributes) o;
		return Objects.equals(health, other.health) && Objects.equals(attack, other.attack) && Objects.equals(speed, other.speed)
				&& Objects.equals(knockbackResistance, other.knockbackResistance) && Objects.equals(armor, other.armor)
				&& Objects.equals(attackKnockback, other.attackKnockback) && Objects.equals(damageImmune, other.damageImmune)
				&& Objects.equals(damageWeak, other.damageWeak) && Objects.equals(invulnerable, other.invulnerable)
				&& swimAbility == other.swimAbility && Objects.equals(sound, other.sound);
	}

	@Override
	public int hashCode() {
		return Objects.hash(health, attack, speed, knockbackResistance, armor, attackKnockback, damageImmune, damageWeak, invulnerable, swimAbility, sound);
	}


	//// CLASSES ////

	public static class Builder {
		private Optional<Double> health;
		private Optional<Double> attack;
		private Optional<Double> speed;
		private Optional<Double> knockbackResist;
		private Optional<Double> armor;
		private Optional<Double> attackKnockback;

		private Optional<DeferredHolderSet<DamageType>> immune;
		private Optional<DeferredHolderSet<DamageType>> weak;
		private Optional<Boolean> invulnerable;

		private Optional<SwimAbility> swimAbility;
		private Optional<SoundType> sound;

		public Builder() {
			this.health = Optional.empty();
			this.attack = Optional.empty();
			this.speed = Optional.empty();
			this.knockbackResist = Optional.empty();
			this.armor = Optional.empty();
			this.attackKnockback = Optional.empty();

			this.immune = Optional.empty();
			this.weak = Optional.empty();
			this.invulnerable = Optional.empty();

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
			if(attributes.knockbackResistance != null) this.knockbackResistance(attributes.knockbackResistance);
			if(attributes.armor != null) this.armor(attributes.armor);
			if(attributes.attackKnockback != null) this.attackKnockback(attributes.attackKnockback);
			if(attributes.damageImmune != null) this.immune(attributes.damageImmune);
			if(attributes.damageWeak != null) this.weak(attributes.damageWeak);
			if(attributes.invulnerable != null) this.invulnerable(attributes.invulnerable);
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
		 * @param immune the damage types to which the entity is immune
		 * @return the builder instance
		 */
		public Builder immune(final DeferredHolderSet<DamageType> immune) {
			this.immune = Optional.of(immune);
			return this;
		}

		/**
		 * @param weak the damage types to which the entity is weak
		 * @return the builder instance
		 */
		public Builder weak(final DeferredHolderSet<DamageType> weak) {
			this.weak = Optional.of(weak);
			return this;
		}

		/**
		 * @param invulnerable whether the entity is invulnerable
		 * @return the builder instance
		 */
		public Builder invulnerable(final boolean invulnerable) {
			this.invulnerable = Optional.of(invulnerable);
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
			return new Attributes(health, attack, speed, knockbackResist, armor, attackKnockback, immune, weak, invulnerable, swimAbility, sound);
		}
	}
}
