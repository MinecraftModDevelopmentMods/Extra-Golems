package com.mcmoddev.golems.renders;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.renders.model.GolemFlowerLayer;
import com.mcmoddev.golems.renders.model.GolemKittyLayer;
import com.mcmoddev.golems.renders.model.GolemModel;
import com.mcmoddev.golems.renders.model.SimpleTextureLayer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;

/**
 * GolemRenderer is the same as RenderIronGolem but with casting to GolemBase
 * instead of EntityIronGolem.
 */
public class GolemRenderer<T extends GolemBase> extends MobRenderer<T, GolemModel<T>> {

  protected static final ResourceLocation boneTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/" + GolemNames.BONE_GOLEM + "_skeleton.png");
  protected static final ResourceLocation specialTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/special.png");
  protected static final ResourceLocation specialTexture2 = new ResourceLocation(ExtraGolems.MODID, "textures/entity/special2.png");
  
  private static final Map<IronGolemEntity.Cracks, ResourceLocation> cracksToTextureMap = ImmutableMap.of(IronGolemEntity.Cracks.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolemEntity.Cracks.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolemEntity.Cracks.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
  
  protected boolean hideVines;
  protected boolean hideEyes;
  protected boolean isAlphaLayer;

  /**
   * @param renderManagerIn the entity render manager
   **/
  public GolemRenderer(final EntityRendererManager renderManagerIn) {
    super(renderManagerIn, new GolemModel<T>(), 0.5F);
  }
  
  /**
   * Used to add a single layer to the renderer
   * @param layer the layer renderer
   * @return instance to allow chaining
   **/
  public GolemRenderer<T> withLayer(final LayerRenderer<T, GolemModel<T>> layer) {
    this.addLayer(layer);
    return this;
  }
  
  /**
   * Used to add all the default layers, in this order:
   * Eyes, Vines, Cracks, Flower, Kitty
   * @return instance to allow chaining
   **/
  public GolemRenderer<T> withAllLayers() {
    // Eyes layer
    this.addLayer(new SimpleTextureLayer<T>(this, 
        g -> this.hideEyes ? null : g.getGolemContainer().getRenderSettings().getEyesTexture().getTexture(g),
        g -> 0xFFFFFF, g -> g.getGolemContainer().getRenderSettings().getEyesLighting().disableLighting(g), 1.0F));
    // Vines layer
    this.addLayer(new SimpleTextureLayer<T>(this, 
        g -> {
          final GolemRenderSettings settings = g.getGolemContainer().getRenderSettings();
          return settings.hasVines() && !this.hideVines ? settings.getVinesTexture().getTexture(g) : null;
        }, g -> g.getGolemContainer().getRenderSettings().getVinesColorProvider().getColor(g), 
        g -> g.getGolemContainer().getRenderSettings().getVinesLighting().disableLighting(g), 1.0F));
    // Cracks layer
    this.addLayer(new SimpleTextureLayer<T>(this, g -> cracksToTextureMap.get(g.func_226512_l_()), g -> 0xFFFFFF, g -> false, 0.55F));
    this.addLayer(new GolemFlowerLayer<T>(this));
    this.addLayer(new GolemKittyLayer<T>(this));
    return this;
  }

  @Override
  public void render(final T golem, final float entityYaw, final float partialTicks, final MatrixStack matrixStackIn,
      final IRenderTypeBuffer bufferIn, final int packedLightIn) {
    if(golem.isInvisible()) {
      return;
    }
    // get render settings from the golem container
    final GolemRenderSettings settings = golem.getGolemContainer().getRenderSettings();
    matrixStackIn.push();
    // scale
    if (golem.isChild()) {
      float scaleChild = 0.5F;
      matrixStackIn.scale(scaleChild, scaleChild, scaleChild);
      //matrixStackIn.translate(0.0F, 1.5F, 0.0F);
    }
    // colors
    if(settings.hasColor()) {
      final Vector3f colors = GolemRenderSettings.unpackColor(settings.getBlockColorProvider().getColor(golem));
      this.getEntityModel().setColor(colors.getX(), colors.getY(), colors.getZ());
    } else {
      this.entityModel.resetColor();
    }
    // transparency flag
    isAlphaLayer = settings.hasTransparency();
    if (isAlphaLayer) {
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
    }
    // lighting
    final int packedLight = settings.getTextureLighting().disableLighting(golem) ? 15728880 : packedLightIn;
    // render the golem
    super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLight);
    if (isAlphaLayer) {
      RenderSystem.disableBlend();
    }
    matrixStackIn.pop();
  }
  
  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless
   * you call Render.bindEntityTexture.
   */
  @Override
  public ResourceLocation getEntityTexture(final T golem) {
    final GolemRenderSettings settings = golem.getGolemContainer().getRenderSettings();
    ResourceLocation texture;
    hideVines = false;
    hideEyes = false;
    if(settings.hasPrefabTexture()) {
      texture = settings.getPrefabTexture().getTexture(golem);
    } else {
      texture = settings.getBlockTexture().getTexture(golem);
    }
    // special cases
    if(ExtraGolemsConfig.halloween() && isNightTime(golem)) {
      texture = boneTexture;
      hideVines = true;
    } else if(golem.hasCustomName()) {
      final String s = TextFormatting.getTextWithoutFormattingCodes(golem.getName().getString());
      if("Ganondorf".equals(s)) {
        texture = specialTexture;
        hideVines = true;
        hideEyes = true;
      }
      if("Cookie".equals(s)) {
        texture = specialTexture2;
        hideVines = true;
        hideEyes = true;
      }
    }
    return texture;
  }

  @Override
  @Nullable
  protected RenderType func_230496_a_(final T golem, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    final GolemRenderSettings settings = golem.getGolemContainer().getRenderSettings();
    ResourceLocation texture = this.getEntityTexture(golem);
    boolean dynamic = isDynamic(texture, settings);
    if (isVisible || isVisibleToPlayer || isAlphaLayer) {
      return GolemRenderType.getGolemTransparent(texture, dynamic);
    } else if(golem.isGlowing()) {
      return GolemRenderType.getGolemOutline(texture, dynamic);
    } else {
      return GolemRenderType.getGolemCutout(texture, dynamic);
    }
  }
  
  protected boolean isDynamic(final ResourceLocation texture, final GolemRenderSettings settings) {
    return texture != boneTexture && texture != specialTexture && texture != specialTexture2 && !settings.hasPrefabTexture();
  }

  public static boolean isNightTime(final GolemBase golem) {
    final long time = golem.world.getDayTime() % 24000L;
    return time > 13000L && time < 23000L;
  }
}
