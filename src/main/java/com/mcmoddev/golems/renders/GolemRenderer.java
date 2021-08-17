package com.mcmoddev.golems.renders;

import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.proxies.ClientProxy;
import com.mcmoddev.golems.renders.model.GolemBannerLayer;
import com.mcmoddev.golems.renders.model.GolemFlowerLayer;
import com.mcmoddev.golems.renders.model.GolemKittyLayer;
import com.mcmoddev.golems.renders.model.GolemModel;
import com.mcmoddev.golems.renders.model.TexturesLayer;
import com.mcmoddev.golems.util.GolemRenderSettings;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

/**
 * GolemRenderer is the same as RenderIronGolem but with casting to GolemBase
 * instead of EntityIronGolem.
 */
public class GolemRenderer<T extends GolemBase> extends MobRenderer<T, GolemModel<T>> {

  protected static final ResourceLocation boneTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/bone_skeleton.png");
  protected static final ResourceLocation specialTexture = new ResourceLocation(ExtraGolems.MODID, "textures/entity/special.png");
  protected static final ResourceLocation specialTexture2 = new ResourceLocation(ExtraGolems.MODID, "textures/entity/special2.png");
  
  private static final Map<IronGolem.Crackiness, ResourceLocation> cracksToTextureMap = ImmutableMap.of(IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolem.Crackiness.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolem.Crackiness.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
  
  protected boolean isAlphaLayer;
  
  protected boolean disableLayers;

  /**
   * @param m the entity render manager
   **/
  public GolemRenderer(final EntityRendererProvider.Context context) {
    super(context, new GolemModel<T>(context.bakeLayer(ClientProxy.GOLEM_MODEL_RESOURCE)), 0.5F);
    this.addLayer(new TexturesLayer<>(this, context.getModelSet()));
    this.addLayer(new GolemFlowerLayer<T>(this));
    this.addLayer(new GolemKittyLayer<T>(this));
    this.addLayer(new GolemBannerLayer<T>(this));
  }

  @Override
  public void render(final T golem, final float entityYaw, final float partialTicks, final PoseStack matrixStackIn,
      final MultiBufferSource bufferIn, final int packedLightIn) {
    if(golem.isInvisible()) {
      return;
    }
    // get render settings
    final GolemRenderSettings settings = ExtraGolems.PROXY.GOLEM_RENDER_SETTINGS.get(golem.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    matrixStackIn.pushPose();
    // scale
    if (golem.isBaby()) {
      float scaleChild = 0.5F;
      matrixStackIn.scale(scaleChild, scaleChild, scaleChild);
    }
    // colors
    if(settings.getBaseColor() > 0) {
      final Vector3f colors = GolemRenderSettings.unpackColor(settings.getBaseColor());
      this.getModel().setColor(colors.x(), colors.y(), colors.z());
    } else {
      this.model.resetColor();
    }
    // transparency flag
    isAlphaLayer = settings.isTranslucent();
    if (isAlphaLayer) {
      // TODO ???
//      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
    }
    // packed light
    final int packedLight = settings.getBaseLight().orElse(settings.getBaseLight().orElse(false)) ? 15728880 : packedLightIn;
    // render the entity
    super.render(golem, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLight);
    if (isAlphaLayer) {
      RenderSystem.disableBlend();
    }
    matrixStackIn.popPose();
  }
  
  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless
   * you call Render.bindEntityTexture.
   */
  @Override
  public ResourceLocation getTextureLocation(final T golem) {
    final GolemRenderSettings settings = ExtraGolems.PROXY.GOLEM_RENDER_SETTINGS.get(golem.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    ResourceLocation texture = settings.getBase().first;
    disableLayers = false;
    // special cases
    if(ExtraGolemsConfig.halloween() && isNightTime(golem)) {
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
    return texture;
  }

  @Override
  @Nullable
  protected RenderType getRenderType(final T golem, boolean isVisible, boolean isVisibleToPlayer, boolean isGlowing) {
    final GolemRenderSettings settings = ExtraGolems.PROXY.GOLEM_RENDER_SETTINGS.get(golem.getMaterial()).orElse(GolemRenderSettings.EMPTY);
    ResourceLocation texture = this.getTextureLocation(golem);
    ResourceLocation template = settings.getBaseTemplate();
    boolean dynamic = isDynamic(texture, settings);
    if (isVisible || isVisibleToPlayer || isAlphaLayer) {
      return GolemRenderType.getGolemTransparent(texture, template, dynamic);
    } else if(isGlowing) {
      return GolemRenderType.getGolemOutline(texture, template, dynamic);
    } else {
      return GolemRenderType.getGolemCutout(texture, template, dynamic);
    }
  }
  
  protected boolean isDynamic(final ResourceLocation texture, final GolemRenderSettings settings) {
    return texture != boneTexture && texture != specialTexture && texture != specialTexture2 && settings.getBase().second;
  }
  
  public static boolean isNightTime(final GolemBase golem) {
    final long time = golem.level.getDayTime() % 24000L;
    return time > 13000L && time < 23000L;
  }
}
