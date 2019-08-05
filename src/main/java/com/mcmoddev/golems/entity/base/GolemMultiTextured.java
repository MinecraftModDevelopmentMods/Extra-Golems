package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemNames;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class GolemMultiTextured extends GolemBase {

	/**
	 * The DataParameter that stores which texture this golem is using. Max value is 128
	 **/
	protected static final DataParameter<Byte> DATA_TEXTURE = EntityDataManager
		.<Byte>createKey(GolemMultiTextured.class, DataSerializers.BYTE);
	protected static final String NBT_TEXTURE = "GolemTextureData";

	/**
	 * ResourceLocation array of textures to loop through when the player interacts with this golem.
	 * Max size is Byte.MAX_VALUE
	 **/
	public final ResourceLocation[] textures;

	/**
	 * Loot Table array to match texture array. If you don't want this, override {@link getLootTable}
	 **/
	public final ResourceLocation[] lootTables;

	/**
	 * This is a base class for golems that change texture when player interacts. Pass Strings that
	 * will be used to construct a ResourceLocation array of textures as well as loot tables
	 * <p><b>Example call to this constructor:</b>
	 * <p><code>
	 * String[] NAMES = new String[] {"one","two","three"};
	 * <br><br>public EntityExampleGolem(World world) {
	 * <br>super(EntityExampleGolem.class, world, "example", NAMES);
	 * <br>}</code>
	 * <p>This will initialize textures at:
	 * <br><code>[getModId()]/textures/entity/golem_example/one.png</code>
	 * <br><code>[getModId()]/textures/entity/golem_example/two.png</code>
	 * <br><code>[getModId()]/textures/entity/golem_example/three.png</code>
	 * <br> as well as loot tables for the same names with the JSON suffix
	 **/
	public GolemMultiTextured(final EntityType<? extends GolemBase> entityType,
			final World world, final String modid, final String prefix, final String[] textureNames) {
		super(entityType, world);
		this.textures = new ResourceLocation[textureNames.length];
		this.lootTables = new ResourceLocation[textureNames.length];
		for (int n = 0, len = textureNames.length; n < len; n++) {
			// initialize textures
			final String s = textureNames[n];
			this.textures[n] = makeTexture(modid, "golem_" + prefix + "/" + s);
			// initialize loot tables
			this.lootTables[n] = new ResourceLocation(modid, "entities/golem_" + prefix + "/" + s);
		}
	}

	@Override
	protected ResourceLocation applyTexture() {
		// apply TEMPORARY texture to avoid NPE. Actual texture is first applied in livingTick
		return makeTexture(ExtraGolems.MODID, GolemNames.CLAY_GOLEM);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(DATA_TEXTURE, (byte) 0);
	}

	@Override
	public boolean processInteract(final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		// only change texture when player has empty hand
		if (stack.isEmpty() && this.canInteractChangeTexture()) {
			final int incremented = (this.getTextureNum() + 1) % this.textures.length;
			this.setTextureNum((byte) incremented);
			player.swingArm(hand);
			return true;
		} else {
			return super.processInteract(player, hand);
		}
	}

	@Override
	public void notifyDataManagerChange(DataParameter<?> key) {
		super.notifyDataManagerChange(key);
		// attempt to sync texture from client -> server -> other clients
		if (DATA_TEXTURE.equals(key)) {
			this.setTextureType(this.getTextureFromArray(this.getTextureNum()));
		}
	}

	@Override
	public void livingTick() {
		super.livingTick();
		// since textureNum is correct, update texture AFTER loading from NBT and init
		if (this.ticksExisted == 2) {
			this.setTextureType(this.getTextureFromArray(this.getTextureNum()));
		}
	}

	@Override
	public void writeAdditional(final CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.putByte(NBT_TEXTURE, (byte) this.getTextureNum());
	}

	@Override
	public void readAdditional(final CompoundNBT nbt) {
		super.readAdditional(nbt);
		this.setTextureNum(nbt.getByte(NBT_TEXTURE));
	}

	@Override
	protected ResourceLocation getLootTable() {
		return this.lootTables[this.getTextureNum() % this.lootTables.length];
	}
	
	@Override
	public ItemStack getPickedResult(final RayTraceResult target) {
		return getCreativeReturn(target);
	}

	/**
	 * Calls {@link #setTextureNum(byte, boolean)} with <b>toSet</b> and <b>true</b>.
	 **/
	public void setTextureNum(final byte toSet) {
		setTextureNum(toSet, true);
	}

	/**
	 * Update the texture data. If <b>updateInstantly</b> is true, call
	 * {@link #setTextureType(ResourceLocation)} based on {@link #getTextureFromArray(int)} and
	 * {@link #getTextureNum()}
	 **/
	public void setTextureNum(final byte toSet, final boolean updateInstantly) {
		this.getDataManager().set(DATA_TEXTURE, Byte.valueOf(toSet));
		if (updateInstantly) {
			this.setTextureType(this.getTextureFromArray(this.getTextureNum()));
		}
	}

	public int getTextureNum() {
		return this.getDataManager().get(DATA_TEXTURE).byteValue();
	}

	public int getNumTextures() {
		return this.textures != null ? this.textures.length : null;
	}

	public int getMaxTextureNum() {
		return getNumTextures() - 1;
	}

	public ResourceLocation[] getTextureArray() {
		return this.textures;
	}

	public ResourceLocation getTextureFromArray(final int index) {
		return this.textures[index % this.textures.length];
	}
	
	// ABSTRACT
	
	/**
	 * Called when the player middle-clicks on a golem to get its "spawn egg"
	 * or similar item
	 * @param target
	 * @return an ItemStack that best represents this golem, or an empty itemstack
	 **/
	public abstract ItemStack getCreativeReturn(final RayTraceResult target);
}
