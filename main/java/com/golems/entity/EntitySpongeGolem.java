package com.golems.entity;

import java.util.List;

import com.golems.events.SpongeGolemSoakEvent;
import com.golems.main.Config;
import com.golems.util.WeightedItem;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class EntitySpongeGolem extends GolemBase 
{	
	public static final String ALLOW_SPECIAL = "Allow Special: Absorb Water";
	public static final String INTERVAL = "Water Soaking Frequency";
	public static final String RANGE = "Water Soaking Range";
	public static final String PARTICLES = "Can Render Sponge Particles";
	
	public EntitySpongeGolem(World world) 
	{
		super(world, Config.SPONGE.getBaseAttack(), Blocks.SPONGE);
		this.setCanSwim(true);
	}

	@Override
	protected ResourceLocation applyTexture()
	{
		return this.makeGolemTexture("sponge");
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		int interval = Config.SPONGE.getInt(INTERVAL);
		if(Config.SPONGE.getBoolean(ALLOW_SPECIAL) && (interval <= 1 || this.ticksExisted % interval == 0))
		{
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY - 0.20000000298023224D) + 2;
			int z = MathHelper.floor_double(this.posZ);
			BlockPos center = new BlockPos(x,y,z);
			
			SpongeGolemSoakEvent event = new SpongeGolemSoakEvent(this, center, Config.SPONGE.getInt(RANGE));
			MinecraftForge.EVENT_BUS.post(event);
			if(event.getResult() != Result.DENY)
			{
				event.replaceWater();
			}
		}

		if(Config.SPONGE.getBoolean(PARTICLES) && Math.abs(this.motionX) < 0.05D && Math.abs(this.motionZ) < 0.05D && worldObj.isRemote)
		{
			EnumParticleTypes particle = this.isBurning() ? EnumParticleTypes.SMOKE_NORMAL : EnumParticleTypes.WATER_SPLASH;
			double x = this.rand.nextDouble() - 0.5D * (double)this.width * 0.6D;
			double y = this.rand.nextDouble() * (double)(this.height - 0.75D);
			double z = this.rand.nextDouble() - 0.5D * (double)this.width;
			this.worldObj.spawnParticle(particle, this.posX + x, this.posY + y, this.posZ + z, (this.rand.nextDouble() - 0.5D) * 0.5D, this.rand.nextDouble() - 0.5D, (this.rand.nextDouble() - 0.5D) * 0.5D, new int[0]);
		}
	}

	@Override
	protected void applyAttributes() 
	{
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Config.SPONGE.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.22D);
	}

	@Override
	public void addGolemDrops(List<WeightedItem> dropList, boolean recentlyHit, int lootingLevel)	
	{
		int size = 1 + this.rand.nextInt(3 + lootingLevel);
		this.addDrop(dropList, new ItemStack(Item.getItemFromBlock(Blocks.SPONGE), size > 4 ? 4 : size), 100);
	}

	@Override
	public SoundEvent getGolemSound() 
	{
		return SoundEvents.BLOCK_CLOTH_STEP;
	}
}
