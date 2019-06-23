package com.mcmoddev.golems.items;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.mcmoddev.golems.entity.EntityBedrockGolem;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Particles;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class ItemBedrockGolem extends Item {

	public ItemBedrockGolem() {
		super(new Properties().group(ItemGroup.MISC));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		final World worldIn = context.getWorld();
		final PlayerEntity player = context.getPlayer();
		final Direction facing = context.getFace();
		final BlockPos pos = context.getPos();
		final ItemStack stack = context.getItem();

		if((ExtraGolemsConfig.bedrockGolemCreativeOnly() && !player.abilities.isCreativeMode) 
				|| facing == Direction.DOWN) {
			return ActionResultType.FAIL;
		}

		// check if the golem is enabled
		final GolemContainer container; // TODO initialize with what information?
		if (container.isEnabled()) {
			// make sure the golem can be spawned here (empty block)
			BlockState state = worldIn.getBlockState(pos);
			BlockPos spawnPos;
			if (state.getCollisionShape(context.getWorld(), context.getPos()).isEmpty()) {
				spawnPos = pos;
			} else {
				spawnPos = pos.offset(context.getFace());
			}
			// attempt to spawn a bedrock golem at this position
			EntityType<?> entitytype = container.getEntityType();
			if (!worldIn.isRemote && entitytype != null) {
				// spawn the golem!
				entitytype.spawn(worldIn, stack, player, spawnPos, SpawnReason.SPAWN_EGG, true,
						!Objects.equals(pos, spawnPos) && facing == Direction.UP);
				stack.shrink(1);
			}
			spawnParticles(worldIn, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.12D);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion) {
		if (world.isRemote) {
			for (int i1 = 60 + world.rand.nextInt(30); i1 > 0; --i1) {
				world.addParticle(ParticleTypes.LARGE_SMOKE, x + world.rand.nextDouble() - 0.5D,
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

		if (Screen.hasShiftDown()) {
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
	private static String trans(final String s) {
		return I18n.format(s);
	}
	@OnlyIn(Dist.CLIENT)
	private static StringTextComponent wrap(String s) {
		return new StringTextComponent(s);
	}
}
