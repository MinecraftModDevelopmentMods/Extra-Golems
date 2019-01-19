package com.golems.network;

import java.util.ArrayList;
import java.util.List;

import com.golems.main.ExtraGolems;
import com.golems.util.GolemEntry;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketGolemEntries implements IMessage {
	
	private List<GolemEntry> entryList;
	private boolean request;
	
	public PacketGolemEntries(List<GolemEntry> entries, boolean isRequest) {
		this.entryList = isRequest ? new ArrayList<GolemEntry> () : entries;
		this.request = isRequest;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		// read length
		int size = buf.readInt();
		for(int i = 0; i < size; i++) {
			ByteBufUtils.readTag(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// write length
		buf.writeInt(entryList.size());
		for(GolemEntry entry : entryList) {
			NBTTagCompound tag = entry.serializeNBT();
			ByteBufUtils.writeTag(buf, tag);
		}
	}
	
	public boolean isRequestFromClient() {
		return this.request;
	}
	
	public List<GolemEntry> getEntryList() {
		return this.entryList;
	}
	
	public static class PacketHandler implements IMessageHandler<PacketGolemEntries, IMessage> {

		@Override
		public IMessage onMessage(PacketGolemEntries message, MessageContext ctx) {
			if(ctx.side == Side.CLIENT) {
				ExtraGolems.proxy.GOLEMS.clear();
				ExtraGolems.proxy.GOLEMS.addAll(message.getEntryList());
				return null;
			}
			else if(ctx.side == Side.SERVER && message.isRequestFromClient()) {
				// populate GolemEntry list if needed, send it back
				if(ExtraGolems.proxy.GOLEMS.isEmpty()) {
					GolemEntry.addGolemEntries(ctx.getServerHandler().player.getEntityWorld(), ExtraGolems.proxy.GOLEMS);
				}
				return new PacketGolemEntries(ExtraGolems.proxy.GOLEMS, false);
			}
			return null;
		}
		
	}
}
