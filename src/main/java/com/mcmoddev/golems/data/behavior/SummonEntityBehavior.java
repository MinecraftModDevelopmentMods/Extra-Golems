package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.behavior.util.TargetType;
import com.mcmoddev.golems.data.behavior.util.TriggerType;
import com.mcmoddev.golems.data.behavior.util.WorldPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This behavior allows an entity to summon an entity under specific conditions
 **/
@Immutable
public class SummonEntityBehavior extends Behavior<GolemBase> {

	public static final Codec<SummonEntityBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity").forGetter(SummonEntityBehavior::getEntity))
			.and(Codec.STRING.optionalFieldOf("nbt", "{}").forGetter(SummonEntityBehavior::getNbt))
			.and(Codec.intRange(0, 255).optionalFieldOf("amount", 1).forGetter(SummonEntityBehavior::getAmount))
			.and(TargetType.SELF_OR_ENEMY_CODEC.optionalFieldOf("position", TargetType.SELF).forGetter(SummonEntityBehavior::getPosition))
			.and(TriggerType.CODEC.fieldOf("trigger").forGetter(SummonEntityBehavior::getTrigger))
			.and(EGCodecUtils.listOrElementCodec(WorldPredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of(WorldPredicate.ALWAYS)).forGetter(SummonEntityBehavior::getPredicates))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance", 1.0D).forGetter(SummonEntityBehavior::getChance))
			.apply(instance, SummonEntityBehavior::new));

	/** The entity ID of an entity to spawn **/
	private final EntityType<?> entity;
	/** The entity NBT **/
	private final String nbt;
	/** The entity NBT as a compound tag **/
	private final CompoundTag compoundTag;
	/** The number of entities to spawn **/
	private final int amount;
	/** The position to summon the entity **/
	private final TargetType position;
	/** The trigger to summon the entity **/
	private final TriggerType trigger;
	/** The conditions to summon the entity **/
	private final List<WorldPredicate> predicates;
	/** The conditions to summon the entity as a single predicate **/
	private final Predicate<GolemBase> predicate;
	/** The percent chance [0,1] to apply **/
	private final double chance;

	public SummonEntityBehavior(MinMaxBounds.Ints variant, EntityType<?> entity, String nbt, int amount, TargetType position, TriggerType trigger, List<WorldPredicate> predicates, double chance) {
		super(variant);
		this.entity = entity;
		this.nbt = nbt;
		this.amount = amount;
		this.position = position;
		this.trigger = trigger;
		this.predicates = predicates;
		this.predicate = PredicateUtils.and(predicates);
		this.chance = chance;
		CompoundTag tag;
		try {
			tag = TagParser.parseTag(this.nbt);
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

	public String getNbt() {
		return nbt;
	}

	public CompoundTag getCompoundTag() {
		return this.compoundTag;
	}

	public int getAmount() {
		return amount;
	}

	public TargetType getPosition() {
		return position;
	}

	public TriggerType getTrigger() {
		return trigger;
	}

	public List<WorldPredicate> getPredicates() {
		return predicates;
	}

	public double getChance() {
		return chance;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.SUMMON.get();
	}

	//// METHODS ////

	@Override
	public void onActuallyHurt(GolemBase entity, DamageSource source, float amount) {
		if(this.trigger == TriggerType.HURT) {
			summonEntity(entity);
		}
	}

	@Override
	public void onHurtTarget(GolemBase entity, Entity target) {
		if(this.trigger == TriggerType.ATTACK) {
			summonEntity(entity);
		}
	}

	@Override
	public void onTick(GolemBase entity) {
		if(this.trigger == TriggerType.TICK) {
			summonEntity(entity);
		}
	}

	@Override
	public List<Component> createDescriptions() {
		final Component description = Component.empty(); // TODO description
		return ImmutableList.of(description);
	}

	public boolean summonEntity(final GolemBase self) {
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
			EntityType.create(compoundTag, self.level()).ifPresent(e -> {
				// read entity from compound tag
				e.load(compoundTag.copy());
				e.setPos(pos);
				// spawn entity
				level.addFreshEntityWithPassengers(e);
				// post process
				if(e instanceof Mob mob) {
					mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(pos)), MobSpawnType.MOB_SUMMONED, null, null);
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
		if (!(o instanceof SummonEntityBehavior)) return false;
		if (!super.equals(o)) return false;
		SummonEntityBehavior that = (SummonEntityBehavior) o;
		return amount == that.amount && Double.compare(that.chance, chance) == 0 && entity.equals(that.entity) && nbt.equals(that.nbt) && position == that.position && trigger == that.trigger && predicates.equals(that.predicates);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), entity, nbt, amount, position, trigger, predicates, chance);
	}
}
