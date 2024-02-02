package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.behavior.util.TargetType;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.GolemPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to summon an entity under specific conditions
 **/
@Immutable
public class SummonBehavior extends Behavior {
	
	public static final Codec<SummonBehavior> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Behavior::getVariantBounds),
			TooltipPredicate.CODEC.optionalFieldOf("tooltip", TooltipPredicate.NORMAL).forGetter(Behavior::getTooltipPredicate),
			ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity").forGetter(SummonBehavior::getEntity),
			Codec.STRING.optionalFieldOf("display_name").forGetter(o -> Optional.ofNullable(o.displayNameKey)),
			Codec.STRING.optionalFieldOf("nbt", "{}").forGetter(SummonBehavior::getNbt),
			Codec.intRange(0, 255).optionalFieldOf("amount", 1).forGetter(SummonBehavior::getAmount),
			TargetType.SELF_OR_ENEMY_CODEC.optionalFieldOf("position", TargetType.SELF).forGetter(SummonBehavior::getPosition),
			Codec.doubleRange(0.0D, 128.0D).optionalFieldOf("radius", 0.0D).forGetter(SummonBehavior::getRadius),
			TriggerType.CODEC.fieldOf("trigger").forGetter(SummonBehavior::getTrigger),
			EGCodecUtils.listOrElementCodec(GolemPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(GolemPredicate.ALWAYS)).forGetter(SummonBehavior::getPredicates),
			Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(SummonBehavior::getChance)
	).apply(instance, SummonBehavior::new));

	/** The entity ID of an entity to spawn **/
	private final EntityType<?> entity;
	/** The translation key for the display name of the entity **/
	private final @Nullable String displayNameKey;
	/** The entity NBT **/
	private final String nbt;
	/** The entity NBT as a compound tag **/
	private final CompoundTag compoundTag;
	/** The number of entities to spawn **/
	private final int amount;
	/** The position to summon the entity **/
	private final TargetType position;
	/** The radius to offset the summon position **/
	private final double radius;
	/** The trigger to summon the entity **/
	private final TriggerType trigger;
	/** The conditions to summon the entity **/
	private final List<GolemPredicate> predicates;
	/** The conditions to summon the entity as a single predicate **/
	private final Predicate<IExtraGolem> predicate;
	/** The percent chance [0,1] to apply **/
	private final double chance;

	public SummonBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, EntityType<?> entity, Optional<String> displayNameKey, String nbt, int amount, TargetType position, double radius, TriggerType trigger, List<GolemPredicate> predicates, double chance) {
		super(variant, tooltipPredicate);
		this.entity = entity;
		this.displayNameKey = displayNameKey.orElse(null);
		this.nbt = nbt;
		this.amount = amount;
		this.position = position;
		this.radius = radius;
		this.trigger = trigger;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.chance = chance;
		CompoundTag tag;
		try {
			tag = TagParser.parseTag(this.nbt);
			tag.putString("id", ForgeRegistries.ENTITY_TYPES.getKey(this.entity).toString());
		} catch (CommandSyntaxException e) {
			ExtraGolems.LOGGER.error(this.getClass().getSimpleName() + " failed to parse NBT from '" + this.nbt + "'");
			tag = new CompoundTag();
		}
		this.compoundTag = tag;
	}

	//// GETTERS ////

	public EntityType<?> getEntity() {
		return entity;
	}

	@Nullable
	public String getDisplayNameKey() {
		return displayNameKey;
	}

	public String getNbt() {
		return nbt;
	}

	public CompoundTag getCompoundTag() {
		return this.compoundTag.copy();
	}

	public int getAmount() {
		return amount;
	}

	public TargetType getPosition() {
		return position;
	}

	public double getRadius() {
		return radius;
	}

	public TriggerType getTrigger() {
		return trigger;
	}

	public List<GolemPredicate> getPredicates() {
		return predicates;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.SUMMON.get();
	}

	//// METHODS ////

	@Override
	public void onActuallyHurt(IExtraGolem entity, DamageSource source, float amount) {
		if(this.trigger == TriggerType.HURT) {
			summonEntity(entity.asMob());
		}
	}

	@Override
	public void onAttack(IExtraGolem entity, Entity target) {
		if(this.trigger == TriggerType.ATTACK) {
			summonEntity(entity.asMob());
		}
	}

	@Override
	public void onTick(IExtraGolem entity) {
		if(this.trigger == TriggerType.TICK) {
			summonEntity(entity.asMob());
		}
	}

	@Override
	public void onStruckByLightning(IExtraGolem entity, LightningBolt lightningBolt) {
		if(this.trigger == TriggerType.LIGHTNING) {
			summonEntity(entity.asMob());
		}
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		// create predicate text, if any
		final Optional<Component> predicateText = createTriggerAndPredicateDescription(trigger, predicates);

		// resolve entity name
		final Component name;
		if(displayNameKey != null && !displayNameKey.isEmpty()) {
			name = Component.translatable(displayNameKey);
		} else {
			name = entity.getDescription();
		}

		// create description
		if(predicateText.isPresent()) {
			return ImmutableList.of(Component.translatable(PREFIX + "summon.predicate", name, predicateText.get()));
		}
		return ImmutableList.of(Component.translatable(PREFIX + "summon", name));
	}

	protected boolean summonEntity(final GolemBase self) {
		if(!this.predicate.test(self)) {
			return false;
		}
		if(!(self.getRandom().nextDouble() < chance)) {
			return false;
		}
		// determine spawn location
		final @Nullable LivingEntity target = self.getTarget();
		final Vec3 pos = (this.position == TargetType.ENEMY && target != null) ? target.position() : self.position();
		final ServerLevel level = (ServerLevel) self.level();
		// spawn entity
		for(int i = 0; i < amount; i++) {
			final CompoundTag tag = compoundTag.copy();
			EntityType.create(tag, self.level()).ifPresent(e -> {
				// randomize position
				e.setPos(pos.add((self.getRandom().nextDouble() - 0.5F) * 2.0F * radius,
						self.getRandom().nextDouble() * (radius / 4.0F),
						(self.getRandom().nextDouble() - 0.5F) * 2.0F * radius));
				// spawn entity
				level.addFreshEntityWithPassengers(e);
				// post process
				if(e instanceof Mob mob) {
					ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), MobSpawnType.MOB_SUMMONED, null, null);
					mob.setTarget(target);
				}
				if(target != null && e instanceof NeutralMob mob) {
					mob.setPersistentAngerTarget(target.getUUID());
					mob.startPersistentAngerTimer();
				}
			});
		}
		return amount > 0;
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SummonBehavior)) return false;
		if (!super.equals(o)) return false;
		SummonBehavior that = (SummonBehavior) o;
		return amount == that.amount && Double.compare(that.chance, chance) == 0 && entity.equals(that.entity) && nbt.equals(that.nbt) && position == that.position && trigger == that.trigger && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), entity, nbt, amount, position, trigger, predicates, chance);
	}
}
