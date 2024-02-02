package com.mcmoddev.golems.network;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;

public class SummonGolemCommand {

	private static final DynamicCommandExceptionType INVALID_ID = new DynamicCommandExceptionType(arg -> Component.translatable("command.golem.invalid_id", arg));
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_ID = ((context, builder) -> SharedSuggestionProvider.suggestResource(
			context.getSource().registryAccess().registryOrThrow(EGRegistry.Keys.GOLEM).keySet().stream().filter(id -> !id.getPath().startsWith("generic_")), builder));

	public static void register(CommandDispatcher<CommandSourceStack> commandSource) {

		LiteralCommandNode<CommandSourceStack> commandNode = commandSource.register(
				Commands.literal("summongolem")
						.requires(p -> p.hasPermission(2))
						.then(Commands.argument("type", ResourceLocationArgument.id())
								.suggests(SUGGEST_ID)
								.executes(command -> summonGolem(command.getSource(),
										ResourceLocationArgument.getId(command, "type"),
										BlockPos.containing(command.getSource().getPosition()),
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

		commandSource.register(Commands.literal("summongolem")
				.requires(p -> p.hasPermission(2))
				.redirect(commandNode));
	}

	private static int summonGolem(CommandSourceStack source, ResourceLocation id, BlockPos pos, CompoundTag tag) throws CommandSyntaxException {
		// hard-coded namespace support
		if ("minecraft".equals(id.getNamespace())) {
			id = new ResourceLocation(ExtraGolems.MODID, id.getPath());
		}
		// validate the id
		final Registry<Golem> registry = source.registryAccess().registryOrThrow(EGRegistry.Keys.GOLEM);
		if (!registry.containsKey(id)) {
			throw INVALID_ID.create(id);
		}
		// create the golem
		tag.putString(IExtraGolem.KEY_GOLEM_ID, id.toString());
		final GolemBase entity = GolemBase.create(source.getLevel(), id);
		entity.load(tag);
		entity.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		source.getLevel().addFreshEntity(entity);
		entity.finalizeSpawn(source.getLevel(), source.getLevel().getCurrentDifficultyAt(pos), MobSpawnType.COMMAND, null, tag);
		ResourceLocation finalId = id;
		source.sendSuccess(() -> Component.translatable("command.golem.success", finalId, pos.getX(), pos.getY(), pos.getZ()), true);
		return 1;
	}
}
