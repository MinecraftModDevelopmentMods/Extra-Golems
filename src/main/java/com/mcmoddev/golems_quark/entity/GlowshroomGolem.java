package com.mcmoddev.golems_quark.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.ai.PlaceBlocksGoal;
import com.mcmoddev.golems.entity.base.GolemBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class GlowshroomGolem extends GolemBase {
  
  public static final String ALLOW_SPECIAL = "Allow Special: Plant Glowshrooms";
  public static final String FREQUENCY = "Mushroom Frequency";
  public static final String ALLOW_HEALING = "Allow Special: Random Healing";

  /**
   * Float value between 0.0F and 1.0F that determines light level
   **/
  private final float brightness;

  public GlowshroomGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world);
    this.brightness = 14.0F / 15.0F;
  }
  
  @Override
  public void livingTick() {
    super.livingTick();
    // particles
    if(this.world.isRemote && rand.nextInt(20) == 0) {
      final Vector3d pos = this.getPositionVec();
      this.world.addParticle(ParticleTypes.END_ROD, 
          pos.getX() + (rand.nextFloat() - 0.5F) * this.getWidth() * 0.8F, 
          pos.getY() + rand.nextFloat() * this.getHeight(), 
          pos.getZ() + (rand.nextFloat() - 0.5F) * this.getWidth() * 0.8F, 0, 0, 0);
    }
  }
    
  
  @Override
  protected void registerGoals() {
    super.registerGoals();
    // "plant glowshrooms" goal
    final ResourceLocation rlGlowshroom = new ResourceLocation("quark", "glowshroom");
    final ResourceLocation rlGlowcelium = new ResourceLocation("quark", "glowcelium");
    if(ForgeRegistries.BLOCKS.containsKey(rlGlowshroom)) {
      final boolean allowed = this.getConfigBool(ALLOW_SPECIAL);
      int freq = allowed ? this.getConfigInt(FREQUENCY) : -100;
      freq += this.rand.nextInt(Math.max(10, freq / 2));
      final Block glowshroom = ForgeRegistries.BLOCKS.getValue(rlGlowshroom);
      final Block glowcelium = ForgeRegistries.BLOCKS.getValue(rlGlowcelium);
      final BlockState[] mushrooms = { glowshroom.getDefaultState() };
      final Block[] soils = { glowcelium };
      this.goalSelector.addGoal(2, new PlaceBlocksGoal(this, freq, mushrooms, soils, allowed));
    }
    // "heal at night" goal
    if (this.getConfigBool(ALLOW_HEALING)) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, Effects.REGENERATION, 50, 60, 1, 1,
          g -> !g.getEntityWorld().isDaytime() && g.getEntityWorld().getRandom().nextInt(450) == 0));
    }
  }

  @Override
  public boolean isProvidingLight() {
    return true;
  }

  @Override
  public float getBrightness() {
    return this.brightness;
  }
  
  @Override
  public boolean hasTransparency() {
    return true;
  }
}
