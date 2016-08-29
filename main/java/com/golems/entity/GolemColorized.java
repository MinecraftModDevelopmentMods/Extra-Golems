package com.golems.entity;

import com.golems.main.GolemItems;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This class should always be registered with RenderGolemColorized.
 * It supports a 2-texture golem where {@link getTextureBase()} returns
 * a normal texture to be rendered and {@link getTextureToColor()} returns
 * a (usually grayscale) texture to be colored according to {@link getColor()}
 **/
public abstract class GolemColorized extends GolemBase
{	
	private long color;
	protected float colorRed;
	protected float colorBlue;
	protected float colorGreen;
	protected float colorAlpha;
	protected final ResourceLocation base;
	protected final ResourceLocation overlay;
	protected final boolean hasBase;
	
	/**
	 * Flexible constructor so child classes can "borrow" this class's behavior and customize.
	 * It is fine to pass 'null' for {@link rBase} or {@link rOverlay}, and null textures will not be rendered.
	 * Args: world, attack, pickBlock, initialColor, rBase, rOverlay.
	 * @param initial the (usually temporary) color to apply to this golem until it is updated by some other method.
	 * @param rBase an optional texture that will not be recolored or rendered transparent, to render before {@link OVERLAY}
	 * @param rOverlay a texture that will be recolored and optionally rendered as transparent.
	 **/
	public GolemColorized(World world, float attack, ItemStack pickBlock, long initial, final ResourceLocation rBase, final ResourceLocation rOverlay)
	{
		super(world, attack, pickBlock);
		this.setColor(initial);
		this.base = rBase;
		this.overlay = rOverlay;
		this.hasBase = this.base != null;
	}
	
	/**
	 * @see {@link GolemColorized(World, float, ItemStack, long, ResourceLocation, ResourceLocation)}
	 **/
	public GolemColorized(World world, float attack, long initial, ResourceLocation rBase, ResourceLocation rOverlay) 
	{
		this(world, attack, new ItemStack(GolemItems.golemHead), initial, rBase, rOverlay);
	}
	
	@Override
	protected ResourceLocation applyTexture() 
	{
		return makeGolemTexture("clay");
	}
	
	/** An optional texture to render as-is, without coloring **/
	public ResourceLocation getTextureBase()
	{
		return base;
	}
	
	/** The (probably grayscaled) texture that will be colored **/
	public ResourceLocation getTextureToColor()
	{
		return overlay;
	}
	
	/** Whether this golem has a sub-texture that should not be colored **/
	public boolean hasBase()
	{
		return this.hasBase;
	}
	
	/** Updates {@link #color} as well as calculates the RGBA components of that color **/
	public void setColor(long toSet)
	{
		this.color = toSet;
		long tmpColor = toSet;
		if((tmpColor & -67108864) == 0) 
		{
			tmpColor |= -16777216;
		}

		this.colorRed = (float) (tmpColor >> 16 & 255) / 255.0F;
		this.colorGreen = (float) (tmpColor >> 8 & 255) / 255.0F;
		this.colorBlue = (float) (tmpColor & 255) / 255.0F;
		this.colorAlpha = (float) (tmpColor >> 24 & 255) / 255.0F;
	}
	
	public long getColor()
	{
		return this.color;
	}

	public float getColorRed()
	{
		return this.colorRed;
	}
	
	public float getColorGreen()
	{
		return this.colorGreen;
	}
	
	public float getColorBlue()
	{
		return this.colorBlue;
	}
	
	public float getColorAlpha()
	{
		return this.colorAlpha;
	}
    
    /**
     * Whether {@link overlay} should be rendered as transparent.
     * This is not called for rendering {@link base},
     * only for rendering the colorized layer.
     **/
    @SideOnly(Side.CLIENT)
    public boolean hasTransparency()
    {
    	return false;
    }
}
