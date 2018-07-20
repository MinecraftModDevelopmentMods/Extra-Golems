package com.golems.entity;

import java.util.List;

import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemDye;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityStainedGlassGolem extends GolemColorizedMultiTextured {

	public static final String DROP_META = "Drop Metadata";

	private static final ResourceLocation TEXTURE_BASE = GolemBase
			.makeGolemTexture("stained_glass");
	private static final ResourceLocation TEXTURE_OVERLAY = GolemBase
			.makeGolemTexture("stained_glass_grayscale");

	public EntityStainedGlassGolem(World world) {
		super(world, Config.STAINED_GLASS.getBaseAttack(), Blocks.STAINED_GLASS, TEXTURE_BASE,
				TEXTURE_OVERLAY, ItemDye.DYE_COLORS);
		this.setCanTakeFallDamage(true);
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.STAINED_GLASS.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

	/**
	 * Whether {@link overlay} should be rendered as transparent. Is not called for rendering
	 * {@link base}
	 **/
	@SideOnly(Side.CLIENT)
	@Override
	public boolean hasTransparency() {
		return true;
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel) {
		int keyReturn = Config.STAINED_GLASS.getInt(DROP_META);
		int meta = keyReturn < 0 ? 15 - this.getTextureNum() : keyReturn;
		int size = lootingLevel + rand.nextInt(3 + lootingLevel);
		this.addDrop(dropList, Blocks.STAINED_GLASS, meta, 0, size > 4 ? 4 : size,
				50 + lootingLevel * 10);
		this.addDrop(dropList, Blocks.STAINED_GLASS_PANE, meta, 1, 5 + lootingLevel,
				80 + lootingLevel * 10);
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
