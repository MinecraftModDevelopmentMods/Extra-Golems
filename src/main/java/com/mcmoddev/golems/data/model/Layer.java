package com.mcmoddev.golems.data.model;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.goal.IVariantPredicate;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.data.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
public class Layer implements IVariantPredicate {

	public static final Layer RAINBOW = new Layer.Builder(new ResourcePair(new ResourceLocation(ExtraGolems.MODID, "rainbow_vines"), true)).build();

	public static final Codec<Layer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourcePair.CODEC.fieldOf("texture").forGetter(o -> o.rawTexture),
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
		if(texture.flag()) {
			this.texture = new ResourcePair(new ResourceLocation(texture.resource().getNamespace(), "textures/entity/golem/" + texture.resource().getPath() + ".png"), texture.flag());
		} else {
			this.texture = new ResourcePair(new ResourceLocation(texture.resource().getNamespace(), "textures/block/" + texture.resource().getPath() + ".png"), texture.flag());
		}
		this.rawTemplate = template.orElse(null);
		this.template = template.map(id -> new ResourceLocation(id.getNamespace(), "textures/entity/golem/" + id.getPath() + ".png")).orElse(null);
		this.emissive = emissive;
		this.color = color;
		this.colors = Vec3.fromRGB24(color);
		this.useBiomeColor = useBiomeColor;
		this.renderType = renderType;
		this.variant = variant;
	}

	//// VARIANT PREDICATE ////

	@Override
	public MinMaxBounds.Ints getVariantBounds() {
		return variant;
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
	
	//// CLASSES ////
	
	public static class Builder {
		private ResourcePair texture;
		private Optional<ResourceLocation> template;
		private boolean emissive;
		private int color;
		private boolean useBiomeColor;
		private RenderTypes renderType;
		private MinMaxBounds.Ints variant;

		public Builder(ResourcePair texture) {
			this.texture = texture;
			this.template = Optional.empty();
			this.emissive = false;
			this.color = 0xFFFFFF;
			this.useBiomeColor = false;
			this.renderType = RenderTypes.CUTOUT;
			this.variant = MinMaxBounds.Ints.ANY;
		}

		/**
		 * @param texture the texture resource pair
		 * @return the builder instance
		 */
		public Builder texture(ResourcePair texture) {
			this.texture = texture;
			return this;
		}

		/**
		 * @param template the template resource location
		 * @return the builder instance
		 */
		public Builder template(@Nullable ResourceLocation template) {
			this.template = Optional.ofNullable(template);
			return this;
		}

		/**
		 * @param emissive true to render as an emissive texture
		 * @return the builder instance
		 */
		public Builder emissive(boolean emissive) {
			this.emissive = emissive;
			return this;
		}

		/**
		 * @param color the layer color, defaults to 0xFFFFFF
		 * @return the builder instance
		 */
		public Builder color(int color) {
			this.color = color;
			return this;
		}

		/**
		 * @param useBiomeColor {@code true} to use the biome color instead of the layer color
		 * @return the builder instance
		 */
		public Builder useBiomeColor(boolean useBiomeColor) {
			this.useBiomeColor = useBiomeColor;
			return this;
		}

		/**
		 * @param renderType the render type identifier
		 * @return the builder instance
		 */
		public Builder renderType(RenderTypes renderType) {
			this.renderType = renderType;
			return this;
		}

		/**
		 * @param variant the variant bounds to render this layer
		 * @return the builder instance
		 */
		public Builder variant(MinMaxBounds.Ints variant) {
			this.variant = variant;
			return this;
		}

		public Layer build() {
			return new Layer(texture, template, emissive, color, useBiomeColor, renderType, variant);
		}
	}
}
