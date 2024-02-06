package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.AbstractShootBehavior;
import com.mcmoddev.golems.data.behavior.ShootArrowsBehavior;
import com.mcmoddev.golems.data.behavior.ShootFireballsBehavior;
import com.mcmoddev.golems.data.behavior.ShootSnowballsBehavior;
import com.mcmoddev.golems.data.behavior.UseFuelBehavior;
import com.mcmoddev.golems.data.behavior.data.ShootBehaviorData;
import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class to get in-game information about types of golems. Intended to use
 * for Waila and The One Probe integration.
 *
 * @author skyjay1
 **/
public abstract class GolemDescriptionManager {

	protected boolean extended;
	private boolean showHealth;
	private boolean showAttack;

	public GolemDescriptionManager() {
		this.extended = false;
		this.showHealth = false;
		this.showAttack = true;
	}

	/**
	 * Collects descriptions that apply to the given golem
	 *
	 * @param entity the entity
	 * @return a List containing all descriptions that apply to the passed entity
	 **/
	public List<Component> getEntityDescription(final IExtraGolem entity, GolemContainer container) {
		final Mob mob = entity.asMob();
		final RegistryAccess registryAccess = mob.level().registryAccess();
		final TooltipFlag tooltipFlag = this.extended ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL;
		// create empty list
		List<Component> list = new ArrayList<>();
		// add attributes
		container.getAttributes().onAddDescriptions(container, registryAccess, list, tooltipFlag, showHealth, showAttack);
		// add ammo count
		if(container.getBehaviors().hasBehavior(ShootArrowsBehavior.class)) {
			addAmmoInfo(entity, "arrows", list);
		}
		if(container.getBehaviors().hasBehavior(ShootFireballsBehavior.class)) {
			addAmmoInfo(entity, "fireballs", list);
		}
		if(container.getBehaviors().hasBehavior(ShootSnowballsBehavior.class)) {
			addAmmoInfo(entity, "snowballs", list);
		}
		// add fuel amount
		if(container.getBehaviors().hasBehavior(UseFuelBehavior.class)) {
			final List<UseFuelBehavior> useFuelBehavior = container.getBehaviors().getActiveBehaviors(UseFuelBehavior.class, entity);
			if(!useFuelBehavior.isEmpty()) {
				addFuelInfo(entity, useFuelBehavior.get(0), list);
			}
		}
		// add behavior descriptions only in extended mode
		if (extended) {
			container.getBehaviors().forEach(b -> b.onAddDescriptions(registryAccess, list, tooltipFlag));
		}
		// add additional descriptions
		list.addAll(container.getGolem().getDescriptions());
		return list;
	}

	/**
	 * Adds information about the amount of fuel stored in the golem
	 *
	 * @param entity the entity
	 * @param behavior the fuel behavior
	 * @param list the description list
	 */
	protected void addFuelInfo(final IExtraGolem entity, final UseFuelBehavior behavior, final List<Component> list) {
		// determine fuel percentage
		final float percentFuel = 100.0F * (float) entity.getFuel() / (float) behavior.getMaxFuel();
		// determine text color
		final ChatFormatting color;
		if (percentFuel < 6) {
			color = ChatFormatting.RED;
		} else if (percentFuel < 16) {
			color = ChatFormatting.YELLOW;
		} else {
			color = ChatFormatting.WHITE;
		}
		// if sneaking, show exact value, otherwise show percentage value
		final String fuelString;
		if (extended) {
			fuelString = String.format("%d / %d", entity.getFuel(), behavior.getMaxFuel());
		} else {
			fuelString = String.format("%.1f", percentFuel) + "%";
		}
		// create fuel amount text
		final Component fuelAmount = Component.literal(fuelString).withStyle(color);
		// add the description
		list.add(Component.translatable("golem.description.fuel", fuelAmount).withStyle(ChatFormatting.GRAY));
	}

	/**
	 * Adds information about the ammo count in the golem inventory
	 *
	 * @param entity the entity
	 * @param key the translation key suffix
	 * @param list the description list
	 */
	protected void addAmmoInfo(final IExtraGolem entity, final String key, final List<Component> list) {
		// determine number of arrows available
		final int count = entity.getAmmo();
		if (count > 0 && extended) {
			// create arrow count text
			final Component countText = Component.literal(String.format("%d", count)).withStyle(ChatFormatting.WHITE);
			// add the description
			list.add(Component.translatable("golem.description." + key, countText).withStyle(ChatFormatting.GRAY));
		}
	}
}
