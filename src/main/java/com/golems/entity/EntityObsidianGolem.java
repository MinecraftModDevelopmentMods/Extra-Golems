package com.golems.entity;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public final class EntityObsidianGolem extends GolemBase {

    public EntityObsidianGolem(final World world) {
        super(world);
        this.setLootTableLoc("golem_obsidian");
        this.setImmuneToFire(true);
    }

    @Override
    protected ResourceLocation applyTexture() {
        return makeGolemTexture("obsidian");
    }

    @Override
    public SoundEvent getGolemSound() {
        return SoundEvents.BLOCK_STONE_STEP;
    }
}
