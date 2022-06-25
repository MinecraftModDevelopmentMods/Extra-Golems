package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.FollowGoal;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Optional;

/**
 * This behavior allows an entity to detect nearby entities
 * of a specific type and move to follow them
 **/
@Immutable
public class FollowBehavior extends GolemBehavior {

	/**
	 * The goal priority
	 **/
	private final int priority;
	/**
	 * The entity ID of an entity to follow
	 **/
	private final ResourceLocation mobType;
	/**
	 * The description component for this behavior
	 **/
	private final Component description;

	public FollowBehavior(CompoundTag tag) {
		super(tag);
		priority = tag.getInt("priority");
		mobType = new ResourceLocation(tag.getString("entity"));
		description = new TranslatableComponent("entitytip.follow_x", new TranslatableComponent("entity." + mobType.getNamespace() + "." + mobType.getPath())).withStyle(ChatFormatting.DARK_GREEN);
	}

	public int getPriority() {
		return priority;
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		final Optional<EntityType<?>> oType = EntityType.byString(mobType.toString());
		if (oType.isPresent()) {
			final EntityType<?> type = oType.get();
			entity.goalSelector.addGoal(priority, new FollowGoal(entity, 1.0D, 4.0F, 8.0F, e -> e.getType().equals(type)));
		}
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(description);
	}
}
