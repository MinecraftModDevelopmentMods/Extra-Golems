package com.mcmoddev.golems.render;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.client.GolemRenderSettings;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.layer.ColoredTextureLayer;
import com.mcmoddev.golems.render.layer.GolemBannerLayer;
import com.mcmoddev.golems.render.layer.GolemCrackinessLayer;
import com.mcmoddev.golems.render.layer.GolemFlowerLayer;
import com.mcmoddev.golems.render.layer.GolemKittyLayer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * GolemRenderer is the same as RenderIronGolem but with casting to GolemBase
 * instead of EntityIronGolem.
 */
public class GolemRenderer<T extends GolemBase> extends MobRenderer<T, GolemModel<T>> {
  
  public static final ModelLayerLocation GOLEM_MODEL_RESOURCE = new ModelLayerLocation(new ResourceLocation(ExtraGolems.MODID, "golem"), "main");

  protected static final ResourceLocation boneTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/bone_skeleton.png");
  protected static final ResourceLocation specialTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/special.png");
  protected static final ResourceLocation specialTexture2 = new ResourceLocation(ExtraGolems.MODID, "textures/entity/special2.png");
    
  private static final Vector3f ONE = new Vector3f(1.0F, 1.0F, 1.0F);
  
  protected boolean isAlphaLayer;
  
  /**
   * @param m the entity render manager
   **/
  public GolemRenderer(final EntityRendererProvider.Context context) {
    super(context, new GolemModel<>(context.bakeLayer(GOLEM_MODEL_RESOURCE)), 0.5F);
    this.addLayer(new ColoredTextureLayer<>(this, context.getModelSet()));
    this.addLayer(new GolemCrackinessLayer<>(this));
    this.addLayer(new GolemFlowerLayer<>(this));
    this.addLayer(new GolemKittyLayer<>(this));
    this.addLayer(new GolemBannerLayer<>(this));
  }

  @Override
  public void render(final T golem, final float entityYaw, final float partialTicks, final PoseStack matrixStackIn,
      final MultiBufferSource bufferIn, final int packedLightIn) {
    if(golem.isInvisible()) {
      return;
    }
    // get render settings
    Optional<GolemRenderSettings> settings = ExtraGolems.GOLEM_RENDER_SETTINGS.get(golem.getMaterial());
    if(!settings.isPresent()) {
      final ResourceLocation m = golem.getMaterial();
      ExtraGolems.LOGGER.error("Missing GolemRenderSettings at assets/" + m.getNamespace() + "/golem/" + m.getPath() + ".json");
      ExtraGolems.GOLEM_RENDER_SETTINGS.put(golem.getMaterial(), GolemRenderSettings.EMPTY);
      settings = Optional.of(GolemRenderSettings.EMPTY);
    }
    matrixStackIn.pushPose();
    // scale
    if (golem.isBaby()) {
      float scaleChild = 0.5F;
      matrixStackIn.scale(scaleChild, scaleChild, scaleChild);
    }
    // colors
    final Vector3f colors;
    if(settings.get().getBaseColor().isPresent() && settings.get().getBaseColor().get() > 0) {
      colors = GolemRenderSettings.unpackColor(settings.get().getBaseColor().get());
    } else if(settings.get().useBiomeColor()) {
      colors = GolemRenderSettings.unpackColor(golem.getBiomeColor());
    } else {
      colors = ONE;
    }
    this.getModel().setColor(colors.x(), colors.y(), colors.z());
    // transparency flag
    isAlphaLayer = settings.get().isTranslucent();
    if (isAlphaLayer) {
      RenderSystem.enableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
    }
    // packed light
    final int packedLight = settings.get().getBaseLight().orElse(settings.get().getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
    // render the entity
    super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLight);
    if (isAlphaLayer) {
      RenderSystem.disableBlend();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
    matrixStackIn.popPose();
  }
  
  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless
   * you call Render.bindEntityTexture.
   */
  @Override
  public ResourceLocation getTextureLocation(final T golem) {
    final GolemRenderSettings settings = ExtraGolems.GOLEM_RENDER_SETTINGS.get(golem.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    ResourceLocation texture = settings.getBase(golem).resource();
    boolean disableLayers = false;
    // special cases
    if(EGConfig.halloween() && isNightTime(golem)) {
      texture = boneTexture;
      disableLayers = true;
    } else if(golem.hasCustomName()) {
      final String s = ChatFormatting.stripFormatting(golem.getName().getString());
      if("Ganondorf".equals(s)) {
        texture = specialTexture;
        disableLayers = true;
      }
      if("Cookie".equals(s)) {
        texture = specialTexture2;
        disableLayers = true;
      }
    }
    this.getModel().disableLayers(disableLayers);
    return texture;
  }

  @Override
  @Nullable
  protected RenderType getRenderType(final T golem, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    final GolemRenderSettings settings = ExtraGolems.GOLEM_RENDER_SETTINGS.get(golem.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    ResourceLocation texture = this.getTextureLocation(golem);
    ResourceLocation template = settings.getBaseTemplate();
    boolean dynamic = isDynamic(golem, texture, settings);
    if (isVisible || isVisibleToPlayer || isAlphaLayer) {
      return GolemRenderType.getGolemTranslucent(texture, template, dynamic);
    } else if(isGlowing) {
      return GolemRenderType.getGolemOutline(texture, template, dynamic);
    } else {
      return GolemRenderType.getGolemCutout(texture, template, dynamic);
    }
  }
  
  protected static boolean isSpecial(final ResourceLocation texture) {
    return texture == boneTexture || texture == specialTexture || texture == specialTexture2;
  }
  
  protected static <T extends GolemBase> boolean isDynamic(final T entity, final ResourceLocation texture, final GolemRenderSettings settings) {
    return !isSpecial(texture) && !settings.getBase(entity).flag();
  }
  
  public static boolean isNightTime(final GolemBase golem) {
    final long time = golem.level.getDayTime() % 24000L;
    return time > 13000L && time < 23000L;
  }
}
