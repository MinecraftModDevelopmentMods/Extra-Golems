/**
 * Copyright (c) 2023 Skyler James
 * Permission is granted to use, modify, and redistribute this software, in parts or in whole,
 * under the GNU LGPLv3 license (https://www.gnu.org/licenses/lgpl-3.0.en.html)
 **/

package com.mcmoddev.golems.network;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGConfig;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.entity.GolemBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ServerBoundSpawnGolemPacket(List<ResourceLocation> ids) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerBoundSpawnGolemPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(ExtraGolems.MODID, "spawn_golem"));

    public static final StreamCodec<ByteBuf, ServerBoundSpawnGolemPacket> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
            ServerBoundSpawnGolemPacket::ids,
            ServerBoundSpawnGolemPacket::new);

    public ServerBoundSpawnGolemPacket(ResourceLocation id) {
        this(ImmutableList.of(id));
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * Handles the packet when it is received.
     */
    public static void handlePacket(final ServerBoundSpawnGolemPacket message, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // validate player
            final ServerPlayer player = (ServerPlayer) context.player();
            // validate permissions
            if (!player.hasPermissions(ExtraGolems.CONFIG.debugPermissionLevel())) {
                return;
            }
            // validate item
            if (!(player.getMainHandItem().is(EGRegistry.ItemReg.GUIDE_BOOK.get())
                    || player.getOffhandItem().is(EGRegistry.ItemReg.GUIDE_BOOK.get()))) {
                return;
            }
            // iterate list
            for (ResourceLocation id : message.ids) {
                // validate golem ID
                final Registry<Golem> registry = player.level().registryAccess().registryOrThrow(EGRegistry.Keys.GOLEM);
                if (!registry.keySet().contains(id)) {
                    return;
                }
                // create a golem at this position
                final GolemBase golem = GolemBase.create(player.level(), id);
                golem.setPlayerCreated(true);
                golem.copyPosition(player);
                // spawn the golem
                player.level().addFreshEntity(golem);
                golem.finalizeSpawn((ServerLevelAccessor) player.level(),
                        player.level().getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                // send feedback
                player.displayClientMessage(Component.translatable("command.golem.success", id, (int) player.getX(),
                        (int) player.getY(), (int) player.getZ()), false);
            }
        });
    }
}
