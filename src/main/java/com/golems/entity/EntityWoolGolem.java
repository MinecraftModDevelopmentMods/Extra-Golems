package com.golems.entity;

import com.golems.main.Config;
import com.golems.main.ExtraGolems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityWoolGolem extends GolemMultiTextured {

	public static final String WOOL_PREFIX = "wool";
	public static final String[] coloredWoolTypes = { "black", "orange", "magenta", "light_blue",
			"yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green",
			"red", "white" };
	private boolean secret = false;
	private byte[] iSecret = { 14, 1, 4, 5, 3, 11, 10, 2 };

	public EntityWoolGolem(final World world) {
		super(world, WOOL_PREFIX, coloredWoolTypes);
		this.setCanSwim(true);
		this.addHealItem(new ItemStack(Items.STRING), 0.1D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if(this.getEntityWorld().getWorldTime() % 10 == 0) {
			this.secret = Config.matchesSecret(this.getCustomNameTag());
			if(this.secret) {
				int index = (int)((this.getEntityWorld().getWorldTime() % Integer.MAX_VALUE) / 10) % iSecret.length;
				this.setTextureNum(iSecret[index]);
			}
		}
	}

	@Override
	public ItemStack getPickedResult(final RayTraceResult target) {
		ItemStack woolStack = new ItemStack(Blocks.WOOL);
		woolStack.setItemDamage(this.getTextureNum() % (coloredWoolTypes.length + 1));
		return woolStack;
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_CLOTH_STEP;
	}

	@Override
	public void setTextureNum(byte toSet, final boolean updateInstantly) {
		 // note: skips texture for 'white'
		toSet %= (byte) (coloredWoolTypes.length - 1);
		super.setTextureNum(toSet, updateInstantly);
	}
	

	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) { 
		// use block metadata to give this golem the right texture (defaults to 0)
		final int meta = body.getBlock().getMetaFromState(body)
				% this.getTextureArray().length;
		this.setTextureNum((byte) meta);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		if(this.secret) {
			String name = getRainbowString(this.getCustomNameTag(), this.ticksExisted / 2);
			return new TextComponentString(name);
		} 
		return super.getDisplayName();
	}
	
	private static String getRainbowString(final String stringIn, final long timeIn) {
		String in = TextFormatting.getTextWithoutFormattingCodes(stringIn);
		StringBuilder stringOut = new StringBuilder(stringIn.length() * 2);
		int time = (int)(timeIn % (Integer.MAX_VALUE / 2));
		   TextFormatting[] colorChar = 
		      {
		         TextFormatting.RED,
		         TextFormatting.GOLD,
		         TextFormatting.YELLOW,
		         TextFormatting.GREEN,
		         TextFormatting.AQUA,
		         TextFormatting.BLUE,
		         TextFormatting.LIGHT_PURPLE,
		         TextFormatting.DARK_PURPLE
		      };
		   for(int i = 0, l = in.length(), cl = colorChar.length; i < l; i++) {
			   int meta = i + time;
			   stringOut.append(colorChar[meta % cl] + "" + in.charAt(i));
		   }
		   return stringOut.toString();
	}
}
