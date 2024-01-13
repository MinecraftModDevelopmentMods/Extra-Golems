package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
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

/**
 * This behavior allows an entity to detect nearby entities
 * of a specific type and move to follow them
 **/
@Immutable
public class FollowBehavior extends Behavior<GolemBase> {

	public static final Codec<FollowBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity").forGetter(FollowBehavior::getEntity))
			.and(Codec.INT.optionalFieldOf("priority", 2).forGetter(FollowBehavior::getPriority))
			.apply(instance, FollowBehavior::new));

	/** The entity ID of an entity to follow **/
	private final EntityType<?> entity;
	/** The goal priority **/
	private final int priority;
	/** The behavior description **/
	private final Component description;

	public FollowBehavior(MinMaxBounds.Ints variant, EntityType<?> entity, int priority) {
		super(variant);
		this.entity = entity;
		this.priority = priority;
		this.description = Component.translatable("entitytip.follow_x", entity.getDescription()).withStyle(ChatFormatting.DARK_GREEN);
	}

	//// GETTERS ////

	public EntityType<?> getEntity() {
		return entity;
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.FOLLOW.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(GolemBase entity) {
		// TODO change FollowGoal to respect Variant min max bounds
		entity.goalSelector.addGoal(this.priority, new FollowGoal(entity, 1.0D, 4.0F, 8.0F, e -> e.getType().equals(this.entity)));
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(description);
	}
}
