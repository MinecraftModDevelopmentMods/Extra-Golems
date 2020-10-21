package com.mcmoddev.golems.entity.base;

import javax.annotation.Nullable;

import com.mcmoddev.golems.util.GolemRenderSettings;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DefinedTextureGolem extends GolemBase {
  
  protected static final DataParameter<String> DATA_TEXTURE = EntityDataManager.createKey(DefinedTextureGolem.class, DataSerializers.STRING);
  protected static final DataParameter<Integer> DATA_VINES = EntityDataManager.createKey(DefinedTextureGolem.class, DataSerializers.VARINT);
  protected static final String KEY_TEXTURE = "Texture";
  protected static final String KEY_VINES = "Vines";
  
  protected ResourceLocation texture = GolemRenderSettings.FALLBACK_BLOCK;
  protected ResourceLocation vines = null;
  protected int vinesColor = 0;

  public DefinedTextureGolem(EntityType<? extends GolemBase> type, World world) {
    super(type, world);
  }
  
  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_TEXTURE, GolemRenderSettings.FALLBACK_BLOCK.toString());
    this.getDataManager().register(DATA_VINES, Integer.valueOf(0));
  }
  
  @Override
  public void notifyDataManagerChange(DataParameter<?> key) {
    super.notifyDataManagerChange(key);
    // attempt to sync texture from client -> server -> other clients
    if (DATA_TEXTURE.equals(key)) {
      String textureString = getTextureString();
      if(!textureString.isEmpty()) {
        texture = new ResourceLocation(textureString);
      }
    } else if(DATA_VINES.equals(key)) {
      vinesColor = this.getVinesColorData();
      vines = vinesColor > 0 ? GolemRenderSettings.FALLBACK_VINES : null;
    }
  }
  
  @Override
  public void writeAdditional(final CompoundNBT nbt) {
    super.writeAdditional(nbt);
    nbt.putString(KEY_TEXTURE, this.getTextureString());
    nbt.putInt(KEY_VINES, this.getVinesColorData());
  }

  @Override
  public void readAdditional(final CompoundNBT nbt) {
    super.readAdditional(nbt);
    this.setTexture(nbt.getString(KEY_TEXTURE));
    this.setVinesColorData(nbt.getInt(KEY_VINES));
  }

  public void setTexture(final String tex) {
    if(!tex.isEmpty()) {
      this.getDataManager().set(DATA_TEXTURE, tex);
      texture = new ResourceLocation(tex);
    }
  }
  
  public String getTextureString() {
    return this.getDataManager().get(DATA_TEXTURE);
  }
  
  public ResourceLocation getTexture() {
    return texture;
  }
  
  public void setVinesColorData(final int vinesColorData) {
    this.getDataManager().set(DATA_VINES, Integer.valueOf(vinesColorData));
    vinesColor = vinesColorData;
    vines = vinesColorData > 0 ? GolemRenderSettings.FALLBACK_VINES : null;
  }
  
  public int getVinesColorData() {
    return this.getDataManager().get(DATA_VINES).intValue();
  }
  
  public int getVinesColor() {
    return vinesColor;
  }
  
  @Nullable
  public ResourceLocation getVines() {
    return vines;
  }
}
