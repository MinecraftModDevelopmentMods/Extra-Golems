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
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServerBoundSpawnGolemPacket {

    private List<ResourceLocation> ids;

    public ServerBoundSpawnGolemPacket(ResourceLocation id) {
        this(ImmutableList.of(id));
    }

	public ServerBoundSpawnGolemPacket(List<ResourceLocation> ids) {
		this.ids = ImmutableList.copyOf(ids);
	}

    /**
     * Reads the raw packet data from the data stream.
     *
     * @param buf the PacketBuffer
     * @return a new instance of the packet based on the PacketBuffer
     */
    public static ServerBoundSpawnGolemPacket fromBytes(final FriendlyByteBuf buf) {
		// read number of entries
		final int count = buf.readInt();
		// create list
		final List<ResourceLocation> list = new ArrayList<>(count);
		// read each entry
		for(int i = 0; i < count; i++) {
			list.add(buf.readResourceLocation());
		}
        return new ServerBoundSpawnGolemPacket(list);
    }

    /**
     * Writes the raw packet data to the data stream.
     *
     * @param msg the packet
     * @param buf the PacketBuffer
     */
    public static void toBytes(final ServerBoundSpawnGolemPacket msg, final FriendlyByteBuf buf) {
		// write number of entries
		buf.writeInt(msg.ids.size());
		// write each entry
		for(ResourceLocation id : msg.ids) {
			buf.writeResourceLocation(id);
		}
    }

    /**
     * Handles the packet when it is received.
     *
     * @param message the packet
     * @param contextSupplier the NetworkEvent.Context supplier
     */
    public static void handlePacket(final ServerBoundSpawnGolemPacket message, final Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER && context.getSender() != null) {
            context.enqueueWork(() -> {
                // validate player
                final ServerPlayer player = context.getSender();
				// validate permissions
				if(!player.hasPermissions(ExtraGolems.CONFIG.debugPermissionLevel())) {
					return;
				}
				// validate item
				if(!(player.getMainHandItem().is(EGRegistry.ItemReg.GUIDE_BOOK.get()) || player.getOffhandItem().is(EGRegistry.ItemReg.GUIDE_BOOK.get()))) {
					return;
				}
				// iterate list
				for(ResourceLocation id : message.ids) {
					// validate golem ID
					final Registry<Golem> registry = player.level().registryAccess().registryOrThrow(EGRegistry.Keys.GOLEM);
					if(!registry.keySet().contains(id)) {
						return;
					}
					// create a golem at this position
					final GolemBase golem = GolemBase.create(player.level(), id);
					golem.setPlayerCreated(true);
					golem.copyPosition(player);
					// spawn the golem
					player.level().addFreshEntity(golem);
					golem.finalizeSpawn((ServerLevelAccessor) player.level(), player.level().getCurrentDifficultyAt(player.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
					// send feedback
					player.displayClientMessage(Component.translatable("command.golem.success", id, (int) player.getX(), (int) player.getY(), (int) player.getZ()), false);
				}
			});
        }
        context.setPacketHandled(true);
    }
}
