package com.mcmoddev.golems_quark.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.util.GolemTextureBytes;
import com.mcmoddev.golems_quark.QuarkGolemsEntities;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.automation.module.ColorSlimeModule;
import vazkii.quark.base.module.ModuleLoader;

public class ColorSlimeGolem extends GolemMultiTextured {

  public static final String ALLOW_SPECIAL = "Allow Special: Extra Knockback";
  public static final String SPLITTING_CHILDREN = "Splitting Factor";
  public static final String KNOCKBACK = "Knockback Factor";
  
  public static final String[] SLIME_TYPES = { "red", "blue", "cyan", "magenta", "yellow" };
  
  private static final Map<Block, Byte> textureBytes = new HashMap<>();
  
  private boolean allowKnockback;
  private double knockbackAmount;

  public ColorSlimeGolem(final EntityType<? extends GolemBase> entityType, final World world) {
    super(entityType, world, QuarkGolemsEntities.MODID, SLIME_TYPES);
    allowKnockback = this.getConfigBool(ALLOW_SPECIAL);
    knockbackAmount = this.getConfigDouble(KNOCKBACK);
  }

  @Override
  public boolean attackEntityAsMob(final Entity entity) {
    if (super.attackEntityAsMob(entity)) {
      // knocks back the target entity (if it's adult and not attacking a slime)
      if (!this.isChild() && allowKnockback && !(entity instanceof SlimeEntity)) {
        applyKnockback(entity, knockbackAmount);
      }
      return true;
    }
    return false;
  }

  @Override
  protected void damageEntity(final DamageSource source, final float amount) {
    if (!this.isInvulnerableTo(source)) {
      super.damageEntity(source, amount);
      // knocks back the entity that is attacking it
      if (allowKnockback && !this.isChild() && source.getImmediateSource() != null) {
        applyKnockback(source.getImmediateSource(), this.getConfigDouble(KNOCKBACK));
      }
    }
  }

  @Override
  public void setChild(final boolean isChild) {
    super.setChild(isChild);
    if(isChild) {
      allowKnockback = false;
    }
  }
 
  @Override
  public void onDeath(final DamageSource source) {
    int children = this.getConfigInt(SPLITTING_CHILDREN);
    if (children > 0) {
      for(final GolemBase g : trySpawnChildren(children)) {
        // update the texture field for each child
        ((ColorSlimeGolem)g).setTextureNum((byte)this.getTextureNum());
      }
    }
    super.onDeath(source);
  }
  
  @Override
  public ItemStack getCreativeReturn(final RayTraceResult target) {
    return new ItemStack(GolemTextureBytes.getByByte(textureBytes, (byte) this.getTextureNum()));
  }
  
  @Override
  public Map<Block, Byte> getTextureBytes() {
    // we have to do this late because not all blocks are loaded initially
    if(textureBytes.isEmpty()) {
      fillTextureBytes();
    }
    return textureBytes;
  }
  
  @Override
  public boolean hasTransparency() {
    return true;
  }
  
  /**
   * Adds extra velocity to the golem's knockback attack.
   **/
  private void applyKnockback(final Entity entity, final double knockbackFactor) {
    final Vec3d myPos = this.getPositionVec();
    final Vec3d ePos = entity.getPositionVec();
    final double dX = Math.signum(ePos.x - myPos.x) * knockbackFactor;
    final double dZ = Math.signum(ePos.z - myPos.z) * knockbackFactor;
    entity.addVelocity(dX, knockbackFactor / 2, dZ);
    entity.velocityChanged = true;
  }
  
  private static void fillTextureBytes() {
    // fills a map with Block-Byte references to correctly build the golem
    if(ModuleLoader.INSTANCE.isModuleEnabled(ColorSlimeModule.class)) {
      for(int i = 0, l = SLIME_TYPES.length; i < l; i++) {
        final Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark:" + SLIME_TYPES[i] + "_slime_block"));
        textureBytes.put(b, (byte) i);
      }
    }
  }
}
