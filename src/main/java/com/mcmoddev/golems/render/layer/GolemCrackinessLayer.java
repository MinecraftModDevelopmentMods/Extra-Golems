package com.mcmoddev.golems.render.layer;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.render.GolemModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;

public class GolemCrackinessLayer<T extends GolemBase> extends LayerRenderer<T, GolemModel<T>> {

  private static final Map<IronGolemEntity.Cracks, ResourceLocation> resourceLocations = ImmutableMap.of(IronGolemEntity.Cracks.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"), IronGolemEntity.Cracks.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"), IronGolemEntity.Cracks.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));

  public GolemCrackinessLayer(IEntityRenderer<T, GolemModel<T>> ientityrenderer) {
    super(ientityrenderer);
  }

  @Override
  public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, T entity,
					 float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
     if (!entity.isInvisible()) {
        IronGolemEntity.Cracks irongolem$crackiness = entity.func_226512_l_();
        if (irongolem$crackiness != IronGolemEntity.Cracks.NONE) {
           ResourceLocation resourcelocation = resourceLocations.get(irongolem$crackiness);        
           IVertexBuilder vertexconsumer = buffer.getBuffer(RenderType.getEntityTranslucent(resourcelocation));
           stack.push();
           RenderSystem.enableBlend();
           RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.5F);
           getEntityModel().render(stack, vertexconsumer, packedLight, MobRenderer.getPackedOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 0.5F);
           RenderSystem.disableBlend();
           stack.pop();
        }
     }
  }
}
