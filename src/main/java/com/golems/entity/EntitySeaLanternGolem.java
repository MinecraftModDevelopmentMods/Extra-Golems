package com.golems.entity;

import com.golems.main.Config;
import com.golems.util.WeightedItem;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public final class EntitySeaLanternGolem extends GolemBase {

	public static final String ALLOW_SPECIAL = "Allow Special: Breathe Underwater";

	public EntitySeaLanternGolem(final World world) {
		super(world, Config.SEA_LANTERN.getBaseAttack(), new ItemStack(Blocks.SEA_LANTERN));
		//Invert result because method is 'canDrown' not 'cannotDrown'
		this.canDrown = !(Config.SEA_LANTERN.getBoolean(ALLOW_SPECIAL));
	}

	protected ResourceLocation applyTexture() {
		return makeGolemTexture("sea_lantern");
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.SEA_LANTERN.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
		final int add = lootingLevel > 0 ? 1 : 0;
		this.addDrop(dropList, Blocks.SEA_LANTERN, 0, 1, 2 + add, 100);
		this.addDrop(dropList, Items.PRISMARINE_SHARD, 0, 1, 3, 4 + lootingLevel * 10);
		this.addDrop(dropList, Items.PRISMARINE_CRYSTALS, 0, 1, 3, 4 + lootingLevel * 10);
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}
	
	@Override
	public List<String> addSpecialDesc(final List<String> list) {
		if(Config.SEA_LANTERN.getBoolean(EntitySeaLanternGolem.ALLOW_SPECIAL))
			list.add(TextFormatting.AQUA + trans("entitytip.breathes_underwater"));
		return list;
	}
}
