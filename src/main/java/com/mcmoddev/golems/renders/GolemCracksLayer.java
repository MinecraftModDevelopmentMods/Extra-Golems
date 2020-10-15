package com.mcmoddev.golems.renders;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;

public class GolemCracksLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>>  {
  private static final Map<IronGolemEntity.Cracks, ResourceLocation> cracksToTextureMap = ImmutableMap.of(IronGolemEntity.Cracks.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolemEntity.Cracks.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolemEntity.Cracks.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

  public GolemCracksLayer(IEntityRenderer<T, GolemModel<T>> ientityrenderer) {
     super(ientityrenderer);
  }

  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
     if (!entitylivingbaseIn.isInvisible()) {
        IronGolemEntity.Cracks irongolementity$cracks = entitylivingbaseIn.func_226512_l_();
        if (irongolementity$cracks != IronGolemEntity.Cracks.NONE) {
           ResourceLocation resourcelocation = cracksToTextureMap.get(irongolementity$cracks);
           renderCutoutModel(this.getEntityModel(), resourcelocation, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, 1.0F, 1.0F, 1.0F);
        }
     }
  }
}
