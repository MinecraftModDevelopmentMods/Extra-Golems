package com.golems.entity;

import javax.annotation.concurrent.Immutable;

import com.golems.blocks.BlockLightProvider;
import com.golems.main.GolemItems;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class GolemLightProvider extends GolemBase {

	protected LightManager lightManager;
	protected int tickDelay;
	@Deprecated /** REMOVE THIS FIELD IN NEXT MAJOR UPDATE **/
	protected int updateFlag;

	public GolemLightProvider(World world, float attack, ItemStack pick, LightManager light) {
		super(world, attack, pick);
		this.lightManager = light;
		this.tickDelay = 2;
	}

	public GolemLightProvider(World world, float attack, LightManager light) {
		this(world, attack, new ItemStack(GolemItems.golemHead), light);
	}

	/**
	 * @deprecated Use {@link #GolemLightProvider(World, float, ItemStack, LightManager)} REMOVE
	 *             THIS CONSTRUCTOR IN NEXT MAJOR UPDATE
	 **/
	@Deprecated
	public GolemLightProvider(World world, float attack, Block pick, EnumLightLevel light) {
		this(world, attack, new ItemStack(pick),
				new LightManager(light.getBrightness(), light.getMaterialToReplace()));
	}

	/**
	 * @deprecated Use {@link #GolemLightProvider(World, float, LightManager)} REMOVE THIS
	 *             CONSTRUCTOR IN NEXT MAJOR UPDATE
	 **/
	@Deprecated
	public GolemLightProvider(World world, float attack, EnumLightLevel light) {
		this(world, attack, GolemItems.golemHead, light);
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
		int x = MathHelper.floor(this.posX);
		int y = MathHelper.floor(this.posY - 0.20000000298023224D);
		int z = MathHelper.floor(this.posZ);
		int[][] validPos = { { x, z }, { x + 1, z }, { x - 1, z }, { x, z + 1 }, { x, z - 1 },
				{ x + 1, z + 1 }, { x - 1, z + 1 }, { x + 1, z - 1 }, { x - 1, z - 1 } };
		for (int[] coord : validPos) {
			int xPos = coord[0];
			int zPos = coord[1];
			for (int k = 0; k < 3; ++k) {
				int yPos = y + k + 1;
				BlockPos pos = new BlockPos(xPos, yPos, zPos);
				IBlockState state = this.world.getBlockState(pos);
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

	/** Gets how bright this entity is **/
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

		private final int LIGHT_RANGE_INT;
		private final int UPDATE_FLAG;
		private final float BRIGHTNESS;
		private final ImmutableSet<Material> REPLACEABLE_MATERIALS;

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
		public LightManager(float brightness, int update, Material... canReplace) {
			int l = (int) (15.0F * brightness);
			this.LIGHT_RANGE_INT = l > MAX ? MAX : (l < MIN ? MIN : l);
			this.BRIGHTNESS = brightness;
			this.UPDATE_FLAG = update;
			if (canReplace != null && canReplace.length > 0) {
				this.REPLACEABLE_MATERIALS = ImmutableSet.copyOf(canReplace);
			} else {
				this.REPLACEABLE_MATERIALS = ImmutableSet.of(Material.AIR);
			}
		}

		/**
		 * @see #LightManager(float, int, Material...)
		 **/
		public LightManager(float brightness, Material... canReplace) {
			this(brightness, 2, canReplace);
		}

		public IBlockState getLightState() {
			return GolemItems.blockLightSource.getDefaultState()
					.withProperty(BlockLightProvider.LIGHT, this.LIGHT_RANGE_INT);
		}

		public int getLightValue() {
			return this.LIGHT_RANGE_INT;
		}

		public float getBrightness() {
			return this.BRIGHTNESS;
		}

		public int getUpdateFlag() {
			return this.UPDATE_FLAG;
		}

		public boolean canReplace(IBlockState state) {
			return this.REPLACEABLE_MATERIALS.contains(state.getMaterial());
		}

		public boolean placeAt(World world, BlockPos pos) {
			return world.setBlockState(pos, this.getLightState(), this.getUpdateFlag());
		}
	}

	/**
	 * @deprecated use {@link LightManager} REMOVE THIS ENUM IN NEXT MAJOR UPDATE
	 **/
	@Deprecated
	public static enum EnumLightLevel {
		HALF(0.5F, Material.AIR), FULL(1.0F, Material.AIR), WATER_HALF(0.5F,
				Material.WATER), WATER_FULL(1.0F, Material.WATER);

		private final float light;
		private final Material replaceable;

		private EnumLightLevel(float brightness, Material canReplace) {
			this.light = brightness;
			this.replaceable = canReplace;
		}

		public Block getLightBlock() {
			return GolemItems.blockLightSource;
		}

		public float getBrightness() {
			return this.light;
		}

		public Material getMaterialToReplace() {
			return this.replaceable;
		}
	}
}
