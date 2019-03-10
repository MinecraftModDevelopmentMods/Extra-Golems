package com.mcmoddev.golems.gui;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public final class GuiLoader {

	private GuiLoader() {
		//
	}

    public static void loadBookGui(EntityPlayer playerIn, ItemStack itemstack) {
    	// only load client-side, of course
    	if(!playerIn.getEntityWorld().isRemote)
    		return;
    	// populate the DummyGolems list if it is empty
	    if (ExtraGolems.PROXY.DUMMY_GOLEMS.isEmpty()) {
		    ExtraGolems.PROXY.DUMMY_GOLEMS.addAll(getDummyGolemList(playerIn.world));
    	}
    	// open the gui
    	Minecraft.getInstance().displayGuiScreen(new GuiGolemBook(playerIn, itemstack));
    }

	/**
	 * @return a List containing default instances of each Golem, sorted by attack power.
	 * They do not exist in the world.
	 * TODO: This is super expensive
	 **/
	private static List<GolemBase> getDummyGolemList(final World world) {
		final List<GolemBase> list = new LinkedList<>();
		// for each entity, find out if it's a golem and add it to the list
		for (EntityType<?> entry : net.minecraftforge.registries.ForgeRegistries.ENTITIES) {
			if (GolemBase.class.isAssignableFrom(entry.getEntityClass())) {
				list.add((GolemBase) entry.create(world));
			}
		}
		return list;
	}
}
