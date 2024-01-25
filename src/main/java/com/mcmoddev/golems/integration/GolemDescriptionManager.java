package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.data.behavior.data.ShootArrowsBehaviorData;
import com.mcmoddev.golems.data.behavior.data.UseFuelBehaviorData;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class to get in-game information about types of golems. Intended to use
 * for Waila and The One Probe integration.
 *
 * @author skyjay1
 **/
public abstract class GolemDescriptionManager {

	protected boolean showBehaviorData;
	protected boolean showChildBehaviorData;
	protected boolean showAttack;
	protected boolean extended;

	public GolemDescriptionManager() {
		this.showBehaviorData = true;
		this.showChildBehaviorData = false;
		this.showAttack = true;
		this.extended = false;
	}

	/**
	 * Collects descriptions that apply to the given golem
	 *
	 * @param entity the entity
	 * @return a List containing all descriptions that apply to the passed entity
	 **/
	public List<Component> getEntityDescription(final IExtraGolem entity, GolemContainer container) {
		final Mob mob = entity.asMob();
		List<Component> list = new LinkedList<>();
		// add attack damage to tip enabled (usually checks if sneaking)
		if (showAttack) {
			double attack = (mob.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
			list.add(Component.translatable("entitytip.attack").withStyle(ChatFormatting.GRAY).append(": ")
					.append(Component.literal(String.format("%.2f", attack)).withStyle(ChatFormatting.WHITE)));
		}
		// add special descriptions
		if (showBehaviorData) {
			// add fuel amount if this golem consumes fuel
			entity.getBehaviorData(UseFuelBehaviorData.class).ifPresent(data -> addFuelInfo(data, list));
			// add arrow amount if this golem shoots arrows
			entity.getBehaviorData(ShootArrowsBehaviorData.class).ifPresent(data -> addArrowsInfo(data, list));
		}

		// add all other descriptions information
		if ((!mob.isBaby() && showBehaviorData) || (mob.isBaby() && showChildBehaviorData)) {
			list.addAll(container.createDescriptions(mob.level().registryAccess()));
		}
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
		// actually add the description
		list.add(Component.translatable("entitytip.fuel").withStyle(ChatFormatting.GRAY).append(": ")
				.append(Component.literal(fuelString).withStyle(color)));
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
			final ChatFormatting color = ChatFormatting.WHITE;
			// if sneaking, show exact value, otherwise show percentage value
			final String arrowString = String.valueOf(arrows);
			// actually add the description
			list.add(Component.translatable("entitytip.arrows").withStyle(ChatFormatting.GRAY).append(": ")
					.append(Component.literal(arrowString).withStyle(color)));
		}

	}

	/**
	 * @return whether TRUE if the user is currently holding the SHIFT key
	 **/
	protected static boolean isShiftDown() {
		return Screen.hasShiftDown();
	}

}
