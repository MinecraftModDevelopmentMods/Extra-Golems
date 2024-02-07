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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to pick up and shoot arrows,
 * as well as load/save the arrow inventory
 **/
@Immutable
public class ShootArrowsBehavior extends AbstractShootBehavior {

	public static final Codec<ShootArrowsBehavior> CODEC = RecordCodecBuilder.create(instance -> shootCodecStart(instance)
			.and(Codec.doubleRange(0.0D, 100.0D).optionalFieldOf("damage", 2.0D).forGetter(ShootArrowsBehavior::getDamage))
			.apply(instance, ShootArrowsBehavior::new));

	/** The amount of damage dealt by arrows **/
	private final double damage;

	public ShootArrowsBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, boolean consume, int attackInterval, double damage) {
		super(variant, tooltipPredicate, consume, attackInterval);
		this.damage = damage;
	}

	//// GETTERS ////

	public double getDamage() {
		return damage;
	}

	public double getDamage(final IExtraGolem entity) {
		double amount = this.damage;
		if(entity.asMob().isBaby()) {
			amount *= 0.5D;
		}
		return amount;
	}

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.SHOOT_ARROWS.get();
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
		ItemStack itemstack = consume() ? findFirst(entity.getInventory(), this::isAmmo) : new ItemStack(Items.ARROW);
		if(itemstack.isEmpty()) {
			return false;
		}
		// raytrace to ensure no other creatures are in the way
		final Vec3 start = mob.position().add(0, mob.getBbHeight() * 0.55F, 0);
		final Vec3 end = target.position().add(0, mob.getBbHeight() * 0.5F, 0);
		if(!hasClearPath(entity, start, end)) {
			return false;
		}
		// make an arrow out of the inventory
		AbstractArrow arrow = ProjectileUtil.getMobArrow(mob, itemstack, distanceFactor);
		arrow.setPos(mob.getX(), mob.getY() + mob.getBbHeight() * 0.55F, mob.getZ());
		double d0 = target.getX() - mob.getX();
		double d1 = target.getY(1.0D / 3.0D) - arrow.getY();
		double d2 = target.getZ() - mob.getZ();
		double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		// set location and attributes
		arrow.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - mob.level().getDifficulty().getId() * 4));
		arrow.pickup = AbstractArrow.Pickup.ALLOWED;
		arrow.setOwner(mob);
		arrow.setBaseDamage(this.getDamage(entity));
		// play sound and add arrow to world
		mob.level().addFreshEntity(arrow);
		// play sound
		mob.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
		// update itemstack
		itemstack.shrink(1);
		return true;
	}

	@Override
	public boolean isAmmo(ItemStack itemStack) {
		return consume() && itemStack.getItem() instanceof ArrowItem;
	}

	//// METHODS ////

	@Override
	public void onAttachData(IExtraGolem entity) {
		final RangedAttackGoal rangedGoal = new RangedAttackGoal(entity.asMob(), 1.0D, getAttackInterval(), 32.0F);
		final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(entity.asMob(), 1.0D, true);
		entity.attachBehaviorData(new ShootBehaviorData(entity, rangedGoal, meleeGoal));
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		final ImmutableList.Builder<Component> builder = ImmutableList.builder();
		builder.add(Component.translatable(PREFIX + "shoot_arrows").withStyle(ChatFormatting.LIGHT_PURPLE));
		if(consume()) {
			builder.add(Component.translatable(PREFIX + "shoot.refill").withStyle(ChatFormatting.GRAY));
		}
		return builder.build();
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ShootArrowsBehavior)) return false;
		if (!super.equals(o)) return false;
		ShootArrowsBehavior that = (ShootArrowsBehavior) o;
		return Double.compare(that.damage, damage) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), damage);
	}
}
