package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.container.behavior.parameter.ChangeMaterialBehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class ChangeMaterialGoal<T extends GolemBase> extends Goal {

	protected T entity;
	private final ChangeMaterialBehaviorParameter tickTextures;
	private final ChangeMaterialBehaviorParameter wetTextures;
	private final ChangeMaterialBehaviorParameter dryTextures;
	private final ChangeMaterialBehaviorParameter fueledTextures;
	private final ChangeMaterialBehaviorParameter emptyTextures;
	/**
	 * True if at least one ChangeMaterialBehaviorParameter is present
	 **/
	protected final boolean useable;

	protected int prevFuel;

	public ChangeMaterialGoal(final T entity, ChangeMaterialBehaviorParameter tickTextures,
                              ChangeMaterialBehaviorParameter wetTextures, ChangeMaterialBehaviorParameter dryTextures,
                              ChangeMaterialBehaviorParameter fueledTextures, ChangeMaterialBehaviorParameter emptyTextures) {
		super();
		this.setFlags(EnumSet.noneOf(Flag.class));
		this.entity = entity;
		this.tickTextures = tickTextures;
		this.wetTextures = wetTextures;
		this.dryTextures = dryTextures;
		this.fueledTextures = fueledTextures;
		this.emptyTextures = emptyTextures;
		this.useable = (tickTextures != null || (wetTextures != null && dryTextures != null)
				|| (fueledTextures != null && emptyTextures != null));
	}

	@Override
	public boolean canUse() {
		return useable;
	}

	@Override
	public boolean canContinueToUse() {
		return true;
	}

	@Override
	public void start() {
		prevFuel = entity.getFuel();
	}

	@Override
	public void tick() {
		ResourceLocation updateMaterialId = entity.getMaterial();
		// update only if fuel has changed
		final int fuel = entity.getFuel();
		if (fueledTextures != null && emptyTextures != null && fuel != prevFuel) {
			ChangeMaterialBehaviorParameter param = (fuel > 0) ? fueledTextures : emptyTextures;
			if (entity.getRandom().nextFloat() < param.getChance()) {
				updateMaterialId = param.getMaterial();
			}
			prevFuel = fuel;
		}
		// update each tick based on wet/dry and current texture
		if (wetTextures != null && dryTextures != null) {
			ChangeMaterialBehaviorParameter param = entity.isInWaterRainOrBubble() ? wetTextures : dryTextures;
			if (entity.getRandom().nextFloat() < param.getChance()) {
				updateMaterialId = param.getMaterial();
			}
		}
		// update each tick based on current texture
		if (tickTextures != null && entity.getRandom().nextFloat() < tickTextures.getChance()) {
			// update tick parameter
			updateMaterialId = tickTextures.getMaterial();
		}

		// attempt to update texture ID
		if (entity.getMaterial() != updateMaterialId) {
			entity.setMaterial(updateMaterialId);
		}
	}
}
