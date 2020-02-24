package com.golems.entity;

import java.util.List;

import com.golems.blocks.BlockUtilityPower;
import com.golems.entity.ai.EntityAIUtilityBlock;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.GolemLookup;
import com.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityRedstoneGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Redstone Power";
  public static final int DEF_FREQ = 2;

  /**
   * Default constructor for Redstone Golem
   **/
  public EntityRedstoneGolem(final World world) {
    this(world, GolemLookup.getConfig(EntityRedstoneGolem.class).getBoolean(ALLOW_SPECIAL), 15, DEF_FREQ);
    this.setLootTableLoc(GolemNames.REDSTONE_GOLEM);
    this.addHealItem(new ItemStack(Items.REDSTONE), 0.25D);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
  }

  /**
   * Flexible constructor to allow child classes to customize
   **/
  public EntityRedstoneGolem(final World world, boolean allowSpecial, int power, int frequency) {
    super(world);
    final IBlockState state = GolemItems.blockPowerSource.getDefaultState().withProperty(BlockUtilityPower.POWER_LEVEL,
        power);
    this.tasks.addTask(9, new EntityAIUtilityBlock(this, state, frequency, allowSpecial));
  }

  @Override
  protected ResourceLocation applyTexture() {
    return makeTexture(ExtraGolems.MODID, GolemNames.REDSTONE_GOLEM);
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_STONE_STEP;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender() {
    return super.getBrightnessForRender() + 64;
  }

  @Override
  public boolean isProvidingPower() {
    return true;
  }

  @Override
  public List<String> addSpecialDesc(final List<String> list) {
    // does not fire for child classes
    if (this.getClass() == EntityRedstoneGolem.class && getConfig(this).getBoolean(ALLOW_SPECIAL))
      list.add(TextFormatting.RED + trans("entitytip.emits_redstone_signal"));
    return list;
  }
}
