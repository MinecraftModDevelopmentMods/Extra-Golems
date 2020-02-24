package com.golems.entity;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.golems.blocks.BlockUtilityGlow;
import com.golems.entity.ai.EntityAIUtilityBlock;
import com.golems.main.ExtraGolems;
import com.golems.main.GolemItems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class EntitySeaLanternGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Emit Light";
  public static final String FREQUENCY = "Light Frequency";
  public static final BiPredicate<GolemBase, IBlockState> WATER_PRED = (golem,
      toReplace) -> toReplace.getBlock() != GolemItems.blockLightSourceWater
          && toReplace.getMaterial() == Material.WATER && toReplace.getValue(BlockLiquid.LEVEL).intValue() == 0;
  private static final float BRIGHTNESS = 1.0F;
  private static final int BRIGHTNESS_INT = (int) (BRIGHTNESS * 15.0F);

  public EntitySeaLanternGolem(final World world) {
    super(world);
    this.canDrown = false;
    this.addHealItem(new ItemStack(Items.PRISMARINE_CRYSTALS), 0.25D);
    this.addHealItem(new ItemStack(Items.PRISMARINE_SHARD), 0.25D);
    this.setLootTableLoc(GolemNames.SEALANTERN_GOLEM);
    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);

  }

  @Override
  protected void initEntityAI() {
    super.initEntityAI();
    // lights above and below water... need to add to different lists to run
    // concurrently
    GolemConfigSet cfg = getConfig(this);
    this.tasks.addTask(8,
        new EntityAIUtilityBlock(this, GolemItems.blockLightSourceWater.getDefaultState()
            .withProperty(BlockUtilityGlow.LIGHT_LEVEL, BRIGHTNESS_INT), cfg.getInt(FREQUENCY),
            cfg.getBoolean(ALLOW_SPECIAL), WATER_PRED));
    this.targetTasks.addTask(8,
        new EntityAIUtilityBlock(this,
            GolemItems.blockLightSource.getDefaultState().withProperty(BlockUtilityGlow.LIGHT_LEVEL, BRIGHTNESS_INT),
            cfg.getInt(FREQUENCY), cfg.getBoolean(ALLOW_SPECIAL)));
  }

  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    // speed boost in water
    if (this.isInWater()) {
      this.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 2, false, false));
    }
  }

  @Override
  protected ResourceLocation applyTexture() {
    return makeTexture(ExtraGolems.MODID, GolemNames.SEALANTERN_GOLEM);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender() {
    return (int) (15728880F * EntitySeaLanternGolem.BRIGHTNESS);
  }

  @Override
  public float getBrightness() {
    return EntitySeaLanternGolem.BRIGHTNESS;
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_GLASS_STEP;
  }

  @Override
  public boolean isProvidingLight() {
    return true;
  }

  @Override
  public List<String> addSpecialDesc(final List<String> list) {
    if (getConfig(this).getBoolean(EntitySeaLanternGolem.ALLOW_SPECIAL)) {
      list.add(TextFormatting.GOLD + trans("entitytip.lights_area"));
    }
    list.add(TextFormatting.AQUA + trans("entitytip.breathes_underwater"));
    return list;
  }
}
