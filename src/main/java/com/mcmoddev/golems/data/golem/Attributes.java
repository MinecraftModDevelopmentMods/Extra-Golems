package com.mcmoddev.golems.data.golem;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.DeferredHolderSet;
import com.mcmoddev.golems.util.SoundTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Immutable
public class Attributes {
	
	/**
	 * Attribute settings to apply before the container is officially loaded.
	 * Health value is very high to ensure it does not interfere with actual health.
	 **/
	public static final Attributes EMPTY = new Attributes(Optional.of(1024.0D), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty());

	public static final Codec<Attributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.doubleRange(0, 1024.0D).optionalFieldOf("health").forGetter(o -> Optional.ofNullable(o.health)),
			Codec.doubleRange(0, 2048.0D).optionalFieldOf("attack").forGetter(o -> Optional.ofNullable(o.attack)),
			Codec.doubleRange(0, 8.0D).optionalFieldOf("speed").forGetter(o -> Optional.ofNullable(o.speed)),
			Codec.doubleRange(0, 1.0D).optionalFieldOf("knockback_resistance").forGetter(o -> Optional.ofNullable(o.knockbackResistance)),
			Codec.doubleRange(0, 30.0D).optionalFieldOf("armor").forGetter(o -> Optional.ofNullable(o.armor)),
			Codec.doubleRange(0, 2.0D).optionalFieldOf("attack_knockback").forGetter(o -> Optional.ofNullable(o.attackKnockback)),
			DeferredHolderSet.codec(Registries.MOB_EFFECT).optionalFieldOf("ignore").forGetter(o -> Optional.ofNullable(o.potionIgnore)),
			DeferredHolderSet.codec(Registries.DAMAGE_TYPE).optionalFieldOf("immune").forGetter(o -> Optional.ofNullable(o.damageImmune)),
			DeferredHolderSet.codec(Registries.DAMAGE_TYPE).optionalFieldOf("weak").forGetter(o -> Optional.ofNullable(o.damageWeak)),
			Codec.BOOL.optionalFieldOf("invulnerable").forGetter(o -> Optional.ofNullable(o.invulnerable)),
			Codec.BOOL.optionalFieldOf("occludes").forGetter(o -> Optional.ofNullable(o.occludes)),
			SwimAbility.CODEC.optionalFieldOf("swim_ability").forGetter(o -> Optional.ofNullable(o.swimAbility)),
			SoundTypeRegistry.CODEC.optionalFieldOf("sound").forGetter(o -> Optional.ofNullable(o.sound))
	).apply(instance, Attributes::new));

	private final @Nullable Double health;
	private final @Nullable Double attack;
	private final @Nullable Double speed;
	private final @Nullable Double knockbackResistance;
	private final @Nullable Double armor;
	private final @Nullable Double attackKnockback;

	private final @Nullable DeferredHolderSet<MobEffect> potionIgnore;
	private final @Nullable DeferredHolderSet<DamageType> damageImmune;
	private final @Nullable DeferredHolderSet<DamageType> damageWeak;
	private final @Nullable Boolean invulnerable;
	private final @Nullable Boolean occludes;

	private final @Nullable SwimAbility swimAbility;
	private final @Nullable SoundType sound;

	private Attributes(Optional<Double> health, Optional<Double> attack, Optional<Double> speed, Optional<Double> knockbackResistance, Optional<Double> armor,
					   Optional<Double> attackKnockback, Optional<DeferredHolderSet<MobEffect>> potionIgnore,
					   Optional<DeferredHolderSet<DamageType>> damageImmune, Optional<DeferredHolderSet<DamageType>> damageWeak,
					   Optional<Boolean> invulnerable, Optional<Boolean> occludes, Optional<SwimAbility> swimAbility, Optional<SoundType> sound) {
		this.health = health.orElse(null);
		this.attack = attack.orElse(null);
		this.speed = speed.orElse(null);
		this.knockbackResistance = knockbackResistance.orElse(null);
		this.armor = armor.orElse(null);
		this.attackKnockback = attackKnockback.orElse(null);
		this.potionIgnore = potionIgnore.orElse(null);
		this.damageImmune = damageImmune.orElse(null);
		this.damageWeak = damageWeak.orElse(null);
		this.invulnerable = invulnerable.orElse(null);
		this.occludes = occludes.orElse(null);
		this.swimAbility = swimAbility.orElse(null);
		this.sound = sound.orElse(null);
	}

	/** @return a new attribute supplier builder **/
	public Supplier<AttributeSupplier.Builder> getAttributeSupplier() {
		return () -> GolemBase.createMobAttributes()
				.add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, this.getHealth())
				.add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, this.getSpeed())
				.add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE, this.getKnockbackResistance())
				.add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_KNOCKBACK, this.getAttackKnockback())
				.add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, this.getArmor())
				.add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, this.getAttack());
	}

	//// GETTERS ////

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

	public boolean ignores(final RegistryAccess registryAccess, final MobEffect mobEffect) {
		if(null == potionIgnore) {
			return false;
		}
		// resolve registry and holder set
		final Registry<MobEffect> registry = registryAccess.registryOrThrow(Registries.MOB_EFFECT);
		final HolderSet<MobEffect> ignore = potionIgnore.get(registry);
		final Holder<MobEffect> holder = registry.wrapAsHolder(mobEffect);
		return ignore.contains(holder);
	}

	/**
	 * @param registryAccess the registry access
	 * @param damageTypes one or more damage types
	 * @return true if the Golem is immune to any of the given damage types
	 */
	public boolean isImmuneTo(final RegistryAccess registryAccess, final ResourceKey<DamageType>... damageTypes) {
		if(null == damageImmune) {
			return false;
		}
		// resolve registry and holder set
		final Registry<DamageType> registry = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
		final HolderSet<DamageType> immune = damageImmune.get(registry);
		// iterate damage types until one is found in the set
		for(ResourceKey<DamageType> key : damageTypes) {
			Holder<DamageType> holder = registry.getHolderOrThrow(key);
			if(immune.contains(holder)) {
				return true;
			}
		}
		// no checks passed
		return false;
	}

	/**
	 * @param registryAccess the registry access
	 * @param damageTypes one or more damage types
	 * @return true if the Golem is weak to any of the given damage types
	 */
	public boolean isWeakTo(final RegistryAccess registryAccess, final ResourceKey<DamageType>... damageTypes) {
		if(null == damageWeak) {
			return false;
		}
		// resolve registry and holder set
		final Registry<DamageType> registry = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
		final HolderSet<DamageType> weak = damageWeak.get(registry);
		// iterate damage types until one is found in the set
		for(ResourceKey<DamageType> key : damageTypes) {
			Holder<DamageType> holder = registry.getHolderOrThrow(key);
			if(weak.contains(holder)) {
				return true;
			}
		}
		// no checks passed
		return false;
	}

	public DeferredHolderSet<MobEffect> getPotionIgnore() {
		return potionIgnore != null ? potionIgnore : DeferredHolderSet.empty();
	}

	public DeferredHolderSet<DamageType> getDamageImmune() {
		return damageImmune != null ? damageImmune : DeferredHolderSet.empty();
	}

	public DeferredHolderSet<DamageType> getDamageWeak() {
		return damageWeak != null ? damageWeak : DeferredHolderSet.empty();
	}

	/** @return {@code true} if the entity is invulnerable **/
	public boolean isInvulnerable() {
		return invulnerable != null && invulnerable;
	}

	/** @return {@code true} if the entity occludes vibrations **/
	public boolean occludes() {
		return occludes != null && occludes;
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

	//// HELPER METHODS ////

	public void updatePathfinding(final PathfinderMob mob) {
		final RegistryAccess registryAccess = mob.level().registryAccess();
		// water damage
		if(isWeakTo(registryAccess, DamageTypes.DROWN)) {
			mob.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
		} else {
			mob.setPathfindingMalus(BlockPathTypes.WATER, 8.0F);
		}
		// fire damage
		if(isImmuneTo(registryAccess, DamageTypes.IN_FIRE, DamageTypes.ON_FIRE)) {
			mob.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
			mob.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
			mob.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
		} else {
			mob.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
			mob.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
			mob.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
		}
	}

	public void onAddDescriptions(final GolemContainer container, final RegistryAccess registryAccess, final List<Component> list, final TooltipFlag tooltipFlag) {
		final String PREFIX = "golem.description.";
		// add health description
		list.add(Component.translatable(PREFIX + "health").append(": ").withStyle(ChatFormatting.GRAY)
				.append(Component.literal(String.format("%.1f", health)).withStyle(ChatFormatting.BLACK))
				.append(Component.literal(" \u2764").withStyle(ChatFormatting.DARK_RED)));
		// add attack description
		list.add(Component.translatable(PREFIX + "attack").append(": ").withStyle(ChatFormatting.GRAY)
				.append(Component.literal(String.format("%.1f", attack)).withStyle(ChatFormatting.BLACK))
				.append(Component.literal(" \u2694")));
		// add "fireproof" description
		if (isImmuneTo(registryAccess, DamageTypes.IN_FIRE, DamageTypes.ON_FIRE)) {
			list.add(Component.translatable("enchantment.minecraft.fire_protection").withStyle(ChatFormatting.GOLD));
		}
		// add "explosion-proof" description
		if (isImmuneTo(registryAccess, DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION)) {
			list.add(Component.translatable("enchantment.minecraft.blast_protection").withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD));
		}
		// TODO add "potion ignore" description
		// add "invulnerable" description
		if(isInvulnerable()) {
			list.add(Component.translatable(PREFIX + "invulnerable").withStyle(ChatFormatting.BOLD));
		}
		// TODO add "occludes" description
		// add "knockback" description
		if (getAttackKnockback() > 0.39D) {
			list.add(Component.translatable(PREFIX + "knockback").withStyle(ChatFormatting.DARK_RED));
		}
		// add "advanced swimmer" description
		if (swimAbility == SwimAbility.SWIM) {
			list.add(Component.translatable(PREFIX + "swim").withStyle(ChatFormatting.DARK_AQUA));
		}
	}


	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Attributes)) return false;
		Attributes other = (Attributes) o;
		return Objects.equals(health, other.health) && Objects.equals(attack, other.attack) && Objects.equals(speed, other.speed)
				&& Objects.equals(knockbackResistance, other.knockbackResistance) && Objects.equals(armor, other.armor)
				&& Objects.equals(attackKnockback, other.attackKnockback) && Objects.equals(potionIgnore, other.potionIgnore)
				&& Objects.equals(damageImmune, other.damageImmune) && Objects.equals(damageWeak, other.damageWeak)
				&& Objects.equals(invulnerable, other.invulnerable) && Objects.equals(occludes, other.occludes)
				&& swimAbility == other.swimAbility && Objects.equals(sound, other.sound);
	}

	@Override
	public int hashCode() {
		return Objects.hash(health, attack, speed, knockbackResistance, armor, attackKnockback, potionIgnore, damageImmune, damageWeak, invulnerable, occludes, swimAbility, sound);
	}

	//// CLASSES ////

	public static class Builder {
		private Optional<Double> health;
		private Optional<Double> attack;
		private Optional<Double> speed;
		private Optional<Double> knockbackResist;
		private Optional<Double> armor;
		private Optional<Double> attackKnockback;

		private Optional<DeferredHolderSet<MobEffect>> ignore;
		private Optional<DeferredHolderSet<DamageType>> immune;
		private Optional<DeferredHolderSet<DamageType>> weak;
		private Optional<Boolean> invulnerable;
		private Optional<Boolean> occludes;

		private Optional<SwimAbility> swimAbility;
		private Optional<SoundType> sound;

		public Builder() {
			this.health = Optional.empty();
			this.attack = Optional.empty();
			this.speed = Optional.empty();
			this.knockbackResist = Optional.empty();
			this.armor = Optional.empty();
			this.attackKnockback = Optional.empty();

			this.ignore = Optional.empty();
			this.immune = Optional.empty();
			this.weak = Optional.empty();
			this.invulnerable = Optional.empty();
			this.occludes = Optional.empty();

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
			if(attributes.potionIgnore != null) this.ignore(attributes.potionIgnore);
			if(attributes.damageImmune != null) this.immune(attributes.damageImmune);
			if(attributes.damageWeak != null) this.weak(attributes.damageWeak);
			if(attributes.invulnerable != null) this.invulnerable(attributes.invulnerable);
			if(attributes.occludes != null) this.occludes(attributes.occludes);
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
		 * @param ignore the mob effects to which the entity is immune
		 * @return the builder instance
		 */
		public Builder ignore(final DeferredHolderSet<MobEffect> ignore) {
			this.ignore = Optional.of(ignore);
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
		 * @param occludes whether the entity occludes sounds
		 * @return the builder instance
		 */
		public Builder occludes(final boolean occludes) {
			this.occludes = Optional.of(occludes);
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
			return new Attributes(health, attack, speed, knockbackResist, armor, attackKnockback, ignore, immune, weak, invulnerable, occludes, swimAbility, sound);
		}
	}
}
