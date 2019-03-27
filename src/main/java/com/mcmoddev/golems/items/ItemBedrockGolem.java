package com.mcmoddev.golems.items;

import com.mcmoddev.golems.entity.EntityBedrockGolem;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
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
import java.util.Objects;

public final class ItemBedrockGolem extends Item {

	public ItemBedrockGolem() {
		super(new Properties().group(ItemGroup.MISC));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext context) {
		final World worldIn = context.getWorld();
		final EntityPlayer player = context.getPlayer();
		final EnumFacing facing = context.getFace();
		final BlockPos pos = context.getPos();
		final ItemStack stack = context.getItem();

		if((/* Config.isBedrockGolemCreativeOnly() && */ !player.abilities.isCreativeMode) || facing == EnumFacing.DOWN) {
			return EnumActionResult.FAIL;
		}

		// check if the golem is enabled
		if (GolemRegistrar.getContainer(EntityBedrockGolem.class).enabled) {
			// make sure the golem can be spawned here (empty block)
			IBlockState state = worldIn.getBlockState(pos);
			BlockPos spawnPos;
			if (state.getCollisionShape(context.getWorld(), context.getPos()).isEmpty()) {
				spawnPos = pos;
			} else {
				spawnPos = pos.offset(context.getFace());
			}
			// attempt to spawn a bedrock golem at this position
			EntityType<?> entitytype = GolemRegistrar.getContainer(EntityBedrockGolem.class).entityType;
			if (!worldIn.isRemote && entitytype != null) {
				// spawn the golem!
				entitytype.spawnEntity(worldIn, stack, player, spawnPos, true,
						!Objects.equals(pos, spawnPos) && facing == EnumFacing.UP);
				stack.shrink(1);
			}
			spawnParticles(worldIn, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.12D);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}

	public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion) {
		if (world.isRemote) {
			for (int i1 = 60 + world.rand.nextInt(30); i1 > 0; --i1) {
				world.spawnParticle(Particles.LARGE_SMOKE, x + world.rand.nextDouble() - 0.5D,
					y + world.rand.nextDouble() - 0.5D, z + world.rand.nextDouble() - 0.5D,
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
		if (/*Config.isBedrockGolemCreativeOnly()*/ true) {
			tooltip.add(wrap(loreCreativeOnly));
		}

		if (GuiScreen.isShiftKeyDown()) {
			tooltip.add(wrap(I18n.format("tooltip.use_to_spawn", trans("entity.golems.golem_bedrock"))));
			tooltip.add(wrap(I18n.format("tooltip.use_on_existing",
				trans("entity.golems.golem_bedrock"))));
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
