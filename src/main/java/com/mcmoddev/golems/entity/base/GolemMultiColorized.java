package com.mcmoddev.golems.entity.base;

import com.mcmoddev.golems.util.GolemTextureBytes;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

@SuppressWarnings("EntityConstructor")
public abstract class GolemMultiColorized extends GolemBase implements IMultiTexturedGolem<Integer> {

  protected static final DataParameter<Byte> DATA_TEXTURE = EntityDataManager.<Byte>createKey(GolemMultiColorized.class, DataSerializers.BYTE);
  protected static final String NBT_TEXTURE = "GolemTextureData";
  protected final Integer[] colors;
  protected final ResourceLocation[] lootTables;

  // here for convenience, used only by child classes
  public static final Integer[] DYE_COLORS = { 16383998, 16351261, 13061821, 3847130, 16701501, 8439583, 15961002, 4673362, 10329495, 1481884,
      8991416, 3949738, 8606770, 6192150, 11546150, 1908001 };

  /**
   * Flexible constructor so child classes can "borrow" this class's behavior and
   * customize. It is fine to pass 'null' for {@code base} or {@code overlay}, and
   * null textures will not be rendered.
   *
   * @param base    an optional texture that will not be recolored or rendered
   *                transparent, to render before {@code overlay}
   * @param overlay a texture that will be recolored and optionally rendered as
   *                transparent.
   * @param lColors an int[] of color values to use for rendering
   **/
  public GolemMultiColorized(final EntityType<? extends GolemBase> entityType, final World world, final String modid, final Integer[] lColors) {
    super(entityType, world);
    colors = lColors;
    lootTables = new ResourceLocation[colors.length];
    for (int n = 0, len = colors.length; n < len; n++) {
      // initialize loot tables
      this.lootTables[n] = new ResourceLocation(modid, "entities/" + this.getEntityString().replaceAll(modid + ":", "") + "/" + n);
    }
  }

  @Override
  protected void registerData() {
    super.registerData();
    this.getDataManager().register(DATA_TEXTURE, (byte) 0);
  }

  @Override
  public ActionResultType func_230254_b_(final PlayerEntity player, final Hand hand) { // processInteract
    // change texture when player clicks (if enabled)
    if (!player.isCrouching() && this.canInteractChangeTexture()) {
      return handlePlayerInteract(player, hand);
    } else {
      return super.func_230254_b_(player, hand);
    }
  }

//  @Override
//  public void notifyDataManagerChange(DataParameter<?> key) {
//    super.notifyDataManagerChange(key);
//    // attempt to sync texture from client -> server -> other clients
//    if (DATA_TEXTURE.equals(key)) {
//      this.updateTextureByData(this.getTextureNum());
//    }
//  }
//
//  @Override
//  public void livingTick() {
//    super.livingTick();
//    // since textureNum is correct, update texture AFTER loading from NBT and init
//    if (this.ticksExisted == 2) {
//      this.updateTextureByData(this.getTextureNum());
//    }
//  }

  @Override
  public void onBuilt(final BlockState body, final BlockState legs, final BlockState arm1, final BlockState arm2) {
    byte textureNum = GolemTextureBytes.getByBlock(this.getTextureBytes(), body.getBlock());
    this.setTextureNum(textureNum);
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

  @Override
  public void setTextureNum(final byte toSet) {
    this.getDataManager().set(DATA_TEXTURE, toSet);
  }

  @Override
  public int getTextureNum() {
    return this.getDataManager().get(DATA_TEXTURE).intValue();
  }

  @Override
  public Integer[] getTextureArray() {
    return this.colors;
  }

  /**
   * @return the full color number currently applied.
   **/
  public int getColor() {
    return this.colors[getTextureNum()];
  }
}
