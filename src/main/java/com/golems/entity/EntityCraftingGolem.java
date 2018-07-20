package com.golems.entity;

import com.golems.blocks.ContainerPortableWorkbench;
import com.golems.main.Config;
import com.golems.util.WeightedItem;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityCraftingGolem extends GolemBase
{
	public EntityCraftingGolem(World world) 
	{
		super(world, Config.CRAFTING.getBaseAttack(), Blocks.CRAFTING_TABLE);
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.CRAFTING.getMaxHealth());
	  	this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.29D);
	}

	@Override
	protected ResourceLocation applyTexture() 
	{
		return makeGolemTexture("crafting");
	}
	
	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		ItemStack itemstack = player.getHeldItem(hand);
		if(!player.world.isRemote && itemstack.isEmpty())
		{
			// display crafting grid for player
			player.displayGui(new EntityCraftingGolem.InterfaceCraftingGrid(player.world, player.bedLocation));
			player.addStat(StatList.CRAFTING_TABLE_INTERACTION);
			player.swingArm(hand);
		}
		
		return super.processInteract(player, hand);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel) 
	{
		this.addDrop(dropList, new ItemStack(Blocks.CRAFTING_TABLE, 1 + rand.nextInt(2)), 100);
		this.addDrop(dropList, Blocks.PLANKS, 0, 1, 6, 70 + lootingLevel * 10);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_WOOD_STEP;
	}

	public static class InterfaceCraftingGrid extends net.minecraft.block.BlockWorkbench.InterfaceCraftingTable
    {
        private final World world2;
        private final BlockPos position2;

        public InterfaceCraftingGrid(World worldIn, BlockPos pos)
        {
            super(worldIn, pos);
            this.world2 = worldIn;
            this.position2 = pos;
        }

        @Override
        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerPortableWorkbench(playerInventory, this.world2, this.position2);
        }
    }
}
