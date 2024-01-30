package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.FollowGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to detect nearby entities
 * of a specific type and move to follow them
 **/
@Immutable
public class FollowBehavior extends Behavior {

	public static final Codec<FollowBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity").forGetter(FollowBehavior::getEntity))
			.and(Codec.INT.optionalFieldOf("priority", 2).forGetter(FollowBehavior::getPriority))
			.apply(instance, FollowBehavior::new));

	/** The entity ID of an entity to follow **/
	private final EntityType<?> entity;
	/** The goal priority **/
	private final int priority;

	public FollowBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, EntityType<?> entity, int priority) {
		super(variant, tooltipPredicate);
		this.entity = entity;
		this.priority = priority;
	}

	//// GETTERS ////

	public EntityType<?> getEntity() {
		return entity;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.FOLLOW.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(IExtraGolem entity) {
		entity.asMob().goalSelector.addGoal(this.priority, new FollowGoal<>(entity.asMob(), 1.0D, 4.0F, 8.0F, e -> e.getType().equals(this.entity), getVariantBounds()));
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.follow_x", entity.getDescription()).withStyle(ChatFormatting.DARK_GREEN));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof FollowBehavior)) return false;
		if (!super.equals(o)) return false;
		FollowBehavior that = (FollowBehavior) o;
		return priority == that.priority && entity.equals(that.entity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), entity, priority);
	}
}
