package com.mcmoddev.golems.network;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;


public class SummonGolemCommand {
  
  private static final DynamicCommandExceptionType INVALID_ID = new DynamicCommandExceptionType(arg -> new TranslationTextComponent("command.golem.invalid_id", arg));
  
  public static void register(CommandDispatcher<CommandSource> commandSource) {
    LiteralCommandNode<CommandSource> commandNode = commandSource.register(
        Commands.literal("golem")
        .requires(p -> p.hasPermissionLevel(2))
        .then(Commands.argument("type", ResourceLocationArgument.resourceLocation())
            .executes(command -> summonGolem(command.getSource(),
                ResourceLocationArgument.getResourceLocation(command, "type"),
                new BlockPos(command.getSource().getPos()),
                new CompoundNBT()))
            .then(Commands.argument("pos", BlockPosArgument.blockPos())
                .executes(command -> summonGolem(command.getSource(),
                    ResourceLocationArgument.getResourceLocation(command, "type"),
                    BlockPosArgument.getLoadedBlockPos(command, "pos"),
                    new CompoundNBT()))
                .then(Commands.argument("tag", NBTCompoundTagArgument.nbt())
                    .executes(command -> summonGolem(command.getSource(), 
                        ResourceLocationArgument.getResourceLocation(command, "type"),
                        BlockPosArgument.getLoadedBlockPos(command, "pos"), 
                        NBTCompoundTagArgument.getNbt(command, "tag")))))));
    
    commandSource.register(Commands.literal("golem")
        .requires(p -> p.hasPermissionLevel(2))
        .redirect(commandNode));
  }

  private static int summonGolem(CommandSource source, ResourceLocation id, BlockPos pos, CompoundNBT tag) throws CommandSyntaxException {
    // hard-coded namespace support
    if(id.getNamespace().equals("minecraft")) {
      id = new ResourceLocation(ExtraGolems.MODID, id.getPath());
    }
    // validate the id
    if(!ExtraGolems.GOLEM_CONTAINERS.get(id).isPresent()) {
      throw INVALID_ID.create(id);
    }
    // create the golem
    tag.putString(GolemBase.KEY_MATERIAL, id.toString());
    final GolemBase entity = GolemBase.create(source.getWorld(), id);
    entity.read(tag);
    entity.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
    source.getWorld().addEntity(entity);
    entity.onInitialSpawn(source.getWorld(), source.getWorld().getDifficultyForLocation(pos), SpawnReason.COMMAND, null, tag);
    source.sendFeedback(new TranslationTextComponent("command.golem.success", id, pos.getX(), pos.getY(), pos.getZ()), true);
    return 1;
  }
}
