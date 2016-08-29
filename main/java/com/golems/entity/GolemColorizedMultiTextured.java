package com.golems.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public abstract class GolemColorizedMultiTextured extends GolemColorized 
{
	protected static final DataParameter<Byte> DATA_TEXTURE = EntityDataManager.<Byte>createKey(GolemColorizedMultiTextured.class, DataSerializers.BYTE);
	protected static final String NBT_TEXTURE = "GolemTextureData";
	protected final int[] colors;

	/**
	 * Flexible constructor so child classes can "borrow" this class's behavior and customize.
	 * It is fine to pass 'null' for {@link BASE} or {@link OVERLAY}, and null textures will not be rendered.
	 * @param BASE an optional texture that will not be recolored or rendered transparent, to render before {@link OVERLAY}
	 * @param OVERLAY a texture that will be recolored and optionally rendered as transparent.
	 * @param lColors an int[] of color values to use for rendering -- interacting with this golem will go to the next color
	 **/
	public GolemColorizedMultiTextured(World world, float damage, ItemStack pick, final ResourceLocation BASE, final ResourceLocation OVERLAY, int[] lColors)
	{
		super(world, damage, pick, 0L, BASE, OVERLAY);
		colors = lColors;
	}
	
	/**
	 * Flexible constructor so child classes can "borrow" this class's behavior and customize.
	 * It is fine to pass 'null' for {@link BASE} or {@link OVERLAY}, and null textures will not be rendered.
	 * @param BASE an optional texture that will not be recolored or rendered transparent, to render before {@link OVERLAY}
	 * @param OVERLAY a texture that will be recolored and optionally rendered as transparent.
	 * @param lColors an int[] of color values to use for rendering -- interacting with this golem will go to the next color
	 **/
	public GolemColorizedMultiTextured(World world, float damage, Block pick, final ResourceLocation BASE, final ResourceLocation OVERLAY, int[] lColors)
	{
		this(world, damage, new ItemStack(pick), BASE, OVERLAY, lColors);
	}
	
	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.getDataManager().register(DATA_TEXTURE, Byte.valueOf((byte)0));
	}
	
	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack)
	{
		// only change texture when player has empty hand
		if(stack != null)
		{
			return super.processInteract(player, hand, stack);
		}
		else
		{
			int incremented = ((this.getTextureNum() + 1) % this.colors.length);
			this.setTextureNum((byte)incremented);
			this.updateTextureByData(this.getTextureNum());
			this.writeEntityToNBT(this.getEntityData());
			player.swingArm(hand);
			return true;
		}
	}
	
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		// since textureNum is correct, update texture AFTER loading from NBT and init
		if(this.ticksExisted == 2)
		{
			this.updateTextureByData(this.getTextureNum());
			// debug:
			//System.out.println("Set texture using textureNum = " + this.getTextureNum());
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
    {
		super.writeEntityToNBT(nbt);
		nbt.setByte(NBT_TEXTURE, (byte)this.getTextureNum());
    }
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.setTextureNum(nbt.getByte(NBT_TEXTURE));
		this.updateTextureByData(this.getTextureNum());
		// debug:
		//System.out.println("NBT textureNum = " + this.getTextureNum());
	}
	
	@Override
	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData data)
	{
		this.setTextureNum((byte)this.rand.nextInt(this.colors.length));
		this.updateTextureByData(this.getTextureNum());
		return super.onInitialSpawn(difficulty, data);
	}
	
	@Override
	public boolean doesInteractChangeTexture()
	{
		return true;
	}
	
	public void setTextureNum(byte toSet)
	{
		this.getDataManager().set(DATA_TEXTURE, new Byte(toSet));
	}

	public int getTextureNum() 
	{
		return this.getDataManager().get(DATA_TEXTURE).intValue();
	}
	
	public int[] getColorArray()
	{
		return this.colors;
	}
	
	protected void updateTextureByData(int data)
	{
		this.setColor(this.colors[data]);
	}
}
