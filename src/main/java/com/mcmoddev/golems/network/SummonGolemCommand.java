package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;

public class SummonGolemCommand {

	private static final DynamicCommandExceptionType INVALID_ID = new DynamicCommandExceptionType(arg -> Component.translatable("command.golem.invalid_id", arg));

	public static void register(CommandDispatcher<CommandSourceStack> commandSource) {
		LiteralCommandNode<CommandSourceStack> commandNode = commandSource.register(
				Commands.literal("golem")
						.requires(p -> p.hasPermission(2))
						.then(Commands.argument("type", ResourceLocationArgument.id())
								.executes(command -> summonGolem(command.getSource(),
										ResourceLocationArgument.getId(command, "type"),
										new BlockPos(command.getSource().getPosition()),
										new CompoundTag()))
								.then(Commands.argument("pos", BlockPosArgument.blockPos())
										.executes(command -> summonGolem(command.getSource(),
												ResourceLocationArgument.getId(command, "type"),
												BlockPosArgument.getLoadedBlockPos(command, "pos"),
												new CompoundTag()))
										.then(Commands.argument("tag", CompoundTagArgument.compoundTag())
												.executes(command -> summonGolem(command.getSource(),
														ResourceLocationArgument.getId(command, "type"),
														BlockPosArgument.getLoadedBlockPos(command, "pos"),
														CompoundTagArgument.getCompoundTag(command, "tag")))))));

		commandSource.register(Commands.literal("golem")
				.requires(p -> p.hasPermission(2))
				.redirect(commandNode));
	}

	private static int summonGolem(CommandSourceStack source, ResourceLocation id, BlockPos pos, CompoundTag tag) throws CommandSyntaxException {
		// hard-coded namespace support
		if ("minecraft".equals(id.getNamespace())) {
			id = new ResourceLocation(ExtraGolems.MODID, id.getPath());
		}
		// validate the id
		if (ExtraGolems.GOLEM_CONTAINERS.get(id).isEmpty()) {
			throw INVALID_ID.create(id);
		}
		// create the golem
		tag.putString(GolemBase.KEY_MATERIAL, id.toString());
		final GolemBase entity = GolemBase.create(source.getLevel(), id);
		entity.load(tag);
		entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		source.getLevel().addFreshEntity(entity);
		entity.finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(pos), MobSpawnType.COMMAND, null, tag);
		source.sendSuccess(Component.translatable("command.golem.success", id, pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}
}
