package com.mcmoddev.golems.items;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.world.item.Item.Properties;

public final class ItemBedrockGolem extends Item {

  public ItemBedrockGolem() {
    super(new Properties().tab(CreativeModeTab.TAB_MISC));
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    final Level worldIn = context.getLevel();
    final Player player = context.getPlayer();
    final Direction facing = context.getClickedFace();
    final BlockPos pos = context.getClickedPos();
    final ItemStack stack = context.getItemInHand();

    if ((ExtraGolemsConfig.bedrockGolemCreativeOnly() && !player.abilities.instabuild) || facing == Direction.DOWN) {
      return InteractionResult.FAIL;
    }

    // check if the golem is enabled
    final GolemContainer container = GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.BEDROCK_GOLEM));
    if (container != null && container.isEnabled()) {
      // make sure the golem can be spawned here (empty block)
      BlockState state = worldIn.getBlockState(pos);
      BlockPos spawnPos;
      if (state.getBlockSupportShape(context.getLevel(), context.getClickedPos()).isEmpty()) {
        spawnPos = pos;
      } else {
        spawnPos = pos.relative(context.getClickedFace());
      }
      // attempt to spawn a bedrock golem at this position
      EntityType<?> entitytype = container.getEntityType();
      if (!worldIn.isClientSide() && entitytype != null) {
        // spawn the golem!
        entitytype.spawn((ServerLevel)worldIn, stack, player, spawnPos, MobSpawnType.SPAWN_EGG, true, !Objects.equals(pos, spawnPos) && facing == Direction.UP);
        stack.shrink(1);
      }
      spawnParticles(worldIn, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.12D);
      return InteractionResult.SUCCESS;
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
  @OnlyIn(Dist.CLIENT)
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    final Component loreCreativeOnly = trans("tooltip.creative_only_item").withStyle(ChatFormatting.RED);
    final Component lorePressShift = trans("tooltip.press").withStyle(ChatFormatting.GRAY).append(" ")
        .append(trans("tooltip.shift").withStyle(ChatFormatting.YELLOW)).append(" ")
        .append(trans("tooltip.for_more_details").withStyle(ChatFormatting.GRAY));
    // "Creative-Mode Only"
    tooltip.add(loreCreativeOnly);
    // "Use to spawn Bedrock Golem. Use on existing Bedrock Golem to remove it"
    if (Screen.hasShiftDown()) {
      tooltip.add(trans("tooltip.use_to_spawn", trans("entity.golems.golem_bedrock")));
      tooltip.add(trans("tooltip.use_on_existing", trans("entity.golems.golem_bedrock")));
      tooltip.add(trans("tooltip.to_remove_it"));
    } else {
      // "Press SHIFT for more details"
      tooltip.add(lorePressShift);
    }
  }

  private static TranslatableComponent trans(final String s, final Object... param) {
    return new TranslatableComponent(s, param);
  }
}
