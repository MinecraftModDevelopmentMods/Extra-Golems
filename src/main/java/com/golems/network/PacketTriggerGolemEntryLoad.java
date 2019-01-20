package com.golems.network;

import java.util.ArrayList;
import java.util.List;

import com.golems.gui.GuiLoader;
import com.golems.main.ExtraGolems;
import com.golems.util.GolemEntry;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketTriggerGolemEntryLoad implements IMessage {

	public PacketTriggerGolemEntryLoad() { }
	
	@Override
	public void fromBytes(ByteBuf buf) { }

	@Override
	public void toBytes(ByteBuf buf) { }
	
	
	public static class PacketHandler implements IMessageHandler<PacketTriggerGolemEntryLoad, IMessage> {

		public PacketHandler() { }
		
		@Override
		public IMessage onMessage(PacketTriggerGolemEntryLoad message, MessageContext ctx) {
			System.out.println("Received Packet on " + ctx.side);
			// PROCESS ON CLIENT
			if(ctx.side == Side.CLIENT && GuiLoader.shouldReloadGolems()) {
					System.out.println("Populating Golem Entries");
					ExtraGolems.proxy.DUMMY_GOLEMS.addAll(GolemEntry.getDummyGolemList(ctx.getServerHandler().player.getEntityWorld()));
			}
			return null;
		}
		
	}
}
