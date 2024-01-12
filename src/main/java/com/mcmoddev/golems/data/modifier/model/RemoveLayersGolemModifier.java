package com.mcmoddev.golems.data.modifier.model;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.model.Layer;
import com.mcmoddev.golems.data.model.RenderTypes;
import com.mcmoddev.golems.data.modifier.GolemModifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import java.util.function.Predicate;

@Immutable
public class RemoveLayersGolemModifier extends GolemModifier {

	public static final Codec<RemoveLayersGolemModifier> CODEC = RemovePredicate.CODEC
			.xmap(RemoveLayersGolemModifier::new, RemoveLayersGolemModifier::getPredicate)
			.fieldOf("predicate").codec();

	private final RemovePredicate predicate;

	public RemoveLayersGolemModifier(RemovePredicate predicate) {
		this.predicate = predicate;
	}

	//// GETTERS ////

	public RemovePredicate getPredicate() {
		return this.predicate;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.model(b -> b.remove(this.getPredicate()));
	}

	@Override
	public Codec<? extends GolemModifier> getCodec() {
		return EGRegistry.GolemModifierReg.REMOVE_LAYERS.get();
	}

	//// CLASSES ////

	public static class RemovePredicate implements Predicate<Layer> {

		public static final Codec<RemovePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourcePair.CODEC.optionalFieldOf("texture").forGetter(o -> Optional.ofNullable(o.texture)),
				ResourceLocation.CODEC.optionalFieldOf("template").forGetter(o -> Optional.ofNullable(o.template)),
				Codec.BOOL.optionalFieldOf("emissive").forGetter(o -> Optional.ofNullable(o.emissive)),
				Codec.BOOL.optionalFieldOf("use_biome_color").forGetter(o -> Optional.ofNullable(o.useBiomeColor)),
				RenderTypes.CODEC.optionalFieldOf("render_type").forGetter(o -> Optional.ofNullable(o.renderType)),
				EGCodecUtils.MIN_MAX_INTS_CODEC.optionalFieldOf("variant").forGetter(o -> Optional.ofNullable(o.variant))
		).apply(instance, RemovePredicate::new));


		private final @Nullable ResourcePair texture;
		private final @Nullable ResourceLocation template;
		private final @Nullable Boolean emissive;
		private final @Nullable Boolean useBiomeColor;
		private final @Nullable RenderTypes renderType;
		private final @Nullable MinMaxBounds.Ints variant;

		public RemovePredicate(Optional<ResourcePair> texture, Optional<ResourceLocation> template, Optional<Boolean> emissive,
							   Optional<Boolean> useBiomeColor, Optional<RenderTypes> renderType, Optional<MinMaxBounds.Ints> variant) {
			this.texture = texture.orElse(null);
			this.template = template.orElse(null);
			this.emissive = emissive.orElse(null);
			this.useBiomeColor = useBiomeColor.orElse(null);
			this.renderType = renderType.orElse(null);
			this.variant = variant.orElse(null);
		}


		@Override
		public boolean test(Layer layer) {
			if(texture != null && !texture.equals(layer.getRawTexture())) {
				return false;
			}
			if(template != null && !template.equals(layer.getRawTemplate())) {
				return false;
			}
			if(emissive != null && emissive != layer.isEmissive()) {
				return false;
			}
			if(useBiomeColor != null && useBiomeColor != layer.useBiomeColor()) {
				return false;
			}
			if(renderType != null && renderType != layer.getRenderType()) {
				return false;
			}
			if(variant != null && !testMinMaxBounds(variant, layer.getVariantBounds())) {
				return false;
			}
			// all checks passed
			return true;
		}

		/**
		 * @param predicate the bounds to check if they are in the range
		 * @param bounds the range
		 * @return true if the predicate is ANY,
		 */
		private boolean testMinMaxBounds(final MinMaxBounds.Ints predicate, final MinMaxBounds.Ints bounds) {
			if(predicate.isAny() || bounds.isAny()) {
				return true;
			}
			// TODO test to make sure this works
			if(bounds.getMin() != null && bounds.getMax() != null
					&& (predicate.matches(bounds.getMin()) || predicate.matches(bounds.getMax()))) {
				return true;
			}
			if(bounds.getMin() != null && predicate.matches(bounds.getMin())) {
				return true;
			}
			if(bounds.getMax() != null && predicate.matches(bounds.getMax())) {
				return true;
			}
			return false;
		}
	}
}
