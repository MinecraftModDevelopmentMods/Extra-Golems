package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.data.ShootBehaviorData;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.MoveToItemGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to pick up and shoot arrows,
 * as well as load/save the arrow inventory
 **/
@Immutable
public class ShootShulkerBulletsBehavior extends AbstractShootBehavior {

	public static final Codec<ShootShulkerBulletsBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.intRange(1, 100).optionalFieldOf("attack_interval", 30).forGetter(AbstractShootBehavior::getAttackInterval))
			.apply(instance, ShootShulkerBulletsBehavior::new));


	public ShootShulkerBulletsBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, int attackInterval) {
		super(variant, tooltipPredicate, false, attackInterval);
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.SHOOT_SHULKER_BULLETS.get();
	}

	//// ABSTRACT SHOOT BEHAVIOR ////

	@Override
	protected boolean openMenu(IExtraGolem entity, ServerPlayer player) {
		return false;
	}

	@Override
	protected boolean performRangedAttack(IExtraGolem entity, LivingEntity target, float distanceFactor) {
		final Mob mob = entity.asMob();
		// create and shoot shulker bullet
		final ShulkerBullet bullet = new ShulkerBullet(mob.level(), mob, target, mob.getDirection().getAxis());
		bullet.setPos(bullet.getX(), mob.getY(0.6F), mob.getZ());
		mob.level().addFreshEntity(bullet);
		// player sound
		mob.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (mob.getRandom().nextFloat() - mob.getRandom().nextFloat()) * 0.2F + 1.0F);
		return true;
	}

	@Override
	public boolean isAmmo(ItemStack itemStack) {
		return false;
	}

	@Override
	protected boolean hasAmmo(final IExtraGolem entity) {
		return true;
	}

	//// METHODS ////

	@Override
	public void onAttachData(IExtraGolem entity) {
		final RangedAttackGoal rangedGoal = new RangedAttackGoal(entity.asMob(), 1.25D, getAttackInterval(), 14.0F);
		final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(entity.asMob(), 1.0D, true);
		entity.attachBehaviorData(new ShootBehaviorData(entity, rangedGoal, meleeGoal));
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		final ImmutableList.Builder<Component> builder = ImmutableList.builder();
		builder.add(Component.translatable(PREFIX + "shoot_shulker_bullets").withStyle(ChatFormatting.GRAY));
		return builder.build();
	}
}
