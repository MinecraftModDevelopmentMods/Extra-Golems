package com.golems.entity;

import java.util.List;

import com.golems.events.IceGolemFreezeEvent;
import com.golems.main.Config;
import com.golems.util.WeightedItem;
import com.google.common.base.Function;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class EntityIceGolem extends GolemBase 
{			
	public static final String ALLOW_SPECIAL = "Allow Special: Freeze Blocks";
	public static final String CAN_USE_REGULAR_ICE = "Can Use Regular Ice";
	public static final String AOE = "Area of Effect";
	
	public EntityIceGolem(World world) 
	{
		super(world, Config.ICE.getBaseAttack(), Blocks.PACKED_ICE);
		this.setCanSwim(true);
	}

	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("ice");
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		// calling every other tick reduces lag by 50%
		if(this.ticksExisted % 2 == 0)
		{
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY - 0.20000000298023224D);
			int z = MathHelper.floor_double(this.posZ);
			BlockPos below = new BlockPos(x,y,z);

			if(this.worldObj.getBiomeGenForCoords(below).getFloatTemperature(below) > 1.0F)
			{
				this.attackEntityFrom(DamageSource.onFire, 1.0F);
			}
			
			if(Config.ICE.getBoolean(ALLOW_SPECIAL))
			{
				IceGolemFreezeEvent event = new IceGolemFreezeEvent(this, below, Config.ICE.getInt(AOE));
				if(!MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Result.DENY)
				{
					this.freezeBlocks(event.getAffectedPositions(), event.getFunction(), event.updateFlag);
				}
			}				
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if(super.attackEntityAsMob(entity))
		{
			if(entity.isBurning())
			{
				this.attackEntityFrom(DamageSource.generic, 0.5F);
			}
			return true;
		}
		return false;  
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.ICE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)
	{
		int size = 1 + lootingLevel;
		this.addDrop(dropList, new ItemStack(Blocks.ICE, size > 4 ? 4 : size), 100);
		if(lootingLevel > 0 || !Config.ICE.getBoolean(CAN_USE_REGULAR_ICE))
		{
			this.addDrop(dropList, Blocks.PACKED_ICE, 0, 0, size > 2 ? 2 : size, 80);
		}
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_GLASS_STEP;
	}
	
	/** 
	 * Usually called after creating and firing a {@link IceGolemFreezeEvent}.
	 * Iterates through the list of positions and calls {@code apply(IBlockState input)}
	 * on the passed Function<IBlockState, IBlockState> . 
	 * @return whether all setBlockState calls were successful.
	 **/
	public boolean freezeBlocks(final List<BlockPos> POSITIONS, final Function<IBlockState, IBlockState> FUNCTION, final int UPDATE_FLAG)
	{		
		boolean flag = false;
		for(int i = 0, len = POSITIONS.size(); i < len; i++)
		{
			final BlockPos POS = POSITIONS.get(i);
			final IBlockState CURRENT_STATE = this.worldObj.getBlockState(POS);
			final IBlockState TO_SET = FUNCTION.apply(CURRENT_STATE);
			if(TO_SET != null && TO_SET != CURRENT_STATE)
			{
				flag &= this.worldObj.setBlockState(POS, TO_SET, UPDATE_FLAG);
			}
		}
		return flag;
	}
}
