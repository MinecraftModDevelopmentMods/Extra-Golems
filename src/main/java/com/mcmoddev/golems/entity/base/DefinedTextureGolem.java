package com.mcmoddev.golems.entity.base;

import javax.annotation.Nullable;

import com.mcmoddev.golems.util.GolemRenderSettings;

import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class DefinedTextureGolem extends GolemBase {
  
  protected static final EntityDataAccessor<String> DATA_TEXTURE = SynchedEntityData.defineId(DefinedTextureGolem.class, EntityDataSerializers.STRING);
  protected static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(DefinedTextureGolem.class, EntityDataSerializers.INT);
  protected static final EntityDataAccessor<Integer> DATA_VINES = SynchedEntityData.defineId(DefinedTextureGolem.class, EntityDataSerializers.INT);
  protected static final String KEY_TEXTURE = "Texture";
  protected static final String KEY_COLOR = "Color";
  protected static final String KEY_VINES = "Vines";
  
  protected ResourceLocation texture = GolemRenderSettings.FALLBACK_BLOCK;
  protected int color = 0xFFFFFF;
  protected ResourceLocation vines = null;
  protected int vinesColor = 0;

  public DefinedTextureGolem(EntityType<? extends GolemBase> type, Level world) {
    super(type, world);
  }
  
  @Override
  public void aiStep() {
    super.aiStep();
    if(color <= 0) {
      color = 0xFFFFFF;
    }
  }
  
  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DATA_TEXTURE, GolemRenderSettings.FALLBACK_BLOCK.toString());
    this.getEntityData().define(DATA_COLOR, Integer.valueOf(0xFFFFFF));
    this.getEntityData().define(DATA_VINES, Integer.valueOf(0));
  }
  
  @Override
  public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
    super.onSyncedDataUpdated(key);
    // attempt to sync texture from client -> server -> other clients
    if (DATA_TEXTURE.equals(key)) {
      String textureString = getTextureString();
      if(!textureString.isEmpty()) {
        texture = new ResourceLocation(textureString);
      }
    } else if(DATA_COLOR.equals(key)) {
      color = this.getColorData();
    } else if(DATA_VINES.equals(key)) {
      vinesColor = this.getVinesColorData();
      vines = vinesColor > 0 ? GolemRenderSettings.FALLBACK_VINES : null;
    }
  }
  
  @Override
  public void addAdditionalSaveData(final CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    nbt.putString(KEY_TEXTURE, this.getTextureString());
    nbt.putInt(KEY_COLOR, this.getColorData());
    nbt.putInt(KEY_VINES, this.getVinesColorData());
  }

  @Override
  public void readAdditionalSaveData(final CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    this.setTexture(nbt.getString(KEY_TEXTURE));
    this.setColorData(nbt.getInt(KEY_COLOR));
    this.setVinesColorData(nbt.getInt(KEY_VINES));
  }

  public void setTexture(final String tex) {
    if(!tex.isEmpty()) {
      this.getEntityData().set(DATA_TEXTURE, tex);
      texture = new ResourceLocation(tex);
    }
  }
  
  public String getTextureString() {
    return this.getEntityData().get(DATA_TEXTURE);
  }
  
  public ResourceLocation getTexture() {
    return texture;
  }
  
  public void setColorData(final int colorData) {
    this.getEntityData().set(DATA_COLOR, Integer.valueOf(colorData));
    color = colorData;
  }
  
  public int getColorData() {
    return this.getEntityData().get(DATA_COLOR).intValue();
  }
  
  public int getColor() {
    return 0xFFFFFF; // TODO color;
  }
  
  public void setVinesColorData(final int vinesColorData) {
    this.getEntityData().set(DATA_VINES, Integer.valueOf(vinesColorData));
    vinesColor = vinesColorData;
    vines = vinesColorData > 0 ? GolemRenderSettings.FALLBACK_VINES : null;
  }
  
  public int getVinesColorData() {
    return this.getEntityData().get(DATA_VINES).intValue();
  }
  
  public int getVinesColor() {
    return vinesColor;
  }
  
  @Nullable
  public ResourceLocation getVines() {
    return vines;
  }
}
