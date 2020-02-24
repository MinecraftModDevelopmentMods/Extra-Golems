package com.golems.entity;

import com.golems.events.SpongeGolemSoakEvent;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemConfigSet;
import com.golems.util.GolemNames;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public final class EntitySpongeGolem extends GolemBase {

  public static final String ALLOW_SPECIAL = "Allow Special: Absorb Water";
  public static final String INTERVAL = "Water Soaking Frequency";
  public static final String RANGE = "Water Soaking Range";
  public static final String PARTICLES = "Can Render Sponge Particles";

  public EntitySpongeGolem(final World world) {
    super(world);
    this.setCanSwim(true);
    this.setLootTableLoc(GolemNames.SPONGE_GOLEM);
    this.addHealItem(new ItemStack(Blocks.SPONGE, 1, 1), 0.75D);
  }

  @Override
  protected ResourceLocation applyTexture() {
    return makeTexture(ExtraGolems.MODID, GolemNames.SPONGE_GOLEM);
  }

  /**
   * Called frequently so the entity can update its state every tick as required.
   * For example, zombies and skeletons use this to react to sunlight and start to
   * burn.
   */
  @Override
  public void onLivingUpdate() {
    super.onLivingUpdate();
    GolemConfigSet cfg = getConfig(this);
    final int interval = cfg.getInt(INTERVAL);
    // TODO: Fix possible NPE
    if (cfg.getBoolean(ALLOW_SPECIAL) && (interval <= 1 || this.ticksExisted % interval == 0)) {
      final int x = MathHelper.floor(this.posX);
      final int y = MathHelper.floor(this.posY - 0.20000000298023224D) + 2;
      final int z = MathHelper.floor(this.posZ);
      final BlockPos center = new BlockPos(x, y, z);

      final SpongeGolemSoakEvent event = new SpongeGolemSoakEvent(this, center, cfg.getInt(RANGE));
      if (!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Result.DENY) {
        this.replaceWater(event.getPositionList(), event.getReplacementState(), event.updateFlag);
      }
    }

    if (cfg.getBoolean(PARTICLES) && Math.abs(this.motionX) < 0.05D && Math.abs(this.motionZ) < 0.05D
        && world.isRemote) {
      final EnumParticleTypes particle = this.isBurning() ? EnumParticleTypes.SMOKE_NORMAL
          : EnumParticleTypes.WATER_SPLASH;
      final double x = this.rand.nextDouble() - 0.5D * (double) this.width * 0.6D;
      final double y = this.rand.nextDouble() * (this.height - 0.75D);
      final double z = this.rand.nextDouble() - 0.5D * (double) this.width;
      this.world.spawnParticle(particle, this.posX + x, this.posY + y, this.posZ + z,
          (this.rand.nextDouble() - 0.5D) * 0.5D, this.rand.nextDouble() - 0.5D, (this.rand.nextDouble() - 0.5D) * 0.5D,
          new int[0]);
    }
  }

  @Override
  public SoundEvent getGolemSound() {
    return SoundEvents.BLOCK_CLOTH_STEP;
  }

  /**
   * Usually called after creating and firing a {@link SpongeGolemSoakEvent}.
   * Iterates through the list of positions and replaces each one with the passed
   * IBlockState.
   *
   * @return whether all setBlockState calls were successful.
   **/
  public boolean replaceWater(final List<BlockPos> positions, final IBlockState replaceWater, final int updateFlag) {
    boolean flag = true;
    for (final BlockPos p : positions) {
      flag &= this.world.setBlockState(p, replaceWater, updateFlag);
    }
    return flag;
  }

  @Override
  public List<String> addSpecialDesc(final List<String> list) {
    if (getConfig(this).getBoolean(EntitySpongeGolem.ALLOW_SPECIAL))
      list.add(TextFormatting.YELLOW + trans("entitytip.absorbs_water"));
    return list;
  }
}
