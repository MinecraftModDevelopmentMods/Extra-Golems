package com.golems.entity;

import javax.annotation.Nullable;

import com.golems.main.Config;
import com.golems.main.ExtraGolems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class GolemColorizedMultiTextured extends GolemColorized {

  protected static final DataParameter<Byte> DATA_TEXTURE = EntityDataManager
      .<Byte>createKey(GolemColorizedMultiTextured.class, DataSerializers.BYTE);
  protected static final String NBT_TEXTURE = "GolemTextureData";
  protected final int[] colors;
  protected final ResourceLocation[] lootTables;

  /**
   * Flexible constructor so child classes can "borrow" this class's behavior and
   * customize. It is fine to pass 'null' for {@link base} or {@link overlay}, and
   * null textures will not be rendered.
   * 
   * @param base    an optional texture that will not be recolored or rendered
   *                transparent, to render before {@link overlay}
   * @param overlay a texture that will be recolored and optionally rendered as
   *                transparent.
   * @param lColors an int[] of color values to use for rendering -- interacting
   *                with this golem will go to the next color
   **/
  public GolemColorizedMultiTextured(final World world, @Nullable final ResourceLocation base,
      @Nullable final ResourceLocation overlay, final int[] lColors) {
    super(world, 0L, base, overlay);
    colors = lColors;
    lootTables = new ResourceLocation[colors.length];
    for (int n = 0, len = colors.length; n < len; n++) {
      // initialize loot tables
      this.lootTables[n] = new ResourceLocation(getModId(),
          "entities/" + this.getEntityString().replaceAll(getModId() + ":", "") + "/" + n);
    }
  }

  @Override
  public void notifyDataManagerChange(DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    // attempt to sync texture from client -> server -> other clients
    if (DATA_TEXTURE.equals(key)) {
      this.updateTextureByData(this.getTextureNum());
    }
  }

  @Override
  protected void entityInit() {
    super.entityInit();
    this.getDataManager().register(DATA_TEXTURE, Byte.valueOf((byte) 0));
  }

  @Override
  public boolean processInteract(final EntityPlayer player, final EnumHand hand) {
    final ItemStack stack = player.getHeldItem(hand);
    // only change texture when player has empty hand
    if (!stack.isEmpty() || !this.doesInteractChangeTexture()) {
      return super.processInteract(player, hand);
    } else {
      int incremented = ((this.getTextureNum() + 1) % this.colors.length);
      this.setTextureNum((byte) incremented);
      this.updateTextureByData(this.getTextureNum());
      this.writeEntityToNBT(this.getEntityData());
      player.swingArm(hand);
      return true;
    }
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    // since textureNum is correct, update texture AFTER loading from NBT and init
    if (this.ticksExisted == 2) {
      this.updateTextureByData(this.getTextureNum());
    }
  }

  @Override
  public void writeEntityToNBT(final NBTTagCompound nbt) {
    super.writeEntityToNBT(nbt);
    nbt.setByte(NBT_TEXTURE, (byte) this.getTextureNum());
  }

  @Override
  public void readEntityFromNBT(final NBTTagCompound nbt) {
    super.readEntityFromNBT(nbt);
    this.setTextureNum(nbt.getByte(NBT_TEXTURE));
    this.updateTextureByData(this.getTextureNum());
  }

  @Override
  protected ResourceLocation getLootTable() {
    return this.lootTables[this.getTextureNum() % this.lootTables.length];
  }

  public void setTextureNum(final byte toSet) {
    this.getDataManager().set(DATA_TEXTURE, new Byte(toSet));
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
