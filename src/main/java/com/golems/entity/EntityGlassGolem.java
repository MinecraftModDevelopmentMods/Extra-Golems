package com.golems.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityGlassGolem extends GolemBase {

    public EntityGlassGolem(final World world) {
        super(world);
        this.setCanTakeFallDamage(true);
        this.setLootTableLoc("golem_glass");
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
    }

    protected ResourceLocation applyTexture() {
        return makeGolemTexture("glass");
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
