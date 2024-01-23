package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.data.ExplodeBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to light a fuse, save/load the fuse,
 * and create an explosion when the fuse reaches 0
 **/
@Immutable
public class ExplodeBehavior extends Behavior {

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
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.EXPLODE.get();
	}

	//// METHODS ////

	@Override
	public void onAttack(final IExtraGolem entity, final Entity target) {
		if (target.isOnFire() || entity.asMob().getRandom().nextFloat() < chanceOnAttack) {
			entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> data.lightFuse());
		}
	}

	@Override
	public void onActuallyHurt(final IExtraGolem entity, final DamageSource source, final float amount) {
		if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || entity.asMob().getRandom().nextFloat() < chanceOnHurt) {
			entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> data.lightFuse());
		}
	}

	@Override
	public void onTick(IExtraGolem entity) {
		final PathfinderMob mob = entity.asMob();
		entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> {
			// reset fuse when wet
			if (mob.isInWaterRainOrBubble()) {
				data.resetFuseLit();
			}
			// verify fuse is lit
			if(data.isFuseLit()) {
				// send fuse particles
				((ServerLevel) mob.level()).sendParticles(ParticleTypes.SMOKE, mob.getX(), mob.getY() + mob.getEyeHeight(mob.getPose()), mob.getZ(), 2, 0.25D, 0.25D, 0.25D, 0);
				// stop navigation
				mob.getNavigation().stop();
				// update fuse
				data.updateFuse();
			}
		});
	}

	@Override
	public void onDie(final IExtraGolem entity, final DamageSource source) {
		entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> data.explode());
	}

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		final Mob mob = entity.asMob();
		final ItemStack itemstack = player.getItemInHand(hand);
		if (itemstack.is(ItemTags.CREEPER_IGNITERS)) {
			// play sound and swing hand
			final Vec3 pos = mob.position();
			SoundEvent sound = itemstack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
			mob.level().playSound(player, pos.x, pos.y, pos.z, sound, mob.getSoundSource(), 1.0F, mob.getRandom().nextFloat() * 0.4F + 0.8F);
			player.swing(hand);

			mob.setSecondsOnFire(Math.floorDiv(getMinFuse(), 20));
			entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> data.lightFuse());
			itemstack.hurtAndBreak(1, player, c -> c.broadcastBreakEvent(hand));
		}
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.explode").withStyle(ChatFormatting.RED));
	}

	///// NBT ////

	private static final String KEY_EXPLOSION_HELPER = "ExplodeData";

	@Override
	public void onWriteData(final IExtraGolem entity, final CompoundTag tag) {
		entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> tag.put(KEY_EXPLOSION_HELPER, data.serializeNBT()));

	}

	@Override
	public void onReadData(final IExtraGolem entity, final CompoundTag tag) {
		entity.getBehaviorData(ExplodeBehaviorData.class).ifPresent(data -> data.deserializeNBT(tag.getCompound(KEY_EXPLOSION_HELPER)));
	}

	//// EQUALITY ////

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ExplodeBehavior)) return false;
		ExplodeBehavior that = (ExplodeBehavior) o;
		return Double.compare(that.radius, radius) == 0 && fuse == that.fuse && Double.compare(that.chanceOnHurt, chanceOnHurt) == 0 && Double.compare(that.chanceOnAttack, chanceOnAttack) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), radius, fuse, chanceOnHurt, chanceOnAttack);
	}
}
