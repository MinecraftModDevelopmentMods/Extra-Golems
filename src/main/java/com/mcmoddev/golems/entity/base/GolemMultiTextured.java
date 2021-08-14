package com.mcmoddev.golems.entity.base;

import java.util.Map;

import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public abstract class GolemMultiTextured extends GolemBase implements IMultiTexturedGolem<ResourceLocation> {

  /**
   * The DataParameter that stores which texture this golem is using. Max value is
   * 128
   **/
  protected static final EntityDataAccessor<Byte> DATA_TEXTURE = SynchedEntityData.<Byte>defineId(GolemMultiTextured.class, EntityDataSerializers.BYTE);
  protected static final String KEY_TEXTURE = "GolemTextureData";

  /**
   * ResourceLocation array of textures to loop through when the player interacts
   * with this golem. Max size is Byte.MAX_VALUE
   **/
  protected final ResourceLocation[] textures;

  /**
   * Loot Table array to match texture array. If you don't want this, override
   * {@link getLootTable}
   **/
  protected final ResourceLocation[] lootTables;

  /**
   * This is a base class for golems that change texture when player interacts.
   * Pass Strings that will be used to construct a ResourceLocation array of
   * textures as well as loot tables
   * <p>
   * <b>Example call to this constructor:</b>
   * <p>
   * <code>
   * String[] NAMES = new String[] {"one","two","three"};
   * <br><br>public EntityExampleGolem(EntityType entityType, World world) {
   * <br>&nbsp;&nbsp;super(entityType, world, "golems", NAMES);
   * <br>}</code>
   * <p>
   * If the golem was registered with name <code>"golem_example"</code>, then the
   * following textures will be initialized: <br>
   * <code>golems/textures/entity/golem_example/one.png</code> <br>
   * <code>golems/textures/entity/golem_example/two.png</code> <br>
   * <code>golems/textures/entity/golem_example/three.png</code> <br>
   * as well as loot tables for the same names with the JSON suffix
   **/
  public GolemMultiTextured(final EntityType<? extends GolemBase> entityType, final Level world, final String textureModId, 
      final String[] textureNames, final String lootTableModId, final String[] lootTableNames) {
    super(entityType, world);
    this.textures = new ResourceLocation[textureNames.length];
    this.lootTables = new ResourceLocation[lootTableNames.length];
    for (int n = 0, len = textureNames.length; n < len; n++) {
      // initialize textures
      this.textures[n] = new ResourceLocation(textureModId, "textures/block/" + textureNames[n] + ".png");
      // initialize loot tables
      this.lootTables[n] = new ResourceLocation(lootTableModId, "entities/" + this.getGolemContainer().getName() + "/" + lootTableNames[n]);
    }
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.getEntityData().define(DATA_TEXTURE, (byte) 0);
  }

  @Override
  public InteractionResult mobInteract(final Player player, final InteractionHand hand) {
    // change texture when player clicks (if enabled)
    if (!player.isCrouching() && this.canInteractChangeTexture()) {
      return handlePlayerInteract(player, hand);
    } else {
      return super.mobInteract(player, hand);
    }
  }

  @Override
  public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
    final Map<Block, Byte> map = this.getTextureBytes();
    if (map != null && !map.isEmpty()) {
      byte textureNum = GolemTextureBytes.getByBlock(map, body.getBlock());
      this.setTextureNum(textureNum);
    }
  }

  @Override
  public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
    super.onSyncedDataUpdated(key);
    // attempt to sync texture from client -> server -> other clients
    if (DATA_TEXTURE.equals(key)) {
      this.setTextureNum((byte) this.getTextureNum());
    }
  }

  @Override
  public void addAdditionalSaveData(final CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    nbt.putByte(KEY_TEXTURE, (byte) this.getTextureNum());
  }

  @Override
  public void readAdditionalSaveData(final CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    this.setTextureNum(nbt.getByte(KEY_TEXTURE));
  }

  @Override
  protected ResourceLocation getDefaultLootTable() {
    return this.lootTables[this.getTextureNum() % this.lootTables.length];
  }

  @Override
  public ItemStack getPickedResult(final HitResult target) {
    return getCreativeReturn(target);
  }

  @Override
  public void setTextureNum(final byte toSet) {
    if (toSet != this.getEntityData().get(DATA_TEXTURE).byteValue()) {
      this.getEntityData().set(DATA_TEXTURE, Byte.valueOf(toSet));
    }
  }

  @Override
  public int getTextureNum() {
    return this.getEntityData().get(DATA_TEXTURE).byteValue();
  }

  @Override
  public int getNumTextures() {
    return this.textures != null ? this.textures.length : null;
  }

  @Override
  public ResourceLocation[] getTextureArray() {
    return this.textures;
  }

  public ResourceLocation getTextureFromArray(final int index) {
    return getTextureArray()[index % this.textures.length];
  }

  public ResourceLocation getTexture() {
    return getTextureFromArray(getTextureNum());
  }
  
  @Override
  public ResourceLocation[] getLootTableArray() {
    return this.lootTables;
  }
}
