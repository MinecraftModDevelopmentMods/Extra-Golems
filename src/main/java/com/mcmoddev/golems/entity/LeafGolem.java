package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.ai.PassiveEffectsGoal;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemColorized;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;

import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public final class LeafGolem extends GolemColorized {

  public static final String ALLOW_SPECIAL = "Allow Special: Regeneration";

  private static final ResourceLocation TEXTURE_BASE = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.LEAF_GOLEM);
  private static final ResourceLocation TEXTURE_OVERLAY = GolemBase.makeTexture(ExtraGolems.MODID, GolemNames.LEAF_GOLEM + "_grayscale");

  public LeafGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, 0x5F904A, TEXTURE_BASE, TEXTURE_OVERLAY);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    if (this.getConfigBool(ALLOW_SPECIAL)) {
      this.goalSelector.addGoal(4, new PassiveEffectsGoal(this, Effects.REGENERATION, 200, 360, 0, 1,
          PassiveEffectsGoal.doesNotHaveEffect(Effects.REGENERATION).and(g -> g.getEntityWorld().getRandom().nextInt(40) == 0)));
    }
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void livingTick() {
    super.livingTick();

    // update color
    if (this.ticksExisted % 10 == 2 && this.world.isRemote && !this.isServerWorld()) {
      // this.world.getBiomeManager().getBiome(BlockPos)
      Biome biome = this.world.getBiome(this.getPositionUnderneath().up(2));
      int color = biome.getFoliageColor();
      this.setColor(color);
    }

    // slow falling for this entity
    if (this.getMotion().y < -0.05D) {
      this.setMotion(this.getMotion().mul(1.0D, 0.75D, 1.0D));
    }
  }
}
