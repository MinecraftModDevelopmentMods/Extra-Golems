package com.golems.entity;

import javax.annotation.concurrent.Immutable;

import com.golems.blocks.BlockLightProvider;
import com.golems.main.GolemItems;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class GolemLightProvider extends GolemBase {

	protected LightManager lightManager;
	protected int tickDelay;

	public GolemLightProvider(final World world, final float attack, final ItemStack pick, final LightManager light) {
		super(world, attack, pick);
		this.lightManager = light;
		this.tickDelay = 2;
	}

	public GolemLightProvider(final World world, final float attack, final LightManager light) {
		this(world, attack, new ItemStack(GolemItems.golemHead), light);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		// only try to place light blocks every other tick -- reduces lag by 50%
		if (this.tickDelay <= 1 || this.ticksExisted % this.tickDelay == 0) {
			this.placeLightBlock();
		}
	}

	protected boolean placeLightBlock() {
		final int x = MathHelper.floor(this.posX);
		final int y = MathHelper.floor(this.posY - 0.20000000298023224D);
		final int z = MathHelper.floor(this.posZ);
		final int[][] validPos = { { x, z }, { x + 1, z }, { x - 1, z }, { x, z + 1 }, { x, z - 1 },
				{ x + 1, z + 1 }, { x - 1, z + 1 }, { x + 1, z - 1 }, { x - 1, z - 1 } };
		for (final int[] coord : validPos) {
			final int xPos = coord[0];
			final int zPos = coord[1];
			for (int k = 0; k < 3; ++k) {
				final int yPos = y + k + 1;
				final BlockPos pos = new BlockPos(xPos, yPos, zPos);
				final IBlockState state = this.world.getBlockState(pos);
				if (state.getBlock() instanceof BlockLightProvider) {
					return false;
				} else if (this.lightManager.canReplace(state)) {
					return this.lightManager.placeAt(this.world, pos);
				}
			}
		}
		return false;
	}

	@Override
	public int getBrightnessForRender() {
		return this.lightManager.getLightValue() > 0 ? 15728880 : super.getBrightnessForRender();
	}

	/** Gets how bright this entity is. **/
	@Override
	public float getBrightness() {
		return this.lightManager.getBrightness();
	}

	/**
	 * Helper class that manages light-block calculations for this golem. Contains 2 constant
	 * instances for convenience (FULL and HALF) for switching over from the EnumLightLevel methods.
	 **/
	@Immutable
	public static class LightManager {

		public static final LightManager FULL = new LightManager(1.0F);
		public static final LightManager HALF = new LightManager(0.5F);
		private static final int MIN = 0;
		private static final int MAX = 15;

		private final int lightRangeInt;
		private final int updateFlag;
		private final float brightness;
		private final ImmutableSet<Material> replaceableMaterials;

		/**
		 * @param brightness
		 *            a float between 0.0F and 1.0F, inclusive
		 * @param update
		 *            the int flag to pass to the
		 *            {@link World#setBlockState(BlockPos, IBlockState, int)} call. Defaults to 2.
		 * @param canReplace
		 *            a list of Materials that the golem's lights are allowed to replace. Defaults
		 *            to {@code Material.AIR} if left empty
		 **/
		public LightManager(final float brightness, final int update, final Material... canReplace) {
			final int l = (int) (15.0F * brightness);
			this.lightRangeInt = l > MAX ? MAX : (l < MIN ? MIN : l);
			this.brightness = brightness;
			this.updateFlag = update;
			if (canReplace != null && canReplace.length > 0) {
				this.replaceableMaterials = ImmutableSet.copyOf(canReplace);
			} else {
				this.replaceableMaterials = ImmutableSet.of(Material.AIR);
			}
		}

		/**
		 * @see #LightManager(float, int, Material...)
		 **/
		public LightManager(final float brightness, final Material... canReplace) {
			this(brightness, 2, canReplace);
		}

		public IBlockState getLightState() {
			return GolemItems.blockLightSource.getDefaultState()
					.withProperty(BlockLightProvider.LIGHT, this.lightRangeInt);
		}

		public int getLightValue() {
			return this.lightRangeInt;
		}

		public float getBrightness() {
			return this.brightness;
		}

		public int getUpdateFlag() {
			return this.updateFlag;
		}

		public boolean canReplace(final IBlockState state) {
			return this.replaceableMaterials.contains(state.getMaterial());
		}

		public boolean placeAt(final World world, final BlockPos pos) {
			return world.setBlockState(pos, this.getLightState(), this.getUpdateFlag());
		}
	}
}
