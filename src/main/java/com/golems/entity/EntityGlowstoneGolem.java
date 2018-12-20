package com.golems.entity;

import java.util.List;

import com.golems.blocks.BlockUtilityGlow;
import com.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.golems.main.Config;
import com.golems.main.GolemItems;
import com.golems.util.WeightedItem;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityGlowstoneGolem extends GolemBase {
	
	public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
	public static final String FREQUENCY = "Light Frequency";
	
	/** Float value between 0.0F and 1.0F that determines light level **/
	private final float brightness;

	/** Default constructor for EntityGlowstoneGolem **/
	public EntityGlowstoneGolem(final World world) {
		this(world, Config.GLOWSTONE.getBaseAttack(), new ItemStack(Blocks.GLOWSTONE), 1.0F,
				Config.GLOWSTONE.getInt(FREQUENCY), Config.GLOWSTONE.getBoolean(ALLOW_SPECIAL));
		this.isImmuneToFire = true;
		this.setCanTakeFallDamage(true);
	}
	
	/** Flexible constructor to allow child classes to customize **/
	public EntityGlowstoneGolem(final World world, final float attack, final ItemStack pick, 
			final float lightLevel, final int freq, final boolean allowed) {
		super(world, attack, pick);
		int lightInt = (int)(lightLevel * 15.0F);
		this.brightness = lightLevel;
		final IBlockState state = GolemItems.blockLightSource.getDefaultState().withProperty(BlockUtilityGlow.LIGHT_LEVEL, lightInt);
		this.tasks.addTask(9, new EntityAIPlaceSingleBlock(this, state, freq, allowed));
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("glowstone");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.GLOWSTONE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		final int size = 6 + this.rand.nextInt(8 + lootingLevel * 2);
		this.addDrop(dropList, new ItemStack(Items.GLOWSTONE_DUST, size), 100);
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
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
		return (int)(15728880F * this.brightness);
	}
	
	@Override
	public float getBrightness() {
		return this.brightness;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		// does not fire for child classes
		if(this.getClass() == EntityGlowstoneGolem.class && Config.GLOWSTONE.getBoolean(ALLOW_SPECIAL))
			list.add(TextFormatting.RED + trans("entitytip.lights_area"));
		return list;
	}
}
