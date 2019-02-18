package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.blocks.BlockUtilityGlow;
import com.mcmoddev.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.main.GolemItems;
import com.mcmoddev.golems.util.GolemLookup;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public final class EntityGlowstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
	public static final String FREQUENCY = "Light Frequency";

	/**
	 * Float value between 0.0F and 1.0F that determines light level
	 **/
	private final float brightness;

	/**
	 * Default constructor for EntityGlowstoneGolem
	 **/
	public EntityGlowstoneGolem(final World world) {
		//HACK: dangerous ... too expensive to check for non-null in constructor call :(
		this(EntityGlowstoneGolem.class, world, 1.0F, GolemLookup.getConfig(EntityGlowstoneGolem.class).getInt(FREQUENCY),
			GolemLookup.getConfig(EntityGlowstoneGolem.class).getBoolean(ALLOW_SPECIAL));
		this.isImmuneToFire = true;
		this.setCanTakeFallDamage(true);
		this.setCanSwim(true);
		this.setLootTableLoc(GolemNames.GLOWSTONE_GOLEM);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	/**
	 * Flexible constructor to allow child classes to customize
	 **/
	public EntityGlowstoneGolem(final Class<? extends EntityGlowstoneGolem> clazz, final World world,
				    final float lightLevel, final int freq, final boolean allowed) {
		super(clazz, world);
		int lightInt = (int) (lightLevel * 15.0F);
		this.brightness = lightLevel;
		final IBlockState state = GolemItems.blockLightSource.getDefaultState().with(BlockUtilityGlow.LIGHT_LEVEL, lightInt);
		this.tasks.addTask(9, new EntityAIPlaceSingleBlock(this, state, freq, allowed));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.GLOWSTONE_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getBrightnessForRender() {
		return (int) (15728880F * this.brightness);
	}

	@Override
	public float getBrightness() {
		return this.brightness;
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// does not fire for child classes
		if (this.getClass() == EntityGlowstoneGolem.class && getConfig(this).getBoolean(ALLOW_SPECIAL)) {
			list.add(TextFormatting.RED + trans("entitytip.lights_area"));
		}
		return list;
	}
}
