package com.mcmoddev.golems.data.model;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public class Layer {

	public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourcePair.CODEC.optionalFieldOf("texture", ResourcePair.EMPTY).forGetter(o -> o.rawTexture),
			ResourceLocation.CODEC.optionalFieldOf("template").forGetter(o -> Optional.ofNullable(o.rawTemplate)),
			Codec.BOOL.optionalFieldOf("emissive", false).forGetter(Layer::isEmissive),
			EGCodecUtils.HEX_OR_INT_CODEC.optionalFieldOf("color", 0xFFFFFF).forGetter(Layer::getPackedColor),
			Codec.BOOL.optionalFieldOf("use_biome_color", false).forGetter(Layer::useBiomeColor),
			RenderTypes.CODEC.optionalFieldOf("render_type", RenderTypes.CUTOUT).forGetter(Layer::getRenderType),
			EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant", MinMaxBounds.Ints.ANY).forGetter(Layer::getVariantBounds)
	).apply(instance, Layer::new));

	private final ResourcePair rawTexture;
	private final ResourcePair texture;
	private final @Nullable ResourceLocation rawTemplate;
	private final @Nullable ResourceLocation template;
	private final boolean emissive;
	private final int color;
	private final boolean useBiomeColor;
	private final Vec3 colors;
	private final RenderTypes renderType;
	private final MinMaxBounds.Ints variant;

	public Layer(ResourcePair texture, Optional<ResourceLocation> template, boolean emissive, int color, boolean useBiomeColor,
				 RenderTypes renderType, MinMaxBounds.Ints variant) {
		this.rawTexture = texture;
		this.texture = new ResourcePair(new ResourceLocation(texture.resource().getNamespace(), "textures/entity/golem/" + texture.resource().getPath() + ".png"), texture.flag());
		this.rawTemplate = template.orElse(null);
		this.template = template.map(id -> new ResourceLocation(id.getNamespace(), "textures/entity/golem/" + id.getPath() + ".png")).orElse(null);
		this.emissive = emissive;
		this.color = color;
		this.colors = Vec3.fromRGB24(color);
		this.useBiomeColor = useBiomeColor;
		this.renderType = renderType;
		this.variant = variant;
	}

	public boolean isInBounds(final int variant) {
		return this.variant.matches(variant);
	}

	//// GETTERS ////


	public ResourcePair getRawTexture() {
		return rawTexture;
	}

	@Nullable
	public ResourceLocation getRawTemplate() {
		return rawTemplate;
	}

	public ResourcePair getTexture() {
		return texture;
	}

	@Nullable
	public ResourceLocation getTemplate() {
		return template;
	}

	public boolean isEmissive() {
		return emissive;
	}

	public int getPackedColor() {
		return color;
	}

	public boolean useBiomeColor() {
		return useBiomeColor;
	}

	public Vec3 getColors() {
		return colors;
	}

	public RenderTypes getRenderType() {
		return renderType;
	}

	public MinMaxBounds.Ints getVariantBounds() {
		return variant;
	}
}
