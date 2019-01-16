package com.golems.entity;

import java.util.ArrayList;
import java.util.List;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityMelonGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Plant Flowers";
	public static final String FREQUENCY = "Flower Frequency";

	public EntityMelonGolem(final World world) {
		super(world, Config.MELON.getBaseAttack(), Blocks.MELON_BLOCK);
		this.setCanSwim(true);
		this.tasks.addTask(2, this.makeFlowerAI());
		this.setLootTableLoc("golem_melon");
	}

	@Override
	protected ResourceLocation applyTexture() {
		return makeGolemTexture("melon");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.MELON.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		final int size = 6 + this.rand.nextInt(6 + lootingLevel * 4);
//		this.addDrop(dropList, new ItemStack(Items.MELON, size), 100);
//		this.addDrop(dropList, Items.MELON_SEEDS, 0, 1, 6 + lootingLevel, 20 + lootingLevel * 10);
//		this.addDrop(dropList, Items.SPECKLED_MELON, 0, 1, 1, 2 + lootingLevel * 10);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_STONE_STEP;
	}

	/** Create an EntityAIPlaceRandomBlocks. **/
	protected EntityAIBase makeFlowerAI() {
		final Block[] soils = { Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM, Blocks.FARMLAND };
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
		final int freq = Config.MELON.getInt(FREQUENCY);
		final boolean allowed = Config.MELON.getBoolean(ALLOW_SPECIAL);
		return new EntityAIPlaceRandomBlocksStrictly(this, freq, flowers, soils, allowed);
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(Config.MELON.getBoolean(EntityMelonGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.GREEN + trans("entitytip.plants_flowers", trans("tile.flower1.name")));
		return list;
	}
}
