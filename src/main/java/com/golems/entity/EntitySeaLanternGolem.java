package com.golems.entity;

import java.util.List;
import java.util.function.Predicate;

import com.golems.blocks.BlockUtilityGlow;
import com.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.golems.main.GolemItems;
import com.golems.util.GolemConfigSet;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntitySeaLanternGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
	public static final String FREQUENCY = "Light Frequency";
	public static final Predicate<IBlockState> WATER_PRED = new Predicate<IBlockState> () {
		@Override
		public boolean test(IBlockState toReplace) {
			return toReplace.getBlock() != GolemItems.blockLightSourceWater && toReplace.getMaterial() == Material.WATER 
					&& toReplace.getValue(BlockLiquid.LEVEL).intValue() == 0;
		}
	};
	private static final float brightness = 1.0F;
	private static final int brightnessInt =  (int)(brightness * 15.0F);

	public EntitySeaLanternGolem(final World world) {
		super(world);
		this.canDrown = false;
		this.setLootTableLoc("golem_sea_lantern");
		this.setBaseMoveSpeed(0.26D);
		
	}
	
	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		// lights above and below water... need to add to different lists to run concurrently
		GolemConfigSet cfg = getConfig(this);
		this.tasks.addTask(8, new EntityAIPlaceSingleBlock(this, GolemItems.blockLightSourceWater.getDefaultState()
				.withProperty(BlockUtilityGlow.LIGHT_LEVEL, brightnessInt), cfg.getInt(FREQUENCY), 
				cfg.getBoolean(ALLOW_SPECIAL), WATER_PRED));
		this.targetTasks.addTask(8, new EntityAIPlaceSingleBlock(this, GolemItems.blockLightSource.getDefaultState()
				.withProperty(BlockUtilityGlow.LIGHT_LEVEL, brightnessInt), cfg.getInt(FREQUENCY), 
				cfg.getBoolean(ALLOW_SPECIAL)));
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		// speed boost in water
		if(this.isInWater()) {
			this.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 2, false, false));
		}
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("sea_lantern");
	}

	@Override
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
		return (int)(15728880F * this.brightness);
	}
	
	@Override
	public float getBrightness() {
		return this.brightness;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(getConfig(this).getBoolean(EntitySeaLanternGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GOLD + trans("entitytip.lights_area"));
		}
		list.add(TextFormatting.AQUA + trans("entitytip.breathes_underwater"));
		return list;
	}
}
