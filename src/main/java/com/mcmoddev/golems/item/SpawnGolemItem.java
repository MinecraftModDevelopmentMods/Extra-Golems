package com.mcmoddev.golems.item;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public final class SpawnGolemItem extends Item {

	public static final ResourceLocation BEDROCK_GOLEM = new ResourceLocation(ExtraGolems.MODID, "bedrock");

	public SpawnGolemItem(final Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		final Level level = context.getLevel();
		final Player player = context.getPlayer();
		final Direction facing = context.getClickedFace();
		final BlockPos pos = context.getClickedPos();
		final ItemStack stack = context.getItemInHand();

		if ((ExtraGolems.CONFIG.isBedrockGolemCreativeOnly() && !player.isCreative()) || facing == Direction.DOWN) {
			return InteractionResult.FAIL;
		}

		// check if the entity is enabled
		final GolemContainer container = GolemContainer.getOrCreate(level.registryAccess(), BEDROCK_GOLEM);
		// make sure the entity can be spawned here (empty block)
		BlockState state = level.getBlockState(pos);
		BlockPos spawnPos;
		if (state.getBlockSupportShape(context.getLevel(), context.getClickedPos()).isEmpty()) {
			spawnPos = pos;
		} else {
			spawnPos = pos.relative(context.getClickedFace());
		}
		// attempt to spawn a bedrock entity at this position
		if (!level.isClientSide()) {
			final GolemBase entity = GolemBase.create(level, BEDROCK_GOLEM);
			entity.moveTo(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			level.addFreshEntity(entity);
			entity.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.SPAWN_EGG, null, null);
			entity.setInvulnerable(true);
		}
		// spawn the entity!
		if (!context.getPlayer().isCreative()) {
			stack.shrink(1);
		}
		spawnParticles(level, spawnPos.getX(), spawnPos.getY() + 0.5D, spawnPos.getZ(), 0.12D);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
		// determine if the entity is a golem
		if (entity instanceof IExtraGolem golem) {
			final Optional<ResourceLocation> oId = golem.getGolemId();
			// determine if the entity is a Bedrock golem
			if (oId.isPresent() && BEDROCK_GOLEM.equals(oId.get())) {
				// attempt to remove the entity
				if (!entity.level().isClientSide()) {
					entity.discard();
				}
				// spawn particles
				spawnParticles(playerIn.level(), entity.getX(), entity.getY() + 0.5D, entity.getZ(), 0.12D);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	public static void spawnParticles(final Level world, final double x, final double y, final double z, final double motion) {
		spawnParticles(world, x, y, z, motion, ParticleTypes.LARGE_SMOKE, 60);
	}

	public static void spawnParticles(final Level world, final double x, final double y, final double z, final double motion, final ParticleOptions type,
									  final int num) {
		if (world.isClientSide) {
			for (int i = num + world.random.nextInt(Math.max(1, num / 2)); i > 0; --i) {
				world.addParticle(type, x + world.random.nextDouble() - 0.5D, y + world.random.nextDouble() - 0.5D, z + world.random.nextDouble() - 0.5D,
						world.random.nextDouble() * motion - motion * 0.5D, world.random.nextDouble() * motion * 0.5D,
						world.random.nextDouble() * motion - motion * 0.5D);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		final Component loreCreativeOnly = Component.translatable("tooltip.creative_only_item").withStyle(ChatFormatting.RED);
		final Component lorePressShift = Component.translatable("tooltip.press").withStyle(ChatFormatting.GRAY).append(" ")
				.append(Component.translatable("tooltip.shift").withStyle(ChatFormatting.YELLOW)).append(" ")
				.append(Component.translatable("tooltip.for_more_details").withStyle(ChatFormatting.GRAY));
		// "Creative-Mode Only"
		tooltip.add(loreCreativeOnly);
		// "Use to spawn Bedrock Golem. Use on existing Bedrock Golem to remove it"
		if (Screen.hasShiftDown()) {
			tooltip.add(Component.translatable("tooltip.use_to_spawn", Component.translatable("entity.golems.golem.bedrock")));
			tooltip.add(Component.translatable("tooltip.use_on_existing", Component.translatable("entity.golems.golem.bedrock")));
			tooltip.add(Component.translatable("tooltip.to_remove_it"));
		} else {
			// "Press SHIFT for more details"
			tooltip.add(lorePressShift);
		}
	}
}
