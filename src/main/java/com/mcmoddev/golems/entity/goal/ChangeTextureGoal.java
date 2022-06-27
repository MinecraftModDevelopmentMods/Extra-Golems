package com.mcmoddev.golems.entity.goal;

import com.mcmoddev.golems.container.behavior.parameter.ChangeTexturesBehaviorParameter;
import com.mcmoddev.golems.entity.IFuelConsumer;
import com.mcmoddev.golems.entity.IMultitextured;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Optional;

public class ChangeTextureGoal<T extends Mob & IMultitextured & IFuelConsumer> extends Goal {

	protected T entity;
	private final ChangeTexturesBehaviorParameter tickTextures;
	private final ChangeTexturesBehaviorParameter wetTextures;
	private final ChangeTexturesBehaviorParameter dryTextures;
	private final ChangeTexturesBehaviorParameter fueledTextures;
	private final ChangeTexturesBehaviorParameter emptyTextures;
	/**
	 * True if at least one ChangeTexturesBehaviorParameter is present
	 **/
	protected final boolean useable;

	protected int prevFuel;

	public ChangeTextureGoal(final T entity, ChangeTexturesBehaviorParameter tickTextures,
							 ChangeTexturesBehaviorParameter wetTextures, ChangeTexturesBehaviorParameter dryTextures,
							 ChangeTexturesBehaviorParameter fueledTextures, ChangeTexturesBehaviorParameter emptyTextures) {
		super();
		this.setFlags(EnumSet.noneOf(Goal.Flag.class));
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
		final int textureId = entity.getTextureId();
		int updateTextureId = textureId;
		// update only if fuel has changed
		final int fuel = entity.getFuel();
		if (fueledTextures != null && emptyTextures != null && fuel != prevFuel) {
			ChangeTexturesBehaviorParameter param = (fuel > 0) ? fueledTextures : emptyTextures;
			if (entity.getRandom().nextFloat() < param.getChance()) {
				updateTextureId = param.getTextureId(String.valueOf(textureId), textureId);
			}
			prevFuel = fuel;
		}
		// update each tick based on wet/dry and current texture
		if (wetTextures != null && dryTextures != null) {
			ChangeTexturesBehaviorParameter param = entity.isInWaterRainOrBubble() ? wetTextures : dryTextures;
			if (entity.getRandom().nextFloat() < param.getChance()) {
				updateTextureId = param.getTextureId(String.valueOf(textureId), textureId);
			}
		}
		// update each tick based on current texture
		if (tickTextures != null && entity.getRandom().nextFloat() < tickTextures.getChance()) {
			// update tick parameter
			updateTextureId = tickTextures.getTextureId(String.valueOf(textureId), textureId);
		}

		// attempt to update texture ID
		if (updateTextureId != textureId) {
			entity.setTextureId((byte) updateTextureId);
		}
	}
}
