package com.golems.entity;

import com.golems.entity.ai.EntityAIPlaceRandomBlocksStrictly;
import com.golems.main.Config;
import com.golems.util.WeightedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import java.util.List;

public final class EntityGlowstoneGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Place Torches";
	public static final String FREQUENCY = "Torch Frequency";
	public final IBlockState[] state = {Blocks.TORCH.getDefaultState()};

	public EntityGlowstoneGolem(final World world) {
		super(world, Config.GLOWSTONE.getBaseAttack(), new ItemStack(Blocks.TORCH));

		this.setCanTakeFallDamage(true);
		this.isImmuneToFire = true;
		final int freq = Config.GLOWSTONE.getInt(FREQUENCY);
		final boolean allowed = Config.GLOWSTONE.getBoolean(ALLOW_SPECIAL);
		this.tasks.addTask(2,
				new EntityAIPlaceRandomBlocksStrictly(this, freq, state, allowed));
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
}
