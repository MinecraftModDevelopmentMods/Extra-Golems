package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathResourcePack;

import java.nio.file.Path;

public final class AddonLoader {

	public static final String QUARK = "quark";
	private static final String QUARK_PACK_NAME = "golems_addon_quark";

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
			if(isQuarkLoaded()) {
				registerAddon(event, QUARK_PACK_NAME);
			}
			// TODO register Mekanism data pack
			// TODO register Thermal data pack
		}
	}

	private static void registerAddon(final AddPackFindersEvent event, final String packName) {
		event.addRepositorySource((packConsumer, constructor) -> {
			Pack pack = Pack.create(ExtraGolems.MODID + ":" + packName, true, () -> {
				Path path = ModList.get().getModFileById(ExtraGolems.MODID).getFile().findResource("/" + packName);
				return new PathResourcePack(packName, path);
			}, constructor, Pack.Position.TOP, PackSource.DEFAULT);

			if (pack != null) {
				packConsumer.accept(pack);
			} else {
				ExtraGolems.LOGGER.error(ExtraGolems.MODID + ": Failed to register data pack \"" + packName + "\"");
			}
		});
	}

}
