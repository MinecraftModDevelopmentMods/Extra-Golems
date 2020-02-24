package com.mcmoddev.golems.entity.base;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * This class should always be registered with RenderGolemColorized. It supports
 * a 2-texture golem where {@link #getTextureBase()} returns a normal texture to
 * be rendered and {@link #getTextureToColor()} returns a (usually grayscale)
 * texture to be colored according to {@link #getColor()}
 **/
public abstract class GolemColorized extends GolemBase {

  private long color;
  protected float colorRed;
  protected float colorBlue;
  protected float colorGreen;
  protected float colorAlpha;
  protected final ResourceLocation base;
  protected final ResourceLocation overlay;

  /**
   * Flexible constructor so child classes can "borrow" this class's behavior and
   * customize. It is fine to pass 'null' for {@code rBase} or {@code rOverlay},
   * and null textures will not be rendered. Args: world, attack, buildingBlock,
   * initialColor, rBase, rOverlay.
   *
   * @param entityType the entity type
   * @param world      the entity world
   * @param initial    the (usually temporary) color to apply to this golem until
   *                   it is updated by some other method.
   * @param rBase      an optional texture that will not be recolored or rendered
   *                   transparent, to render before the {@code overlay}
   * @param rOverlay   a texture that will be recolored and optionally rendered as
   *                   transparent.
   **/
  public GolemColorized(final EntityType<? extends GolemBase> entityType, final World world, final long initial,
      @Nullable final ResourceLocation rBase, @Nullable final ResourceLocation rOverlay) {
    super(entityType, world);
    this.setColor(initial);
    this.base = rBase;
    this.overlay = rOverlay;
  }

  /**
   * An optional texture to render as-is, without coloring.
   **/
  public ResourceLocation getTextureBase() {
    return base;
  }

  /**
   * The (probably grayscaled) texture that will be colored.
   **/
  public ResourceLocation getTextureToColor() {
    return overlay;
  }

  /**
   * Whether this golem has a sub-texture that should not be colored.
   **/
  public boolean hasBase() {
    return this.base != null;
  }

  /**
   * Whether this golem has a texture that should be colored.
   **/
  public boolean hasOverlay() {
    return this.overlay != null;
  }

  /**
   * Updates {@code #color} as well as calculates the RGBA components of that
   * color. Note: normal render class cannot handle an alpha value here, the
   * actual texture image must be saved with transparency instead.
   **/
  public void setColor(final long toSet) {
    this.color = toSet;
    long tmpColor = toSet;
    if ((tmpColor & -67108864) == 0) {
      tmpColor |= -16777216;
    }

    this.colorRed = (float) (tmpColor >> 16 & 255) / 255.0F;
    this.colorGreen = (float) (tmpColor >> 8 & 255) / 255.0F;
    this.colorBlue = (float) (tmpColor & 255) / 255.0F;
    this.colorAlpha = (float) (tmpColor >> 24 & 255) / 255.0F;
  }

  /**
   * @return the full color number currently applied.
   * @see #getColorRed()
   * @see #getColorGreen()
   * @see #getColorBlue()
   * @see #getColorAlpha()
   **/
  public long getColor() {
    return this.color;
  }

  public float getColorRed() {
    return this.colorRed;
  }

  public float getColorGreen() {
    return this.colorGreen;
  }

  public float getColorBlue() {
    return this.colorBlue;
  }

  public float getColorAlpha() {
    return this.colorAlpha;
  }

  /**
   * Whether {@code overlay} should be rendered as transparent. This is not called
   * for rendering {@code base}, only for rendering the colorized layer.
   **/
  public boolean hasTransparency() {
    return false;
  }
}
