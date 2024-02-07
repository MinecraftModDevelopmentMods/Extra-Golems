package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.data.behavior.data.ShootBehaviorData;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.entity.goal.MoveToItemGoal;
import com.mcmoddev.golems.menu.GolemInventoryMenu;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Immutable
public abstract class AbstractShootBehavior extends Behavior {

	/** The follow range modifier to allow the entity to detect enemies from a distance **/
	public static final AttributeModifier RANGED_FOLLOW_BONUS = new AttributeModifier("Ranged follow bonus", 8.0F, AttributeModifier.Operation.ADDITION);

	/** True to consume ammo when shooting **/
	private final boolean consume;
	/** The number of ticks between attacks **/
	private final int attackInterval;

	public AbstractShootBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate, boolean consume, int attackInterval) {
		super(variant, tooltipPredicate);
		this.consume = consume;
		this.attackInterval = attackInterval;
	}

	//// GETTERS ////

	public boolean consume() {
		return consume;
	}

	public int getAttackInterval() {
		return attackInterval;
	}

	protected boolean isInRangeToAttack(final IExtraGolem entity, final @Nullable LivingEntity target) {
		return target != null && entity.asMob().position().closerThan(target.position(), 3.5D);
	}

	//// ABSTRACT METHODS ////

	/**
	 * Called from {@link #onRangedAttack(IExtraGolem, LivingEntity, float)}
	 * @param entity the entity
	 * @param target the target entity
	 * @param distanceFactor the distance factor
	 * @return true if an attack was performed
	 */
	protected abstract boolean performRangedAttack(final IExtraGolem entity, final LivingEntity target, final float distanceFactor);

	/**
	 * @param itemStack the item stack
	 * @return true if the item stack is ammo
	 */
	public abstract boolean isAmmo(final ItemStack itemStack);

	//// DEFAULT METHODS ////

	/**
	 * @param entity the entity
	 * @return the behavior data for this behavior, if any
	 * @see IExtraGolem#getBehaviorData(Class)
	 */
	protected Optional<? extends ShootBehaviorData> getShootData(final IExtraGolem entity) {
		return entity.getBehaviorData(ShootBehaviorData.class);
	}

	/**
	 * Called when the inventory is changed, before updating the combat task
	 * @param entity the entity
	 */
	protected void onInventoryChanged(final IExtraGolem entity) {
		entity.setAmmo(count(entity.getInventory(), this::isAmmo));
	}

	/**
	 * @param entity the entity
	 * @return true if the entity has ammo
	 */
	protected boolean hasAmmo(final IExtraGolem entity) {
		if(!consume()) {
			return true;
		}
		return count(entity.getInventory(), this::isAmmo) > 0;
	}

	/**
	 * Called when the player interacts with the entity
	 * @param entity the entity
	 * @param player the player
	 * @return true if a menu was opened
	 */
	protected boolean openMenu(final IExtraGolem entity, final ServerPlayer player) {
		final Mob mob = entity.asMob();
		NetworkHooks.openScreen(player, new SimpleMenuProvider(
				(windowId, inventory, menuPlayer) -> new GolemInventoryMenu(windowId, inventory, entity.getInventory(), entity, ContainerLevelAccess.create(mob.level(), mob.blockPosition())),
				mob.getName()));
		return true;
	}

	//// METHODS ////

	@Override
	public void onRegisterGoals(final IExtraGolem entity) {
		// modify follow range
		AttributeInstance followRange = entity.asMob().getAttribute(Attributes.FOLLOW_RANGE);
		if(!followRange.hasModifier(RANGED_FOLLOW_BONUS)) {
			followRange.addPermanentModifier(RANGED_FOLLOW_BONUS);
		}
		// register move to item goal
		if(consume()) {
			entity.asMob().goalSelector.addGoal(3, new MoveToItemGoal<>(entity.asMob(), 10.0D, 50, 1.0D, this.getVariantBounds()));
			entity.asMob().setCanPickUpLoot(true);
		}
	}

	@Override
	public void onActuallyHurt(final IExtraGolem entity, final DamageSource source, final float amount) {
		final Mob mob = entity.asMob();
		// if it's an arrow or other projectile, set the attacker as revenge target
		if (source.isIndirect() && source.getEntity() instanceof LivingEntity) {
			mob.setTarget((LivingEntity) source.getEntity());
		}
		updateCombatTask(entity, isInRangeToAttack(entity, mob.getTarget()));
	}

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && player instanceof ServerPlayer && player.containerMenu == player.inventoryMenu) {
			// update menu player
			if(entity.getPlayerInMenu() != null) {
				entity.getPlayerInMenu().closeContainer();
			}
			if(openMenu(entity, (ServerPlayer) player)) {
				player.swing(hand);
				entity.setPlayerInMenu(player);
			}
		}
	}

	@Override
	public void onTarget(IExtraGolem entity, @Nullable LivingEntity target) {
		// update target task
		if(target != null) {
			updateCombatTask(entity, isInRangeToAttack(entity,  entity.asMob().getTarget()));
		}
	}

	@Override
	public void onTick(IExtraGolem entity) {
		final Mob mob = entity.asMob();
		// update combat goal
		if(entity.isInventoryChanged()) {
			entity.resetInventoryChanged();
			onInventoryChanged(entity);
			updateCombatTask(entity, isInRangeToAttack(entity,  entity.asMob().getTarget()));
		}
		// process player in menu
		if(entity.getPlayerInMenu() != null) {
			// stop moving and look at player
			mob.getNavigation().stop();
			mob.getLookControl().setLookAt(entity.getPlayerInMenu());
			// clear attack target
			if(mob.getTarget() != null) {
				mob.setTarget(null);
			}
			// ensure the container closes when the player is too far away
			if(!entity.isPlayerInRangeForMenu(8.0D)) {
				entity.getPlayerInMenu().closeContainer();
				entity.setPlayerInMenu(null);
			}
		}
	}

	@Override
	public void onRangedAttack(final IExtraGolem entity, final LivingEntity target, final float distanceFactor) {
		if(performRangedAttack(entity, target, distanceFactor) && consume()) {
			entity.getInventory().setChanged();
		}
	}

	//// NBT ////

	private static final String KEY_AMMO = "Ammo";

	@Override
	public void onWriteData(final IExtraGolem entity, final CompoundTag tag) {
		tag.putInt(KEY_AMMO, entity.getAmmo());
	}

	@Override
	public void onReadData(final IExtraGolem entity, final CompoundTag tag) {
		entity.setAmmo(tag.getInt(KEY_AMMO));
	}

	//// HELPER METHODS ////

	protected static <T extends AbstractShootBehavior> Products.P4<RecordCodecBuilder.Mu<T>, MinMaxBounds.Ints, TooltipPredicate, Boolean, Integer> shootCodecStart(RecordCodecBuilder.Instance<T> instance) {
		return codecStart(instance)
				.and(Codec.BOOL.optionalFieldOf("consume", true).forGetter(AbstractShootBehavior::consume))
				.and(Codec.intRange(1, 100).optionalFieldOf("attack_interval", 30).forGetter(AbstractShootBehavior::getAttackInterval));
	}

	/**
	 * Checks the entity ammo count and updates to use the melee or ranged attack goal.
	 * @param entity the entity
	 * @param forceMelee true to update the combat task to melee
	 */
	protected void updateCombatTask(final IExtraGolem entity, final boolean forceMelee) {
		final Mob mob = entity.asMob();
		final boolean hasAmmo = hasAmmo(entity);
		getShootData(entity).ifPresent(data -> {
			Goal meleeGoal = data.getMeleeGoal();
			Goal rangedGoal = data.getRangedGoal();
			// remove both goals (clean slate)
			mob.goalSelector.removeGoal(meleeGoal);
			mob.goalSelector.removeGoal(rangedGoal);
			// check if target is close enough to attack
			if (forceMelee || !hasAmmo) {
				mob.goalSelector.addGoal(0, meleeGoal);
			} else {
				mob.goalSelector.addGoal(0, rangedGoal);
			}
		});
	}

	/**
	 * @param inv a container inventory
	 * @param predicate a predicate to test the items in the container inventory
	 * @return the number of items that pass the predicate
	 */
	protected int count(final Container inv, Predicate<ItemStack> predicate) {
		int count = 0;
		// add up the size of each itemstack in inventory
		for (int i = 0, n = inv.getContainerSize(); i < n; i++) {
			final ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && predicate.test(stack)) {
				count += stack.getCount();
			}
		}
		// return arrow count
		return count;
	}

	/**
	 * @param inv a container inventory
	 * @param predicate a predicate to test the items in the container inventory
	 * @return the first itemstack that passes the predicate, or an empty itemstack
	 */
	protected static ItemStack findFirst(final Container inv, Predicate<ItemStack> predicate) {
		// search inventory to find suitable itemstack
		for (int i = 0, l = inv.getContainerSize(); i < l; i++) {
			final ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && predicate.test(stack)) {
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
		if (!(o instanceof AbstractShootBehavior)) return false;
		if (!super.equals(o)) return false;
		AbstractShootBehavior that = (AbstractShootBehavior) o;
		return consume == that.consume;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), consume);
	}
}
