package com.golems.entity;

import com.golems.main.Config;
import com.golems.main.ExtraGolems;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class EntityWoolGolem extends GolemMultiTextured {

	private static final String WOOL_PREFIX = "wool";
	private static final String[] coloredWoolTypes = { "black", "orange", "magenta", "light_blue",
			"yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green",
			"red", "white" };
	private boolean secret = false;
	private byte[] iSecret = { 14, 1, 4, 5, 3, 11, 10, 2 };

	public EntityWoolGolem(final World world) {
		super(world, Config.WOOL.getBaseAttack(), new ItemStack(Blocks.WOOL), WOOL_PREFIX,
				coloredWoolTypes);
		this.setCanSwim(true);
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
	public ItemStack getCreativeReturn() {
		ItemStack woolStack = super.getCreativeReturn();
		woolStack.setItemDamage(this.getTextureNum() % (coloredWoolTypes.length + 1));
		return woolStack;
	}

	@Override
	public String getModId() {
		return ExtraGolems.MODID;
	}

	@Override
	protected void applyAttributes() {
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
				.setBaseValue(Config.WOOL.getMaxHealth());
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30D);
	}

//	@Override
//	public void addGolemDrops(final List<WeightedItem> dropList, final boolean recentlyHit, final int lootingLevel) {
//		// final int size = 1 + this.rand.nextInt(3) + lootingLevel;
//		final int meta = this.getTextureNum() % coloredWoolTypes.length;
//		this.addDrop(dropList, new ItemStack(Blocks.WOOL, 1 + rand.nextInt(2), 0), 100);
//		this.addDrop(dropList, Blocks.WOOL, meta, 1, 2, 60 + lootingLevel * 10);
//		this.addDrop(dropList, Items.STRING, 0, 1, 2, 5 + lootingLevel * 10);
//	}

	@Override
	public SoundEvent getGolemSound() {
		return SoundEvents.BLOCK_CLOTH_STEP;
	}

	@Override
	public void setTextureNum(byte toSet, final boolean updateInstantly) {
		toSet %= (byte) (coloredWoolTypes.length - 1);
		super.setTextureNum(toSet, updateInstantly);
	}
	
	@Override
	public ITextComponent getDisplayName() {
		if(this.secret) {
			String name = getRainbowString(this.getCustomNameTag(), this.getEntityWorld().getWorldTime());
			return new TextComponentString(name);
		} else return super.getDisplayName();
	}
	
	/** Note:  stringIn MUST be unformatted **/
	private String getRainbowString(final String stringIn, final long timeIn) {
		String in = TextFormatting.getTextWithoutFormattingCodes(stringIn);
		String stringOut = "";
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
			   stringOut = stringOut + colorChar[meta % cl] + in.charAt(i);
		   }
		   return stringOut;
	}
}
