package com.golems.entity;

import java.util.List;
import java.util.function.BiPredicate;

import com.golems.blocks.BlockUtilityGlow;
import com.golems.entity.ai.EntityAIUtilityBlock;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.GolemConfigSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntityRedstoneLampGolem extends GolemMultiTextured {

  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
  public static final String FREQUENCY = "Light Frequency";

  public static final String LAMP_PREFIX = "redstone_lamp";
  public static final String[] VARIANTS = { "lit", "unlit" };

  public static final BiPredicate<GolemBase, IBlockState> LIT_PRED = (golem, toReplace) -> golem.isProvidingLight();

  public EntityRedstoneLampGolem(final World world) {
    super(world, LAMP_PREFIX, VARIANTS);
    this.setCanTakeFallDamage(true);
    this.addHealItem(new ItemStack(Items.REDSTONE), 0.25D);
    this.addHealItem(new ItemStack(Items.GLOWSTONE_DUST), 0.25D);
    final GolemConfigSet cfg = getConfig(this);
    final IBlockState state = GolemItems.blockLightSource.getDefaultState().withProperty(BlockUtilityGlow.LIGHT_LEVEL,
        15);
    this.tasks.addTask(9, new EntityAIUtilityBlock(this, state, cfg.getInt(FREQUENCY), cfg.getBoolean(ALLOW_SPECIAL),
        EntityAIUtilityBlock.getDefaultBiPred(state).and(LIT_PRED)));
  }

  @Override
  public boolean doesInteractChangeTexture() {
    // always allow interact
    return true;
  }

  @Override
  public boolean isProvidingLight() {
    // only allow light if correct texture data
    return this.getTextureNum() == 0;
  }

  @Override
  public String getModId() {
    return ExtraGolems.MODID;
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_GLASS_STEP;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.BLOCK_GLASS_BREAK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender() {
    return isProvidingLight() ? 15728880 : super.getBrightnessForRender();
  }

  @Override
  public float getBrightness() {
    return isProvidingLight() ? 1.0F : super.getBrightness();
  }

  @Override
  public List<String> addSpecialDesc(final List<String> list) {
    // does not fire for child classes
    if (getConfig(this).getBoolean(ALLOW_SPECIAL)) {
      list.add(TextFormatting.GOLD + trans("entitytip.lights_area_toggle"));
    }
    return list;
  }
}
