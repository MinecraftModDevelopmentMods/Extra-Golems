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
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to pick up and shoot arrows,
 * as well as load/save the arrow inventory
 **/
@Immutable
public class ShootFireballsBehavior extends AbstractShootBehavior {

	public static final Codec<ShootFireballsBehavior> CODEC = RecordCodecBuilder.create(instance -> shootCodecStart(instance)
			.apply(instance, ShootFireballsBehavior::new));

	public ShootFireballsBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, boolean consume, int attackInterval) {
		super(variant, tooltipPredicate, consume, attackInterval);
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.SHOOT_FIREBALLS.get();
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
		ItemStack itemstack = consume() ? findFirst(entity.getInventory(), this::isAmmo) : new ItemStack(Items.FIRE_CHARGE);
		if(itemstack.isEmpty()) {
			return false;
		}
		// raytrace to ensure no other creatures are in the way
		final Vec3 start = mob.position().add(0, mob.getBbHeight() * 0.55F, 0);
		final Vec3 end = target.position().add(0, mob.getBbHeight() * 0.5F, 0);
		if(!hasClearPath(entity, start, end)) {
			return false;
		}
		// create fireball
		SmallFireball fireball = new SmallFireball(mob.level(), mob, 0, 0, 0);
		fireball.setPos(mob.getX(), mob.getY(0.6F), mob.getZ());
		// determine position and distance
		final double targetY = target.getEyeY() - 1.1D;
		final double dx = target.getX() - mob.getX();
		final double dy = targetY - fireball.getY();
		final double dz = target.getZ() - mob.getZ();
		final double distance = Math.sqrt(dx * dx + dz * dz) * 0.2D;
		// shoot fireball
		fireball.shoot(dx, dy + distance, dz, 1.6F, 8.0F);
		mob.level().addFreshEntity(fireball);
		// play sound
		mob.playSound(SoundEvents.FIRECHARGE_USE, 1.0F, 0.8F + mob.getRandom().nextFloat() * 0.4F);
		// update itemstack
		itemstack.shrink(1);
		return true;
	}

	@Override
	public boolean isAmmo(ItemStack itemStack) {
		return consume() && itemStack.is(Items.FIRE_CHARGE);
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
		builder.add(Component.translatable(PREFIX + "shoot_fireballs").withStyle(ChatFormatting.RED));
		if(consume()) {
			builder.add(Component.translatable(PREFIX + "shoot.refill").withStyle(ChatFormatting.GRAY));
		}
		return builder.build();
	}
}
