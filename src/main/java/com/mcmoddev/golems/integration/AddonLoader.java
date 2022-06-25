package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;

import java.io.File;

public final class AddonLoader {

	public static final String QUARK = "quark";

	private static boolean isQuarkLoaded;

	public static void init() {
		isQuarkLoaded = ModList.get().isLoaded(QUARK);
	}


	public static boolean isQuarkLoaded() {
		return isQuarkLoaded;
	}

	public static void onAddPackFinders(final AddPackFindersEvent event) {
		if(event.getPackType() == PackType.SERVER_DATA) {
			ExtraGolems.LOGGER.debug(ExtraGolems.MODID + ": addPackFinders");
			// register Quark data pack
			// TODO add condition: isQuarkLoaded
			event.addRepositorySource((packConsumer, constructor) -> {
				Pack pack = Pack.create(ExtraGolems.MODID + ":data_quark", true, () -> {
					File file = new File("/data_quark");
					return new FolderPackResources(file);
				}, constructor, Pack.Position.TOP, PackSource.DEFAULT);

				if (pack != null) {
					packConsumer.accept(pack);
				} else {
					ExtraGolems.LOGGER.error(ExtraGolems.MODID + ": Failed to add compatibility data pack for Quark");
				}
			});


		}
	}

}
