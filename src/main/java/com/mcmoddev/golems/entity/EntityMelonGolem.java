package com.mcmoddev.golems.entity;

import java.util.List;

import com.mcmoddev.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityMelonGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
	public static final String FREQUENCY = "Flower Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public EntityMelonGolem(final World world) {
		super(EntityMelonGolem.class, world);
		this.setCanSwim(true);
		this.tasks.addTask(2, this.makeFlowerAI());
		this.setLootTableLoc(GolemNames.MELON_GOLEM);
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeTexture(ExtraGolems.MODID, GolemNames.MELON_GOLEM);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void livingTick() {
		super.livingTick();
		if(!this.getConfigBool(ALLOW_HEALING)) return;
		// heals randomly (about every 20 sec)
		if(rand.nextInt(450) == 0) {
			this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 50, 1));
		}
	}

	/**
	 * Create an EntityAIPlaceRandomBlocks.
	 **/
	protected EntityAIBase makeFlowerAI() {

		final Block[] soils = {Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM};
		// init list and AI for planting flowers
		final IBlockState[] flowers = {Blocks.POPPY.getDefaultState()};
		// get other parameters for the AI
		final int freq = this.getConfigInt(FREQUENCY);
		final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
		return new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, allowed);
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (this.getConfigBool(ALLOW_SPECIAL)) {
			list.add(TextFormatting.GREEN + trans("entitytip.plants_flowers", trans("tile.flower1.name")));
		}
		if(this.getConfigBool(ALLOW_HEALING)) {
			String sHeals = TextFormatting.RED + trans("entitytip.heals");
			list.add(sHeals);
		}
		return list;
	}
}
