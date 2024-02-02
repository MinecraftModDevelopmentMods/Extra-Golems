package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.data.ShootArrowsBehaviorData;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.MoveToItemGoal;
import com.mcmoddev.golems.menu.PortableDispenserMenu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This behavior allows an entity to pick up and shoot arrows,
 * as well as load/save the arrow inventory
 **/
@Immutable
public class ShootArrowsBehavior extends Behavior {

	public static final Codec<ShootArrowsBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.and(Codec.doubleRange(0.0D, 100.0D).optionalFieldOf("damage", 2.0D).forGetter(ShootArrowsBehavior::getDamage))
			.apply(instance, ShootArrowsBehavior::new));

	protected static final EntityDataAccessor<Integer> ARROWS = SynchedEntityData.defineId(AbstractGolem.class, EntityDataSerializers.INT);

	/** The amount of damage dealt by arrows **/
	private final double damage;
	/** The follow range modifier to allow the entity to detect enemies from a distance **/
	protected final AttributeModifier rangeModifier = new AttributeModifier("Ranged follow bonus", 8.0F, AttributeModifier.Operation.ADDITION);

	// TODO make other shoot behaviors (fireball, snowball)

	public ShootArrowsBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, double damage) {
		super(variant, tooltipPredicate);
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

	//// METHODS ////


	@Override
	public void onAttachData(IExtraGolem entity) {
		final RangedAttackGoal rangedGoal = new RangedAttackGoal(entity.asMob(), 1.0D, 28, 32.0F);
		final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(entity.asMob(), 1.0D, true);
		entity.attachBehaviorData(new ShootArrowsBehaviorData(entity, ARROWS, rangedGoal, meleeGoal));
	}

	@Override
	public void onRegisterSynchedData(IExtraGolem entity) {
		defineSynchedData(entity.asMob().getEntityData(), ARROWS, 0);
	}

	@Override
	public void onRegisterGoals(final IExtraGolem entity) {
		final Mob mob = entity.asMob();
		// modify follow range
		AttributeInstance followRange = mob.getAttribute(Attributes.FOLLOW_RANGE);
		if(!followRange.hasModifier(rangeModifier)) {
			followRange.addPermanentModifier(rangeModifier);
		}
		// TODO adjust goal to take variant into account
		mob.goalSelector.addGoal(4, new MoveToItemGoal(mob, 10.0D, 30, 1.0D));
		mob.setCanPickUpLoot(true);
	}

	@Override
	public void onActuallyHurt(final IExtraGolem entity, final DamageSource source, final float amount) {
		final Mob mob = entity.asMob();
		// if it's an arrow or other projectile, set the attacker as revenge target
		if (source.isIndirect() && source.getEntity() instanceof LivingEntity) {
			mob.setTarget((LivingEntity) source.getEntity());
		}
		final boolean forceMelee = (mob.getTarget() != null && mob.getTarget().position().closerThan(mob.position(), 8.0D));
		updateCombatTask(entity, forceMelee);
	}

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
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
	public void onTick(IExtraGolem entity) {
		final Mob mob = entity.asMob();
		final Optional<ShootArrowsBehaviorData> oData = entity.getBehaviorData(ShootArrowsBehaviorData.class);
		// update combat goal
		if(entity.isInventoryChanged()) {
			entity.resetInventoryChanged();
			oData.ifPresent(data -> data.setArrowsInInventory(countArrowsInInventory(entity.getInventory())));
			updateCombatTask(entity, false);
		}
		// process player in menu
		if(null == entity.getPlayerInMenu()) {
			return;
		}
		// stop moving and look at player
		mob.getNavigation().stop();
		mob.getLookControl().setLookAt(entity.getPlayerInMenu());
		// ensure the container closes when the player is too far away
		if(!entity.isPlayerInRangeForMenu(8.0D)) {
			entity.getPlayerInMenu().closeContainer();
			entity.setPlayerInMenu(null);
		}
	}

	@Override
	public void onRangedAttack(final IExtraGolem entity, final LivingEntity target, final float distanceFactor) {
		final Mob mob = entity.asMob();
		ItemStack itemstack = findArrowsInInventory(entity.getInventory());
		if (!itemstack.isEmpty()) {
			// first, raytrace to ensure no other creatures are in the way
			final Vec3 start = mob.position().add(0, mob.getBbHeight() * 0.55F, 0);
			final Vec3 end = target.position().add(0, mob.getBbHeight() * 0.5F, 0);
			if(!hasClearPath(entity, start, end)) {
				return;
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
			mob.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (mob.getRandom().nextFloat() * 0.4F + 0.8F));
			mob.level().addFreshEntity(arrow);
			// update itemstack and inventory
			itemstack.shrink(1);
			entity.getInventory().setChanged();
		}
	}


	@Override
	public void onWriteData(final IExtraGolem entity, final CompoundTag tag) {
		entity.writeInventoryToTag(tag);
	}

	@Override
	public void onReadData(final IExtraGolem entity, final CompoundTag tag) {
		entity.readInventoryFromTag(tag);
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of(Component.translatable(PREFIX + "shoot_arrows").withStyle(ChatFormatting.LIGHT_PURPLE),
				Component.translatable(PREFIX + "shoot_arrows.refill").withStyle(ChatFormatting.GRAY));
	}

	//// HELPER METHODS ////

	protected void updateCombatTask(final IExtraGolem entity, final boolean forceMelee) {
		final Mob mob = entity.asMob();
		entity.getBehaviorData(ShootArrowsBehaviorData.class).ifPresent(data -> {
			Goal meleeGoal = data.getMeleeGoal();
			Goal rangedGoal = data.getRangedGoal();
			// remove both goals (clean slate)
			mob.goalSelector.removeGoal(meleeGoal);
			mob.goalSelector.removeGoal(rangedGoal);
			// check if target is close enough to attack
			if (forceMelee || countArrowsInInventory(entity.getInventory()) == 0) {
				mob.goalSelector.addGoal(0, meleeGoal);
			} else {
				mob.goalSelector.addGoal(0, rangedGoal);
			}
		});
	}

	protected int countArrowsInInventory(final Container inv) {
		int arrowCount = 0;
		// add up the size of each itemstack in inventory
		for (int i = 0, n = inv.getContainerSize(); i < n; i++) {
			final ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
				arrowCount += stack.getCount();
			}
		}
		// return arrow count
		return arrowCount;
	}

	protected static ItemStack findArrowsInInventory(final Container inv) {
		// search inventory to find suitable arrow itemstack
		for (int i = 0, l = inv.getContainerSize(); i < l; i++) {
			final ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof ArrowItem) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	public static boolean hasClearPath(final IExtraGolem entity, final Vec3 start, final Vec3 end) {
		final Mob mob = entity.asMob();
		final Vec3 normal = end.subtract(start).normalize();
		final double radius = 0.2D;
		// step along the vector and check for entities at each point
		for (double i = 0, n = start.distanceToSqr(end), stepSize = radius * 2.0D; (i * i) < n; i += stepSize) {
			// scale the vector to the step progress
			Vec3 scaled = normal.scale(i);
			double x = start.x + scaled.x;
			double y = start.y + scaled.y;
			double z = start.z + scaled.z;
			AABB aabb = new AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
			// if any mob at this location cannot be attacked, exit the function
			for (Entity e : mob.level().getEntities(mob, aabb)) {
				if (!mob.canAttackType(e.getType())) {
					return false;
				}
			}
		}
		return true;
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
