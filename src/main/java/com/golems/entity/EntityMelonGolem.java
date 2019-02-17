package com.golems.entity;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class EntityMelonGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
	public static final String FREQUENCY = "Flower Frequency";
	public static final String ALLOW_HEALING = "Allow Special: Random Healing";

	public EntityMelonGolem(final World world) {
		super(GolemEntityTypes.MELON, world);
		this.setCanSwim(true);
		this.tasks.addTask(2, this.makeFlowerAI());
		this.setLootTableLoc(GolemNames.MELON_GOLEM);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
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
		// heals randomly (about every 20 sec)
		if(rand.nextInt(Config.RANDOM_HEAL_TIMER) == 0 && getConfig(this).getBoolean(ALLOW_HEALING)) {
			this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 20, 2));
		}
	}

	/**
	 * Create an EntityAIPlaceRandomBlocks.
	 **/
	protected EntityAIBase makeFlowerAI() {
		GolemConfigSet cfg = getConfig(this);
		final Block[] soils = {Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM, Blocks.FARMLAND};
		// init list and AI for planting flowers
		final List<IBlockState> lFlowers = new ArrayList<>();
		for (final EnumFlowerType e : BlockFlower.EnumFlowerType.values()) {
			lFlowers.add(e.getBlockType().getBlock().getStateFromMeta(e.getMeta()));
		}
		for (BlockTallGrass.EnumType e : BlockTallGrass.EnumType.values()) {
			lFlowers.add(Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, e));
		}
		final IBlockState[] flowers = lFlowers.toArray(new IBlockState[lFlowers.size()]);
		// get other parameters for the AI
		final int freq = cfg.getInt(FREQUENCY);
		//TODO: Fix possible NPE
		final boolean allowed = cfg.getBoolean(ALLOW_SPECIAL);
		return new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, allowed);
	}

	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if (getConfig(this).getBoolean(EntityMelonGolem.ALLOW_SPECIAL)) {
			list.add(TextFormatting.GREEN + trans("entitytip.plants_flowers", trans("tile.flower1.name")));
		}
		if(getConfig(this).getBoolean(ALLOW_HEALING)) {
			String sHeals = TextFormatting.RED + trans("entitytip.heals");
			list.add(sHeals);
		}
		return list;
	}
}
