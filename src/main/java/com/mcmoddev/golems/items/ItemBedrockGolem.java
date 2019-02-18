package com.mcmoddev.golems.items;

import com.mcmoddev.golems.entity.EntityBedrockGolem;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.main.Config;
import com.mcmoddev.golems.util.GolemLookup;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public final class ItemBedrockGolem extends Item {

	public ItemBedrockGolem() {
		super(new Properties().group(ItemGroup.MISC));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext context) {
		EntityPlayer player = context.getPlayer();
		EnumHand hand = player.getActiveHand();
		EnumFacing facing = context.getFace();
		World worldIn = context.getWorld();
		BlockPos pos = context.getPos();
		final ItemStack stack = player.getHeldItem(hand);
		// creative players can use this item to spawn a bedrock golem
		if (GolemLookup.getConfig(EntityBedrockGolem.class).canSpawn()) {
			if (Config.isBedrockGolemCreativeOnly() && !player.abilities.isCreativeMode) {
				return EnumActionResult.PASS;
			}

			if (facing == EnumFacing.DOWN) {
				return EnumActionResult.FAIL;
			}
			//TODO: High chance of explosion
			final boolean flag = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn.getBlockState(pos)
				, new BlockItemUseContext(context));
			final BlockPos spawn = flag ? pos : pos.offset(facing);

			if (!worldIn.isRemote) {
				final GolemBase golem = new EntityBedrockGolem(worldIn);
				golem.setPlayerCreated(true);
				golem.moveToBlockPosAndAngles(spawn, 0.0F, 0.0F);
				worldIn.spawnEntity(golem);
			}
			spawnParticles(worldIn, pos.getX() - 0.5D, pos.getY() + 1.0D, pos.getZ() - 0.5D, 0.2D);
			player.swingArm(hand);
			if (!player.abilities.isCreativeMode) {
				stack.shrink(1);
			}
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion) {
		if (world.isRemote) {
			for (int i1 = 60 + world.rand.nextInt(30); i1 > 0; --i1) {
				world.spawnParticle(Particles.EXPLOSION, x + world.rand.nextDouble(),
					y + world.rand.nextDouble(), z + world.rand.nextDouble(),
					world.rand.nextDouble() * motion,
					world.rand.nextDouble() * motion * 0.25D + 0.08D,
					world.rand.nextDouble() * motion);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		final String loreCreativeOnly = TextFormatting.RED + trans("tooltip.creative_only_item");
		if (Config.isBedrockGolemCreativeOnly()) {
			tooltip.add(wrap(loreCreativeOnly));
		}

		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(wrap(I18n.format("tooltip.use_to_spawn", trans("entity.golems.golem_bedrock.name"))));
			tooltip.add(wrap(I18n.format("tooltip.use_on_existing",
				trans("entity.golems.golem_bedrock.name"))));
			tooltip.add(wrap(trans("tooltip.to_remove_it") + "."));
		} else {
			final String lorePressShift = TextFormatting.GRAY + trans("tooltip.press") + " "
				+ TextFormatting.YELLOW + trans("tooltip.shift").toUpperCase() + " "
				+ TextFormatting.GRAY + trans("tooltip.for_more_details");
			tooltip.add(wrap(lorePressShift));
		}
	}


	@OnlyIn(Dist.CLIENT)
	private String trans(final String s) {
		return I18n.format(s);
	}
	@OnlyIn(Dist.CLIENT)
	private TextComponentString wrap(String s) {
		return new TextComponentString(s);
	}
}
