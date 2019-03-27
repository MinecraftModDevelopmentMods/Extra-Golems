package com.mcmoddev.golems.entity;

import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
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
		super(EntityWoolGolem.class, world, WOOL_PREFIX, coloredWoolTypes);
		this.setCanSwim(true);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

//	@Override
//	public void livingTick() {
//		super.livingTick();
//
//		if(this.getEntityWorld().getWorldTime() % 10 == 0) {
//			this.secret = Config.matchesSecret(this.getCustomNameTag());
//			if(this.secret) {
//				int index = (int)((this.getEntityWorld().getWorldTime() % Integer.MAX_VALUE) / 10) % iSecret.length;
//				this.setTextureNum(iSecret[index]);
//			}
//		}
//	}
//
//	@Override
//	public ItemStack getCreativeReturn() {
//		ItemStack woolStack = super.getCreativeReturn();
//		woolStack.setItemDamage(this.getTextureNum() % (coloredWoolTypes.length + 1));
//		return woolStack;
//	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_WOOL_STEP;
	}

	@Override
	public void setTextureNum(byte toSet, final boolean updateInstantly) {
		//  note: skips texture for 'white'
		toSet %= (byte) (coloredWoolTypes.length - 1);
		super.setTextureNum(toSet, updateInstantly);
	}


	@Override
	public void onBuilt(IBlockState body, IBlockState legs, IBlockState arm1, IBlockState arm2) {
		/* TODO */
		// use block metadata to give this golem the right texture (defaults to 0)
//		final int meta = body.getBlock().getMetaFromState(body)
//				% this.getTextureArray().length;
//		this.setTextureNum((byte) meta);
	}

//	@Override
//	public ITextComponent getDisplayName() {
//		if(this.secret) {
//			String name = getRainbowString(this.getCustomNameTag(), this.ticksExisted / 2);
//			return new TextComponentString(name);
//		} else return super.getDisplayName();
//	}

	private static String getRainbowString(final String stringIn, final long timeIn) {
		String in = TextFormatting.getTextWithoutFormattingCodes(stringIn);
		StringBuilder stringOut = new StringBuilder(stringIn.length() * 2);
		int time = timeIn > Integer.MAX_VALUE / 2 ? Integer.MAX_VALUE / 2 : (int)timeIn;
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
			   int meta = i + time ;
			   stringOut.append(colorChar[meta % cl] + "" + in.charAt(i));
		   }
		   return stringOut.toString();
	}
}
