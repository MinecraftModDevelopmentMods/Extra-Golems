package com.golems.entity;

import com.golems.items.ItemBedrockGolem;
import com.golems.main.GolemItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public final class EntityBedrockGolem extends GolemBase {

    public EntityBedrockGolem(final World world) {
        super(world);
        this.setCreativeReturn(new ItemStack(GolemItems.spawnBedrockGolem));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
    }

    @Override
    protected ResourceLocation applyTexture() {
        return makeGolemTexture("bedrock");
    }

    @Override
    public boolean isEntityInvulnerable(final DamageSource src) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean canRenderOnFire() {
        return false;
    }

    @Override
    protected boolean processInteract(final EntityPlayer player, final EnumHand hand) {
        // creative players can "despawn" by using spawnBedrockGolem on this entity
        final ItemStack itemstack = player.getHeldItem(hand);
        if (player.capabilities.isCreativeMode && !itemstack.isEmpty() && itemstack.getItem() == GolemItems.spawnBedrockGolem) {
            player.swingArm(hand);
            if (!this.world.isRemote) {
                this.setDead();
            } else {
                ItemBedrockGolem.spawnParticles(this.world, this.posX - 0.5D, this.posY + 0.1D,
                        this.posZ - 0.5D, 0.1D);
            }
        }

        return super.processInteract(player, hand);
    }

    @Override
    protected void damageEntity(final DamageSource source, final float amount) {
        //
    }

    @Override
    public SoundEvent getGolemSound() {
        return SoundEvents.BLOCK_STONE_STEP;
    }

    @Override
    public List<String> addSpecialDesc(final List<String> list) {
        list.add(TextFormatting.WHITE + "" + TextFormatting.BOLD + trans("entitytip.indestructible"));
        return list;
    }
}
