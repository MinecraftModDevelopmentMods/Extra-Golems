package com.mcmoddev.golems.container.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.util.ResourcePair;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.IForgeRegistryInternal;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class GolemRenderSettings {

	public static final ResourceLocation FALLBACK_PREFAB = new ResourceLocation("minecraft", "textures/entity/iron_golem/iron_golem.png");
	public static final ResourceLocation FALLBACK_BLOCK = new ResourceLocation("minecraft", "textures/block/clay.png");
	public static final ResourceLocation BASE_TEMPLATE = new ResourceLocation(ExtraGolems.MODID, "template");

	public static final ResourcePair FALLBACK_TEXTURE = new ResourcePair(FALLBACK_PREFAB, true);
	public static final ResourcePair NOT_LOADED = new ResourcePair(new ResourceLocation("not_loaded"), true);

	public static final GolemRenderSettings EMPTY = new GolemRenderSettings(
			ImmutableList.of(new ResourcePair(new ResourceLocation("minecraft", "iron_golem/iron_golem"), true)),
			BASE_TEMPLATE, Optional.empty(), false, Optional.empty(), false, Optional.empty(),
			LayerRenderSettings.EMPTY, LayerRenderSettings.EMPTY, ImmutableList.of());

	public static final Codec<GolemRenderSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.either(ResourcePair.CODEC, ResourcePair.CODEC.listOf())
					.xmap(either -> either.map(ImmutableList::of, Function.identity()),
							list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list))
					.optionalFieldOf("base", Lists.newArrayList()).forGetter(GolemRenderSettings::getBaseList),
			ResourceLocation.CODEC.optionalFieldOf("base_template", BASE_TEMPLATE).forGetter(GolemRenderSettings::getBaseTemplateRaw),
			Codec.INT.optionalFieldOf("base_color").forGetter(GolemRenderSettings::getBaseColor),
			Codec.BOOL.optionalFieldOf("use_biome_color", false).forGetter(GolemRenderSettings::useBiomeColor),
			Codec.BOOL.optionalFieldOf("base_light").forGetter(GolemRenderSettings::getBaseLight),
			Codec.BOOL.optionalFieldOf("translucent", false).forGetter(GolemRenderSettings::isTranslucent),
			MultitextureRenderSettings.CODEC.optionalFieldOf("multitexture").forGetter(GolemRenderSettings::getMultitexture),
			LayerRenderSettings.CODEC.optionalFieldOf("eyes", LayerRenderSettings.EYES).forGetter(GolemRenderSettings::getEyes),
			LayerRenderSettings.CODEC.optionalFieldOf("vines", LayerRenderSettings.VINES).forGetter(GolemRenderSettings::getVines),
			LayerRenderSettings.CODEC.listOf().optionalFieldOf("layers", Lists.newArrayList()).forGetter(GolemRenderSettings::getLayers)
	).apply(instance, GolemRenderSettings::new));

	private final List<ResourcePair> baseList;
	private ResourcePair base;
	private final ResourceLocation baseTemplateRaw;
	private ResourceLocation baseTemplate;
	private final Optional<Integer> baseColor;
	private final boolean useBiomeColor;
	private final Optional<Boolean> baseLight;
	private final boolean translucent;
	private final Optional<MultitextureRenderSettings> multitexture;
	private final LayerRenderSettings eyes;
	private final LayerRenderSettings vines;
	private final List<LayerRenderSettings> layers;

	private GolemRenderSettings(List<ResourcePair> baseList, ResourceLocation baseTemplate,
								Optional<Integer> baseColor, boolean useBiomeColor,
								Optional<Boolean> baseLight, boolean translucent,
								Optional<MultitextureRenderSettings> multitexture,
								LayerRenderSettings eyes, LayerRenderSettings vines,
								List<LayerRenderSettings> layers) {
		this.base = NOT_LOADED;
		this.baseList = baseList;
		this.baseColor = baseColor;
		this.baseTemplateRaw = baseTemplate;
		this.baseLight = baseLight;
		this.useBiomeColor = useBiomeColor;
		this.translucent = translucent;
		this.multitexture = multitexture;
		this.eyes = eyes;
		this.vines = vines;
		// create list of layer render settings
		ImmutableList.Builder<LayerRenderSettings> builder = ImmutableList.builder();
		builder.addAll(layers);
		// optionally add eyes to list
		if (eyes.isEnabled()) {
			builder.add(eyes);
		}
		// optionally add vines to list
		if (vines.isEnabled()) {
			builder.add(vines);
		}
		this.layers = builder.build();

		// validate multitexture
		if (getBaseList().isEmpty() && !getMultitexture().isPresent()) {
			ExtraGolems.LOGGER.warn("Error parsing GolemRenderSettings: Missing either 'base' or 'multitexture', exactly one must be defined");
		}
		if (!getBaseList().isEmpty() && getMultitexture().isPresent()) {
			ExtraGolems.LOGGER.warn("Found both 'base' and 'multitexture' in GolemRenderSettings. Ignoring 'base'");
		}
	}

	/**
	 * @return a List of ResourcePairs of base textures
	 **/
	private List<ResourcePair> getBaseList() {
		return baseList;
	}

	/**
	 * @return a ResourceLocation and Boolean of the base texture,
	 * ignoring multitexture settings.
	 * The Boolean is true for a block texture and
	 * false for a prefab texture;
	 */
	public ResourcePair getBase() {
		return base;
	}

	/**
	 * @return a color to apply to the base texture
	 **/
	public Optional<Integer> getBaseColor() {
		return baseColor;
	}

	/**
	 * @return a prefab template texture to use
	 **/
	public ResourceLocation getBaseTemplate() {
		return baseTemplate;
	}

	/**
	 * @return a prefab template texture to use
	 **/
	public ResourceLocation getBaseTemplateRaw() {
		return baseTemplateRaw;
	}

	/**
	 * @return an Optional containing the light for the layer.
	 * If empty, use the Golem light
	 */
	public Optional<Boolean> getBaseLight() {
		return baseLight;
	}

	/**
	 * @return true to use the biome color instead of {@link #getBaseColor()}
	 **/
	public boolean useBiomeColor() {
		return useBiomeColor;
	}

	/**
	 * @return whether the texture should be rendered translucent
	 **/
	public boolean isTranslucent() {
		return translucent;
	}

	/**
	 * @return the LayerRenderSettings for the eyes, if any
	 **/
	public LayerRenderSettings getEyes() {
		return eyes;
	}

	/**
	 * @return the LayerRenderSettings for the vines, if any
	 **/
	public LayerRenderSettings getVines() {
		return vines;
	}

	/**
	 * @return a List of LayerRenderSettings, may be empty
	 **/
	public List<LayerRenderSettings> getLayers() {
		return layers;
	}

	/**
	 * @return the GolemMultiTextureRenderSettings, if present
	 **/
	public Optional<MultitextureRenderSettings> getMultitexture() {
		return multitexture;
	}

	/**
	 * @param entity the Golem
	 * @return a ResourceLocation and Boolean of the base texture,
	 * taking into account multitexture settings.
	 * The Boolean is true for a block texture and
	 * false for a prefab texture;
	 */
	public ResourcePair getBase(final GolemBase entity) {
		if (multitexture.isPresent()) {
			return multitexture.get().getBaseMap().getOrDefault(entity.getTextureId(), FALLBACK_TEXTURE);
		}
		return getBase();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("GolemRenderSettings: ");
		b.append("base[").append(base).append("] ");
		b.append("template[").append(baseTemplate).append("] ");
		b.append("color[").append(baseColor).append("] ");
		b.append("biome_color[").append(useBiomeColor).append("] ");
		b.append("light[").append(baseLight).append("] ");
		b.append("translucent[").append(translucent).append("] ");
		b.append("multitexture[").append(multitexture).append("] ");
		b.append("layers[").append(layers).append("] ");
		return b.toString();
	}

	/**
	 * Separates a hex color into RGB components.
	 * If the color int is less than 0, returns
	 * a Vector3f with all 1.0s
	 *
	 * @param color a packed int RGB color
	 * @return the red, green, and blue components as a Vector3f
	 **/
	public static Vector3f unpackColor(final int color) {
		long tmpColor = color;
		if ((tmpColor & -67108864) == 0) {
			tmpColor |= -16777216;
		}
		float colorRed = (float) (tmpColor >> 16 & 255) / 255.0F;
		float colorGreen = (float) (tmpColor >> 8 & 255) / 255.0F;
		float colorBlue = (float) (tmpColor & 255) / 255.0F;
		return new Vector3f(colorRed, colorGreen, colorBlue);
	}

	public static class ClientUtils {

		/**
		 * Called by the packet handler when this class is received on the client
		 *
		 * @return true if this method was called client-side
		 */
		public static void loadSettings(final GolemRenderSettings settings) {
			// load base
			settings.base = settings.multitexture.isPresent()
					? new ResourcePair(new ResourceLocation("multitexture"), true)
					: buildPreferredTexture(settings.baseList);
			// load template
			settings.baseTemplate = new ResourceLocation(settings.baseTemplateRaw.getNamespace(), "textures/entity/golem/" + settings.baseTemplateRaw.getPath() + ".png");
			// load multitexture
			settings.multitexture.ifPresent(MultitextureRenderSettings.ClientUtils::loadMultitextureSettings);
			settings.layers.forEach(LayerRenderSettings.ClientUtil::loadLayerRenderSettings);
		}

		/**
		 * Accepts a list of texture strings and loads the first one that actually exists
		 *
		 * @param textureList the texture list
		 * @return a ResourcePair containing the first texture that loads
		 */
		public static ResourcePair buildPreferredTexture(final List<ResourcePair> textureList) {
			// attempt to locate each texture in the list until one is successful
			for (final ResourcePair texture : textureList) {
				ResourceLocation loadedTexture = null;
				// parse resource location
				if (texture.flag()) {
					loadedTexture = new ResourceLocation(texture.resource().getNamespace(), "textures/entity/golem/" + texture.resource().getPath() + ".png");
				} else {
					loadedTexture = new ResourceLocation(texture.resource().getNamespace(), "textures/block/" + texture.resource().getPath() + ".png");
				}
				// attempt to load the resource to ensure it exists
				final Optional<Resource> r = net.minecraft.client.Minecraft.getInstance().getResourceManager().getResource(loadedTexture);
				if (r.isPresent()) {
					return new ResourcePair(loadedTexture, texture.flag());
				}
			}
			// if none of the resources loaded, return fallback texture
			return new ResourcePair(FALLBACK_PREFAB, true);
		}

	}
}
