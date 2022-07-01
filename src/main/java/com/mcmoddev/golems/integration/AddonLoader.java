package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathResourcePack;

import java.nio.file.Path;

/**
 * Tracks which mods are loaded and registered the associated data pack, if any
 */
public final class AddonLoader {

	/**
	 * Mod Id for Quark (by Vazkii)
	 **/
	public static final String QUARK = "quark";
	/**
	 * Datapack name for Quark addon
	 **/
	private static final String QUARK_PACK_NAME = "golems_addon_quark";

	/**
	 * Mod Id for Mekanism (by bradyaidanc)
	 **/
	public static final String MEKANISM = "mekanism";
	/**
	 * Datapack name for Mekanism addon
	 **/
	private static final String MEKANISM_PACK_NAME = "golems_addon_mekanism";

	/**
	 * Mod Id for Tinkers Construct (by mDiyo)
	 **/
	public static final String TCONSTRUCT = "tconstruct";
	/**
	 * Datapack name for Tinkers addon
	 **/
	private static final String TCONSTRUCT_PACK_NAME = "golems_addon_tconstruct";

	private static boolean isQuarkLoaded;
	private static boolean isMekanismLoaded;
	private static boolean isTinkersLoaded;

	/**
	 * Called from FMLCommonSetupEvent to determine which mods are loaded
	 */
	public static void init() {
		isQuarkLoaded = ModList.get().isLoaded(QUARK);
		isMekanismLoaded = ModList.get().isLoaded(MEKANISM);
		isTinkersLoaded = ModList.get().isLoaded(TCONSTRUCT);
	}

	/**
	 * @return true if Quark is present
	 **/
	public static boolean isQuarkLoaded() {
		return isQuarkLoaded;
	}

	/**
	 * @return true if Mekanism is present
	 **/
	public static boolean isMekanismLoaded() {
		return isMekanismLoaded;
	}

	/**
	 * @return true if Tinkers is present
	 **/
	public static boolean isTinkersLoaded() {
		return isTinkersLoaded;
	}

	/**
	 * Registers data packs if the corresponding mod is loaded.
	 *
	 * @param event the AddPackFindersEvent
	 * @see #init()
	 */
	public static void onAddPackFinders(final AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			ExtraGolems.LOGGER.debug(ExtraGolems.MODID + ": addPackFinders");
			// register Quark data pack
			if (isQuarkLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Quark, registering data pack now");
				registerAddon(event, QUARK_PACK_NAME);
			}
			// register Mekanism data pack
			if (isMekanismLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Mekanism, registering data pack now");
				registerAddon(event, MEKANISM_PACK_NAME);
			}
			// register Tinkers data pack
			if (isTinkersLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Tinkers Construct, registering data pack now");
				registerAddon(event, TCONSTRUCT_PACK_NAME);
			}
			// TODO register Thermal data pack
			// TODO register Biomes O Plenty data pack
			// TODO register Botania data pack
		}
	}

	/**
	 * Registers a data pack to the pack finders event
	 *
	 * @param event    the event
	 * @param packName the name of the folder that contains the data pack,
	 *                 located at the same level as the usual "data" folder
	 */
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
