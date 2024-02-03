package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.data.ShootArrowsBehaviorData;
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
		// add behavior data
		entity.getBehaviorData(UseFuelBehaviorData.class).ifPresent(data -> addFuelInfo(data, list));
		entity.getBehaviorData(ShootArrowsBehaviorData.class).ifPresent(data -> addArrowsInfo(data, list));
		// add behavior descriptions only in extended mode
		if (extended) {
			container.getBehaviors().forEach(b -> b.onAddDescriptions(registryAccess, list, tooltipFlag));
		}
		// add additional descriptions
		list.addAll(container.getGolem().getDescriptions());
		return list;
	}

	/**
	 * Adds information about the amount of fuel in the golem's inventory
	 *
	 * @param data the fuel behavior data
	 * @param list the description list
	 */
	protected void addFuelInfo(final UseFuelBehaviorData data, final List<Component> list) {
		// determine fuel percentage
		final float percentFuel = data.getFuelPercentage() * 100.0F;
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
			fuelString = String.format("%d / %d", data.getFuel(), data.getMaxFuel());
		} else {
			fuelString = String.format("%.1f", percentFuel) + "%";
		}
		// create fuel amount text
		final Component fuelAmount = Component.literal(fuelString).withStyle(color);
		// add the description
		list.add(Component.translatable("golem.description.behavior.fuel", fuelAmount).withStyle(ChatFormatting.GRAY));
	}

	/**
	 * Adds information about the number of arrows in the golem's inventory
	 *
	 * @param data the shoot arrows behavior data
	 * @param list the description list
	 */
	protected void addArrowsInfo(final ShootArrowsBehaviorData data, final List<Component> list) {
		// determine number of arrows available
		final int arrows = data.getArrowsInInventory();
		if (arrows > 0 && extended) {
			// create arrow count text
			final Component arrowCount = Component.literal(String.format("%d", arrows)).withStyle(ChatFormatting.WHITE);
			// add the description
			list.add(Component.translatable("golem.description.behavior.arrows", arrowCount).withStyle(ChatFormatting.GRAY));
		}
	}
}
