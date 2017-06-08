package com.golems.events.handlers;

import com.golems.events.GolemPaperAddInfoEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GolemClientEventHandler 
{
	@SubscribeEvent
	public void onAddInfo(GolemPaperAddInfoEvent event)
	{
		// debug:
		//event.infoList.add("test");
	}
}
