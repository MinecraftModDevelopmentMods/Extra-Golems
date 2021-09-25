package com.mcmoddev.golems.item;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;
import javax.swing.*;

import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;


public final class SpawnGolemItem extends Item {
  
  public static final ResourceLocation BEDROCK_GOLEM = new ResourceLocation(ExtraGolems.MODID, "bedrock");

  public SpawnGolemItem() {
    super(new Item.Properties().group(ItemGroup.MISC));
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
	final World worldIn = context.getWorld();
	final PlayerEntity player = context.getPlayer();
	final Direction facing = context.getFace();
	final BlockPos pos = context.getPos();
	final ItemStack stack = context.getItem();

	if ((EGConfig.bedrockGolemCreativeOnly() && !player.isCreative()) || facing == Direction.DOWN) {
	  return ActionResultType.FAIL;
	}

	// check if the golem is enabled
	final Optional<GolemContainer> container = ExtraGolems.GOLEM_CONTAINERS.get(BEDROCK_GOLEM);
	if (container.isPresent()) {
	  // make sure the golem can be spawned here (empty block)
	  BlockState state = worldIn.getBlockState(pos);
	  BlockPos spawnPos;
	  if (state.getCollisionShape(context.getWorld(), context.getPos()).isEmpty()) {
		spawnPos = pos;
	  } else {
		spawnPos = pos.offset(context.getFace());
	  }
	  // attempt to spawn bedrock golem at this position
	  if(worldIn instanceof ServerWorld) {
		final GolemBase entity = GolemBase.create((ServerWorld)worldIn, BEDROCK_GOLEM);
		entity.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
		worldIn.addEntity(entity);
		entity.onInitialSpawn((ServerWorld)worldIn, worldIn.getDifficultyForLocation(spawnPos), SpawnReason.SPAWN_EGG, null, null);
		entity.setInvulnerable(true);
	  }
	  spawnParticles(worldIn, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0.12D);
	  return ActionResultType.SUCCESS;
	}
	return ActionResultType.PASS;
  }

  @Override
  public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity entity, Hand hand) {
	// determine if the entity is a golem
	if(entity instanceof GolemBase) {
	  GolemBase golem = (GolemBase)entity;
	  // determine if the entity is a Bedrock golem
	  if(golem.getMaterial().equals(BEDROCK_GOLEM)) {
		// attempt to remove the entity
		if(!entity.world.isRemote()) {
		  golem.remove();
		}
		// spawn particles
		spawnParticles(playerIn.world, entity.getPosX(), entity.getPosY() + 0.5D, entity.getPosZ(), 0.12D);
		return ActionResultType.SUCCESS;
	  }
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
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    final ITextComponent loreCreativeOnly = new TranslationTextComponent("tooltip.creative_only_item").mergeStyle(TextFormatting.RED);
    final ITextComponent lorePressShift = new TranslationTextComponent("tooltip.press").mergeStyle(TextFormatting.GRAY).appendString(" ")
        .appendSibling(new TranslationTextComponent("tooltip.shift").mergeStyle(TextFormatting.YELLOW)).appendString(" ")
        .appendSibling(new TranslationTextComponent("tooltip.for_more_details").mergeStyle(TextFormatting.GRAY));
    // "Creative-Mode Only"
    tooltip.add(loreCreativeOnly);
    // "Use to spawn Bedrock Golem. Use on existing Bedrock Golem to remove it"
    if (Screen.hasShiftDown()) {
      tooltip.add(new TranslationTextComponent("tooltip.use_to_spawn", new TranslationTextComponent("entity.golems.golem.bedrock")));
      tooltip.add(new TranslationTextComponent("tooltip.use_on_existing", new TranslationTextComponent("entity.golems.golem.bedrock")));
      tooltip.add(new TranslationTextComponent("tooltip.to_remove_it"));
    } else {
      // "Press SHIFT for more details"
      tooltip.add(lorePressShift);
    }
  }
}
