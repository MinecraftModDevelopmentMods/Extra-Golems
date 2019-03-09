package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.main.ExtraGolems;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

@SuppressWarnings("EntityConstructor")
public abstract class GolemColorizedMultiTextured extends GolemColorized {

	protected static final DataParameter<Byte> DATA_TEXTURE = EntityDataManager
			.<Byte>createKey(GolemColorizedMultiTextured.class, DataSerializers.BYTE);
	protected static final String NBT_TEXTURE = "GolemTextureData";
	protected final int[] colors;
	protected final ResourceLocation[] lootTables;

	/**
	 * Flexible constructor so child classes can "borrow" this class's behavior and customize.
	 * It is fine to pass 'null' for {@link base} or {@link overlay}, and null textures will not  be rendered.
	 * @param base an optional texture that will not be recolored or rendered transparent, to render before {@link overlay}
	 * @param overlay a texture that will be recolored and optionally rendered as transparent.
	 * @param lColors an int[] of color values to use for rendering -- interacting with this golem  will go to the next color
	 **/
	public GolemColorizedMultiTextured(Class<? extends GolemColorizedMultiTextured> type, final World world,
					   @Nullable final ResourceLocation base, @Nullable final ResourceLocation overlay, final int[] lColors) {
		super(type, world, 0L, base, overlay);
		colors = lColors;
		lootTables = new ResourceLocation[colors.length];
		for (int n = 0, len = colors.length; n < len; n++) {
			// initialize loot tables
			this.lootTables[n] = new ResourceLocation(getModId(), "entities/"
					+ this.getEntityString().replaceAll(getModId() + ":", "") + "/" + n);
		}
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(DATA_TEXTURE, (byte) 0);
	}



//	@Override
//	public void livingTick() {
//		super.livingTick();
//		// since textureNum is correct, update texture AFTER loading from NBT and init
//		if (this.ticksExisted == 2) {
//			this.updateTextureByData(this.getTextureNum());
//		}
//	}
//
//	@Override
//	public void writeAdditional(final NBTTagCompound nbt) {
//		super.writeAdditional(nbt);
//		nbt.setByte(NBT_TEXTURE, (byte) this.getTextureNum());
//	}
//
//	@Override
//	public void readAdditional(final NBTTagCompound nbt) {
//		super.readAdditional(nbt);
//		this.setTextureNum(nbt.getByte(NBT_TEXTURE));
//		this.updateTextureByData(this.getTextureNum());
//	}

	@Override
	public boolean doesInteractChangeTexture() {
		return false;
	}
	
	@Override
    	protected ResourceLocation getLootTable()
    	{
    	    return this.lootTables[this.getTextureNum() % this.lootTables.length];
    	}

	public void setTextureNum(final byte toSet) {
		this.getDataManager().set(DATA_TEXTURE, toSet);
	}

	public int getTextureNum() {
		return this.getDataManager().get(DATA_TEXTURE).intValue();
	}

	public int[] getColorArray() {
		return this.colors;
	}

	protected void updateTextureByData(final int data) {
		this.setColor(this.colors[data]);
	}

	public String getModId() {
		return ExtraGolems.MODID;
	}
}
