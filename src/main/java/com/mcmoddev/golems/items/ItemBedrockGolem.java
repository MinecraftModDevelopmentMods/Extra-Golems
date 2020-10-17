package com.mcmoddev.golems.items;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems.util.config.ExtraGolemsConfig;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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

    if ((ExtraGolemsConfig.bedrockGolemCreativeOnly() && !player.abilities.isCreativeMode) || facing == Direction.DOWN) {
      return ActionResultType.FAIL;
    }

    // check if the golem is enabled
    final GolemContainer container = GolemRegistrar.getContainer(new ResourceLocation(ExtraGolems.MODID, GolemNames.BEDROCK_GOLEM));
    if (container != null && container.isEnabled()) {
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
        entitytype.spawn(worldIn, stack, player, spawnPos, SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, spawnPos) && facing == Direction.UP);
        stack.shrink(1);
      }
      spawnParticles(worldIn, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.12D);
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }

  public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion) {
    spawnParticles(world, x, y, z, motion, ParticleTypes.LARGE_SMOKE, 60);
  }

  public static void spawnParticles(final World world, final double x, final double y, final double z, final double motion, final IParticleData type,
      final int num) {
    if (world.isRemote) {
      for (int i = num + world.rand.nextInt(Math.max(1, num / 2)); i > 0; --i) {
        world.addParticle(type, x + world.rand.nextDouble() - 0.5D, y + world.rand.nextDouble() - 0.5D, z + world.rand.nextDouble() - 0.5D,
            world.rand.nextDouble() * motion - motion * 0.5D, world.rand.nextDouble() * motion * 0.5D,
            world.rand.nextDouble() * motion - motion * 0.5D);
      }
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    final ITextComponent loreCreativeOnly = trans("tooltip.creative_only_item").applyTextStyle(TextFormatting.RED);
    final ITextComponent lorePressShift = trans("tooltip.press").applyTextStyle(TextFormatting.GRAY).appendSibling(wrap(" "))
        .appendSibling(trans("tooltip.shift").applyTextStyle(TextFormatting.YELLOW)).appendSibling(wrap(" "))
        .appendSibling(trans("tooltip.for_more_details").applyTextStyle(TextFormatting.GRAY));
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

  private static TranslationTextComponent trans(final String s, final Object... param) {
    return new TranslationTextComponent(s, param);
  }

  private static StringTextComponent wrap(final String s) {
    return new StringTextComponent(s);
  }

}
