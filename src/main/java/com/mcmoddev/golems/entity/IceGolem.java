package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.IceGolemFreezeEvent;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public final class IceGolem extends GolemBase {

	public static final String AOE = "Area of Effect";
	public static final String FROST = "Use Frosted Ice";

	public IceGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.setCanSwim(true);
	}

	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.ICE_GOLEM);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		// calling every other tick reduces lag by 50%
		if (this.ticksExisted % 2 == 0) {
			final int x = MathHelper.floor(this.posX);
			final int y = MathHelper.floor(this.posY - 0.20000000298023224D);
			final int z = MathHelper.floor(this.posZ);
			final BlockPos below = new BlockPos(x, y, z);

			if (this.world.getBiome(below).getTemperature(below) > 1.0F) {
				this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
			}
			int aoe = this.getConfigInt(AOE);
			if (aoe > 0) {
				final IceGolemFreezeEvent event = new IceGolemFreezeEvent(this, below, aoe);
				if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
					this.freezeBlocks(event.getAffectedPositions(), event.getFunction(),
						event.updateFlag);
				}
			}
		}
	}

	@Override
	public boolean attackEntityAsMob(final Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			if (entity.isBurning()) {
				this.attackEntityFrom(DamageSource.GENERIC, 0.5F);
			}
			return true;
		}
		return false;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	/**
	 * Usually called after creating and firing a {@link IceGolemFreezeEvent}. Iterates through the
	 * list of positions and calls {@code apply(BlockState input)} on the passed
	 * {@code Function<BlockState, BlockState>} .
	 *
	 * @return whether all setBlockState calls were successful.
	 **/
	public boolean freezeBlocks(final List<BlockPos> positions,
				    final Function<BlockState, BlockState> function, final int updateFlag) {
		boolean flag = true;
		for (BlockPos pos : positions) {
			flag &= this.world.setBlockState(pos, function.apply(this.world.getBlockState(pos)), updateFlag);
		}
		return flag;
	}
}
