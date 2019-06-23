package com.mcmoddev.golems.entity;

import java.util.HashMap;
import java.util.Map;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.entity.base.GolemMultiTextured;
import com.mcmoddev.golems.main.ExtraGolems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityWoolGolem extends GolemMultiTextured {

	public static final String WOOL_PREFIX = "wool";
	public static final String[] coloredWoolTypes = { "black", "orange", "magenta", "light_blue",
			"yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green",
			"red", "white" };
	private static final Map<Block, Byte> blockToTexture = new HashMap<>();
	static {
		blockToTexture.put(Blocks.BLACK_WOOL, (byte) 0);
		blockToTexture.put(Blocks.ORANGE_WOOL, (byte) 1);
		blockToTexture.put(Blocks.MAGENTA_WOOL, (byte) 2);
		blockToTexture.put(Blocks.LIGHT_BLUE_WOOL, (byte) 3);
		blockToTexture.put(Blocks.YELLOW_WOOL, (byte) 4);
		blockToTexture.put(Blocks.LIME_WOOL, (byte) 5);
		blockToTexture.put(Blocks.PINK_WOOL, (byte) 6);
		blockToTexture.put(Blocks.GRAY_WOOL, (byte) 7);
		blockToTexture.put(Blocks.LIGHT_GRAY_WOOL, (byte) 8);
		blockToTexture.put(Blocks.CYAN_WOOL, (byte) 9);
		blockToTexture.put(Blocks.PURPLE_WOOL, (byte) 10);
		blockToTexture.put(Blocks.BLUE_WOOL, (byte) 11);
		blockToTexture.put(Blocks.BROWN_WOOL, (byte) 12);
		blockToTexture.put(Blocks.GREEN_WOOL, (byte) 13);
		blockToTexture.put(Blocks.RED_WOOL, (byte) 14);
		blockToTexture.put(Blocks.WHITE_WOOL, (byte) 15);
	}
	
	private boolean secret = false;
	private byte[] iSecret = { 14, 1, 4, 5, 3, 11, 10, 2 };

	public EntityWoolGolem(final EntityType<? extends GolemBase> entityType, final World world) {
		super(entityType, world, ExtraGolems.MODID, WOOL_PREFIX, coloredWoolTypes);
		this.setCanSwim(true);
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
	public void onBuilt(BlockState body, BlockState legs, BlockState arm1, BlockState arm2) {
		// uses HashMap to determine which texture this golem should apply
		// based on the top-middle building block. Defaults to a random texture.
		byte textureNum = blockToTexture.containsKey(body.getBlock()) 
				? blockToTexture.get(body.getBlock()) 
				: (byte)this.rand.nextInt(coloredWoolTypes.length);
		this.setTextureNum(textureNum);
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
