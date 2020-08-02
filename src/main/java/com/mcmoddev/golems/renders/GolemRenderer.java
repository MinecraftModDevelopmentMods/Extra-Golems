package com.mcmoddev.golems.renders;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolemsEntities;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

/**
 * GolemRenderer is the same as RenderIronGolem but with casting to GolemBase
 * instead of EntityIronGolem.
 */
public class GolemRenderer<T extends GolemBase> extends MobRenderer<T, GolemModel<T>> {

  protected static final ResourceLocation fallbackTexture = ExtraGolemsEntities.makeTexture(GolemNames.CLAY_GOLEM);
  protected static final ResourceLocation boneTexture = ExtraGolemsEntities.makeTexture(GolemNames.BONE_GOLEM + "_skeleton");
  protected static final ResourceLocation specialTexture = ExtraGolemsEntities.makeTexture("special");
  protected static final ResourceLocation specialTexture2 = ExtraGolemsEntities.makeTexture("special2");
  protected ResourceLocation texture;

  protected static final String damageTexture = "minecraft:textures/entity/iron_golem/iron_golem_crackiness";
  protected static final ResourceLocation[] damageIndicators = { 
      new ResourceLocation(damageTexture + "_low.png"),
      new ResourceLocation(damageTexture + "_medium.png"),
      new ResourceLocation(damageTexture + "_high.png")
  };

  protected static final float DAMAGE_ALPHA = 0.55F;
  
  protected boolean isAlphaLayer;

  public GolemRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GolemModel<T>(), 0.5F);
    this.addLayer(new GolemFlowerLayer<T>(this));
  }

  @Override
  public void render(final T golem, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    // render the golem
    this.bindGolemTexture(golem);
    this.resetColor();
    isAlphaLayer = isAlphaLayer(golem);
    super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    // render damage indicator texture
    isAlphaLayer = true;
    this.renderDamage(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
  }

  protected void bindGolemTexture(final T golem) {
    texture = golem.getTexture();
    // special cases
    if(ExtraGolemsConfig.halloween() && isNightTime(golem)) {
      texture = boneTexture;
    } else if(golem.hasCustomName()) {
      final String s = TextFormatting.getTextWithoutFormattingCodes(golem.getName().getString());
      if("Ganondorf".equals(s)) {
        texture = specialTexture;
      }
      if("Cookie".equals(s)) {
        texture = specialTexture2;
      }
    }
  }

  protected void resetColor() {
    this.entityModel.resetColor();
  }
  
  /**
   * Called just before rendering the body layer
   * @param golem the golem
   * @return whether the golem should be rendered as translucent
   **/
  protected boolean isAlphaLayer(final T golem) {
    return golem.hasTransparency();
  }

  protected void renderDamage(final T golem, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    // render damage indicator if necessary
    final int index = Math.min(getDamageTexture(golem), damageIndicators.length - 1);
    if (index > -1) {
      matrixStackIn.push();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      // set alpha
      this.entityModel.setAlpha(DAMAGE_ALPHA);
      // actually render the damage texture
      this.texture = damageIndicators[index];
      super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      // return GL settings to normal
      RenderSystem.disableAlphaTest();
      RenderSystem.disableBlend();
      matrixStackIn.pop();
    }
  }

  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless
   * you call Render.bindEntityTexture.
   */
  @Override
  public ResourceLocation getEntityTexture(final T golem) {
    return this.texture != null ? this.texture : fallbackTexture;
  }

  @Override
  @Nullable
  protected RenderType func_230496_a_(final T golem, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    ResourceLocation tex = this.getEntityTexture(golem);
    if (isVisible || isVisibleToPlayer || golem.hasTransparency() || isAlphaLayer) {
      return RenderType.getEntityTranslucent(tex);
    } else {
      return golem.isGlowing() ? RenderType.getOutline(tex) : RenderType.getEntityCutout(tex);
    }
  }

  /**
   * @return a value between {@code -1} and {@code damageIndicators.length-1},
   *         inclusive
   **/
  protected int getDamageTexture(final T golem) {
    final float percentHealth = golem.getHealth() / golem.getMaxHealth();
    return damageIndicators.length - (int) Math.ceil(percentHealth * 4.0F);
  }
  
  protected boolean isNightTime(final T golem) {
    final long time = golem.world.getDayTime() % 24000L;
    return time > 13000L && time < 23000L;
  }
}
