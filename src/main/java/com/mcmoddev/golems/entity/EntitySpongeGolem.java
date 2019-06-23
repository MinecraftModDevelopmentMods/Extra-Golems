package com.mcmoddev.golems.entity;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.events.SpongeGolemSoakEvent;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;

public final class EntitySpongeGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Absorb Water";
	public static final String INTERVAL = "Water Soaking Frequency";
	public static final String RANGE = "Water Soaking Range";

	public EntitySpongeGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world);
		this.setCanSwim(true);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.SPONGE_GOLEM);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example,
	 * zombies and skeletons use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		final int interval = this.getConfigInt(INTERVAL);
		if (this.getConfigBool(ALLOW_SPECIAL)
			&& (this.ticksExisted % interval == 0)) {
			final int x = MathHelper.floor(this.posX);
			final int y = MathHelper.floor(this.posY - 0.20000000298023224D) + 2;
			final int z = MathHelper.floor(this.posZ);
			final BlockPos center = new BlockPos(x, y, z);
			final SpongeGolemSoakEvent event = new SpongeGolemSoakEvent(this, center,
				this.getConfigInt(RANGE));
			if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY) {
				this.replaceWater(event.getPositionList(), event.getAbsorbFunction(),
					event.updateFlag);
			}
		}
		if (Math.abs(this.motionX) < 0.05D
			&& Math.abs(this.motionZ) < 0.05D && world.isRemote) {
			final BasicParticleType particle = this.isBurning() ? ParticleTypes.SMOKE
				: ParticleTypes.field_218422_X; // SPLASH
			final double x = this.rand.nextDouble() - 0.5D * (double) this.getWidth() * 0.6D;
			final double y = this.rand.nextDouble() * (this.getHeight() - 0.75D);
			final double z = this.rand.nextDouble() - 0.5D * (double) this.getWidth();
			this.world.addParticle(particle, this.posX + x, this.posY + y, this.posZ + z,
				(this.rand.nextDouble() - 0.5D) * 0.5D, this.rand.nextDouble() - 0.5D,
				(this.rand.nextDouble() - 0.5D) * 0.5D);
		}
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOL_STEP;
	}

	/**
	 * Usually called after creating and firing a {@link SpongeGolemSoakEvent}. Iterates through the
	 * list of positions and replaces each one with the passed BlockState.
	 *
	 * @return whether all setBlockState calls were successful.
	 **/
	public boolean replaceWater(final List<BlockPos> positions, 
			final Function<BlockState, BlockState> replaceWater, final int updateFlag) {
		boolean flag = true;
		for (final BlockPos p : positions) {
			flag &= this.world.setBlockState(p, replaceWater.apply(world.getBlockState(p)), updateFlag);
		}
		return flag;
	}
}
