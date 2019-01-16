package com.golems.entity;

import java.util.List;
import java.util.function.Predicate;

import com.golems.blocks.BlockUtilityGlow;
import com.golems.entity.ai.EntityAIPlaceSingleBlock;
import com.golems.main.Config;
import com.golems.main.GolemItems;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
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
		super(world, Config.SEA_LANTERN.getBaseAttack(), new ItemStack(Blocks.SEA_LANTERN));
		this.canDrown = false;
		this.setLootTableLoc("golem_sea_lantern");
		
	}
	
	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		// lights above and below water... need to add to different lists to run concurrently
		this.tasks.addTask(8, new EntityAIPlaceSingleBlock(this, GolemItems.blockLightSourceWater.getDefaultState()
				.withProperty(BlockUtilityGlow.LIGHT_LEVEL, brightnessInt), Config.SEA_LANTERN.getInt(FREQUENCY), 
				Config.SEA_LANTERN.getBoolean(ALLOW_SPECIAL), WATER_PRED));
		this.targetTasks.addTask(8, new EntityAIPlaceSingleBlock(this, GolemItems.blockLightSource.getDefaultState()
				.withProperty(BlockUtilityGlow.LIGHT_LEVEL, brightnessInt), Config.SEA_LANTERN.getInt(FREQUENCY), 
				Config.SEA_LANTERN.getBoolean(ALLOW_SPECIAL)));
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
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.SEA_LANTERN.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int add = lootingLevel > 0 ? 1 : 0;
//		this.addDrop(dropList, Blocks.SEA_LANTERN, 0, 1, 2 + add, 100);
//		this.addDrop(dropList, Items.PRISMARINE_SHARD, 0, 1, 3, 4 + lootingLevel * 10);
//		this.addDrop(dropList, Items.PRISMARINE_CRYSTALS, 0, 1, 3, 4 + lootingLevel * 10);
//	}
	
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
		if(Config.SEA_LANTERN.getBoolean(EntitySeaLanternGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GOLD + trans("entitytip.lights_area"));
		}
		list.add(TextFormatting.AQUA + trans("entitytip.breathes_underwater"));
		return list;
	}
}
