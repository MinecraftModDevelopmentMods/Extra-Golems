package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ExplodeGoal;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to light a fuse, save/load the fuse,
 * and create an explosion when the fuse reaches 0
 **/
@Immutable
public class ExplodeBehavior extends Behavior<GolemBase> {

	public static final Codec<ExplodeBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.doubleRange(0.0D, 127.0D).optionalFieldOf("radius", 2.0D).forGetter(ExplodeBehavior::getRadius))
			.and(Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("fuse", 60).forGetter(ExplodeBehavior::getMinFuse))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance_on_hurt", 0.0D).forGetter(ExplodeBehavior::getChanceOnHurt))
			.and(Codec.doubleRange(0.0D, 1.0D).optionalFieldOf("chance_on_attack", 0.0D).forGetter(ExplodeBehavior::getChanceOnAttack))
			.apply(instance, ExplodeBehavior::new));

	/** The radius of the explosion **/
	protected final double radius;
	/** The minimum length of the fuse **/
	protected final int fuse;
	/** The percent chance [0,1] to apply when the entity is hurt */
	protected final double chanceOnHurt;
	/** The percent chance [0,1] to apply when the entity attacks **/
	protected final double chanceOnAttack;

	public ExplodeBehavior(MinMaxBounds.Ints variant, double radius, int fuse, double chanceOnHurt, double chanceOnAttack) {
		super(variant);
		this.radius = radius;
		this.fuse = fuse;
		this.chanceOnHurt = chanceOnHurt;
		this.chanceOnAttack = chanceOnAttack;
	}

	//// GETTERS ////

	public double getRadius() {
		return radius;
	}

	public int getMinFuse() {
		return fuse;
	}

	public double getChanceOnHurt() {
		return chanceOnHurt;
	}

	public double getChanceOnAttack() {
		return chanceOnAttack;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.EXPLODE.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// TODO add variant to goal
		entity.goalSelector.addGoal(0, new ExplodeGoal<>(entity, (float) radius));
	}

	@Override
	public void onHurtTarget(final GolemBase entity, final Entity target) {
		if (target.isOnFire() || entity.getRandom().nextFloat() < chanceOnAttack) {
			entity.lightFuse();
		}
	}

	@Override
	public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
		if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || entity.getRandom().nextFloat() < chanceOnHurt) {
			entity.lightFuse();
		}
	}

	@Override
	public void onDie(final GolemBase entity, final DamageSource source) {
		entity.explode((float) radius);
	}

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		final ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
			// play sound and swing hand
			final Vec3 pos = entity.position();
			SoundEvent sound = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
			entity.level().playSound(player, pos.x, pos.y, pos.z, sound, entity.getSoundSource(), 1.0F,
					entity.getRandom().nextFloat() * 0.4F + 0.8F);
			player.swing(hand);

			entity.setSecondsOnFire(Math.floorDiv(getMinFuse(), 20));
			entity.lightFuse();
			itemstack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
		}
	}

	@Override
	public void onWriteData(final GolemBase entity, final CompoundTag tag) {
		entity.saveFuse(tag);
	}

	@Override
	public void onReadData(final GolemBase entity, final CompoundTag tag) {
		entity.loadFuse(tag);
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.explode").withStyle(ChatFormatting.RED));
	}
}
