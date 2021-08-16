package com.mcmoddev.golems.network;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SSpawnGolemPacket {

  private final ResourceLocation material;
  private final GolemBase entity;
  private final int entityId;
  private final UUID uuid;
  private final double posX, posY, posZ;
  private final byte pitch, yaw, headYaw;
  private final FriendlyByteBuf buf;

  public SSpawnGolemPacket(GolemBase e, ResourceLocation m) {
    this.material = m;
    this.entity = e;
    this.entityId = e.getId();
    this.uuid = e.getUUID();
    this.posX = e.getX();
    this.posY = e.getY();
    this.posZ = e.getZ();
    this.pitch = (byte) Mth.floor(e.getXRot() * 256.0F / 360.0F);
    this.yaw = (byte) Mth.floor(e.getYRot() * 256.0F / 360.0F);
    this.headYaw = (byte) (e.getYHeadRot() * 256.0F / 360.0F);
    this.buf = null;
  }

  public SSpawnGolemPacket(ResourceLocation m, int entityId, UUID uuid, 
      double posX, double posY, double posZ, byte pitch,
      byte yaw, byte headYaw, FriendlyByteBuf buf) {
    this.material = m;
    this.entity = null;
    this.entityId = entityId;
    this.uuid = uuid;
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;
    this.pitch = pitch;
    this.yaw = yaw;
    this.headYaw = headYaw;
    this.buf = buf;
  }

  /**
   * Writes the raw packet data to the data stream.
   * 
   * @param msg the SSpawnGolemPacket
   * @param buf the FriendlyByteBuf
   */
  public static void toBytes(SSpawnGolemPacket msg, FriendlyByteBuf buf) {
    buf.writeResourceLocation(msg.material);
    buf.writeInt(msg.entityId);
    buf.writeLong(msg.uuid.getMostSignificantBits());
    buf.writeLong(msg.uuid.getLeastSignificantBits());
    buf.writeDouble(msg.posX);
    buf.writeDouble(msg.posY);
    buf.writeDouble(msg.posZ);
    buf.writeByte(msg.pitch);
    buf.writeByte(msg.yaw);
    buf.writeByte(msg.headYaw);
    if (msg.entity instanceof IEntityAdditionalSpawnData) {
      ((IEntityAdditionalSpawnData) msg.entity).writeSpawnData(buf);
    }
  }

  /**
   * Reads the raw packet data from the data stream.
   * @param buf the FriendlyByteBuf
   * @return a new instance of a SGolemContainerPacket based on the FriendlyByteBuf
   */
  public static SSpawnGolemPacket fromBytes(FriendlyByteBuf buf) {
    return new SSpawnGolemPacket(buf.readResourceLocation(), buf.readInt(), new UUID(buf.readLong(), buf.readLong()),
        buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readByte(), buf.readByte(), buf.readByte(), buf);
  }

  /**
   * Handles the packet when it is received.
   * @param msg the SSpawnGolemPacket
   * @param ctx the NetworkEvent.Context supplier
   */
  public static void handle(SSpawnGolemPacket msg, Supplier<NetworkEvent.Context> ctx) {
    NetworkEvent.Context context = ctx.get();
    if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
      context.enqueueWork(() -> {
        EntityType<GolemBase> type = EGRegistry.GOLEM;
  
        Optional<Level> world = LogicalSidedProvider.CLIENTWORLD.get(context.getDirection().getReceptionSide());
        GolemBase e;
        if(world.isPresent()) {
          e = GolemBase.create(world.get(), msg.material);
        } else {
          ExtraGolems.LOGGER.error("Failed to handle SSPawnGolemPacket at [" + msg.posX + ", " + msg.posY + ", " + msg.posZ + "]");
          return;
        }
  
        e.setPacketCoordinates(msg.posX, msg.posY, msg.posZ);
        e.absMoveTo(msg.posX, msg.posY, msg.posZ, (msg.yaw * 360) / 256.0F, (msg.pitch * 360) / 256.0F);
        e.setYHeadRot((msg.headYaw * 360) / 256.0F);
        e.setYBodyRot((msg.headYaw * 360) / 256.0F);
  
        e.setId(msg.entityId);
        e.setUUID(msg.uuid);
        world.filter(ClientLevel.class::isInstance).ifPresent(w -> ((ClientLevel) w).putNonPlayerEntity(msg.entityId, e));
        if (e instanceof IEntityAdditionalSpawnData) {
          ((IEntityAdditionalSpawnData) e).readSpawnData(msg.buf);
        }
      });
    }
    context.setPacketHandled(true);
  }
}
