package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.container.behavior.GolemBehavior;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.MoveToItemGoal;
import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * This behavior allows an entity to pick up and shoot arrows,
 * as well as load/save the arrow inventory
 **/
@Immutable
public class ShootArrowsBehavior extends Behavior<GolemBase> {

	public static final Codec<ShootArrowsBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.doubleRange(0.0D, 100.0D).optionalFieldOf("damage", 2.0D).forGetter(ShootArrowsBehavior::getDamage))
			.apply(instance, ShootArrowsBehavior::new));

	/** The amount of damage dealt by arrows **/
	private final double damage;
	/** The follow range modifier to allow the entity to detect enemies from a distance **/
	protected final AttributeModifier rangeModifier = new AttributeModifier("Ranged attack bonus", 2.0F, AttributeModifier.Operation.MULTIPLY_TOTAL);

	public ShootArrowsBehavior(MinMaxBounds.Ints variant, double damage) {
		super(variant);
		this.damage = damage;
	}

	//// GETTERS ////

	public double getDamage() {
		return damage;
	}

	@Override
	public Codec<? extends Behavior<?>> getCodec() {
		return EGRegistry.BehaviorReg.SHOOT_ARROWS.get();
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// modify follow range
		AttributeInstance followRange = entity.getAttribute(Attributes.FOLLOW_RANGE);
		if(!followRange.hasModifier(rangeModifier)) {
			followRange.addPermanentModifier(rangeModifier);
		}
		// TODO adjust goal to take variant into account
		entity.goalSelector.addGoal(4, new MoveToItemGoal(entity, 10.0D, 30, 1.0D));
		entity.setCanPickUpLoot(true);
	}

	@Override
	public void onActuallyHurt(final GolemBase entity, final DamageSource source, final float amount) {
		// if it's an arrow or other projectile, set the attacker as revenge target
		if (source.isIndirect() && source.getEntity() instanceof LivingEntity) {
			entity.setTarget((LivingEntity) source.getEntity());
		}
		final boolean forceMelee = (entity.getTarget() != null && entity.getTarget().position().closerThan(entity.position(), 8.0D));
		entity.updateCombatTask(forceMelee);
	}

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && player instanceof ServerPlayer) {
			// update menu player
			if(entity.getPlayerInMenu() != null) {
				entity.getPlayerInMenu().closeContainer();
			}
			entity.setPlayerInMenu(player);
			// open dispenser GUI by sending request to server
			NetworkHooks.openScreen((ServerPlayer) player, new PortableDispenserMenu.Provider(entity.getInventory()));
			player.swing(hand);
		}
	}

	@Override
	public void onTick(GolemBase entity) {
		if(null == entity.getPlayerInMenu()) {
			return;
		}
		// stop moving and look at player
		entity.getNavigation().stop();
		entity.getLookControl().setLookAt(entity.getPlayerInMenu());
		// ensure the container closes when the player is too far away
		if(!entity.isPlayerInRangeForMenu(8.0D)) {
			entity.getPlayerInMenu().closeContainer();
			entity.setPlayerInMenu(null);
		}
	}

	@Override
	public void onWriteData(final GolemBase entity, final CompoundTag tag) {
		entity.writeInventoryToTag(tag);
	}

	@Override
	public void onReadData(final GolemBase entity, final CompoundTag tag) {
		entity.readInventoryFromTag(tag);
	}

	@Override
	public List<Component> createDescriptions() {
		return ImmutableList.of(Component.translatable("entitytip.shoot_arrows").withStyle(ChatFormatting.LIGHT_PURPLE),
				Component.translatable("entitytip.click_refill").withStyle(ChatFormatting.GRAY));
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
