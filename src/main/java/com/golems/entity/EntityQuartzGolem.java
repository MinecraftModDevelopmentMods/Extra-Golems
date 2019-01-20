package com.golems.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityQuartzGolem extends GolemBase {

    public EntityQuartzGolem(final World world) {
        super(world);
        this.setLootTableLoc("golem_quartz");
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
    }

    @Override
    protected ResourceLocation applyTexture() {
        return makeGolemTexture("quartz");
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
