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
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
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
public class ShootSnowballsBehavior extends AbstractShootBehavior {

	public static final Codec<ShootSnowballsBehavior> CODEC = RecordCodecBuilder.create(instance -> shootCodecStart(instance)
			.apply(instance, ShootSnowballsBehavior::new));

	public ShootSnowballsBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, boolean consume, int attackInterval) {
		super(variant, tooltipPredicate, consume, attackInterval);
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.SHOOT_SNOWBALLS.get();
	}

	//// ABSTRACT SHOOT BEHAVIOR ////

	@Override
	protected boolean openMenu(IExtraGolem entity, ServerPlayer player) {
		// do not open menu when consume is disabled
		if(!consume()) {
			return false;
		}
		return super.openMenu(entity, player);
	}

	@Override
	protected boolean performRangedAttack(IExtraGolem entity, LivingEntity target, float distanceFactor) {
		final Mob mob = entity.asMob();
		ItemStack itemstack = consume() ? findFirst(entity.getInventory(), this::isAmmo) : new ItemStack(Items.SNOWBALL);
		if(itemstack.isEmpty()) {
			return false;
		}
		// create snowball
		final Snowball snowball = new Snowball(mob.level(), mob);
		// determine position and distance
		final double targetY = target.getEyeY() - 1.1D;
		final double dx = target.getX() - mob.getX();
		final double dy = targetY - snowball.getY();
		final double dz = target.getZ() - mob.getZ();
		final double distance = Math.sqrt(dx * dx + dz * dz) * 0.2D;
		// shoot snowball
		snowball.shoot(dx, dy + distance, dz, 1.6F, 12.0F);
		mob.level().addFreshEntity(snowball);
		// play sound
		mob.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
		// update itemstack
		itemstack.shrink(1);
		return true;
	}

	@Override
	public boolean isAmmo(ItemStack itemStack) {
		return consume() && itemStack.is(Items.SNOWBALL);
	}

	@Override
	protected boolean hasAmmo(final IExtraGolem entity) {
		if(consume()) {
			return super.hasAmmo(entity);
		}
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
	public void onRegisterGoals(final IExtraGolem entity) {
		super.onRegisterGoals(entity);
		if(consume()) {
			entity.asMob().goalSelector.addGoal(4, new MoveToItemGoal<>(entity.asMob(), 10.0D, 30, 1.0D, this.getVariantBounds()));
			entity.asMob().setCanPickUpLoot(true);
		}
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		final ImmutableList.Builder<Component> builder = ImmutableList.builder();
		builder.add(Component.translatable(PREFIX + "shoot_snowballs").withStyle(ChatFormatting.AQUA));
		if(consume()) {
			builder.add(Component.translatable(PREFIX + "shoot.refill").withStyle(ChatFormatting.GRAY));
		}
		return builder.build();
	}
}
