package com.golems.renders;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelGolem extends ModelIronGolem {

  /**
   * Sets the models various rotation angles then renders the model.
   */
  @Override
  public void render(final Entity entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks,
      final float netHeadYaw, final float headPitch, final float scale) {
    GlStateManager.pushMatrix();
    this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
    // adjust scale for child golem
    if (this.isChild) {
      final float scaleChild = 0.5F;
      GlStateManager.scale(scaleChild, scaleChild, scaleChild);
      GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
    }
    // perform all other renders
    super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    GlStateManager.popMatrix();
  }
}
