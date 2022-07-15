package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;

/**
 * Tracks which mods are loaded and registered the associated data pack, if any
 */
public final class AddonLoader {

	/** Mod Id for Quark (by Vazkii) **/
	public static final String QUARK = "quark";
	/** Datapack name for Quark addon **/
	private static final String QUARK_PACK_NAME = "golems_addon_quark";

	/** Mod Id for Mekanism (by bradyaidanc) **/
	public static final String MEKANISM = "mekanism";
	/** Datapack name for Mekanism addon **/
	private static final String MEKANISM_PACK_NAME = "golems_addon_mekanism";

	/** Mod Id for Tinkers Construct (by mDiyo) **/
	public static final String TCONSTRUCT = "tconstruct";
	/** Datapack name for Tinkers addon **/
	private static final String TCONSTRUCT_PACK_NAME = "golems_addon_tconstruct";

	/** Mod Id for Biomes O Plenty (by Glitchfiend) **/
	public static final String BIOMESOPLENTY = "biomesoplenty";
	/** Datapack name for Biomes O Plenty addon **/
	private static final String BIOMESOPLENTY_PACK_NAME = "golems_addon_biomesoplenty";

	private static boolean isQuarkLoaded;
	private static boolean isMekanismLoaded;
	private static boolean isTinkersLoaded;
	private static boolean isBiomesOPlentyLoaded;

	/**
	 * Called from FMLCommonSetupEvent to determine which mods are loaded
	 */
	public static void init() {
		isQuarkLoaded = ModList.get().isLoaded(QUARK);
		isMekanismLoaded = ModList.get().isLoaded(MEKANISM);
		isTinkersLoaded = ModList.get().isLoaded(TCONSTRUCT);
		isBiomesOPlentyLoaded = ModList.get().isLoaded(BIOMESOPLENTY);
	}

	/** @return true if Quark is present **/
	public static boolean isQuarkLoaded() {
		return isQuarkLoaded;
	}

	/** @return true if Mekanism is present **/
	public static boolean isMekanismLoaded() {
		return isMekanismLoaded;
	}

	/** @return true if Mekanism is present **/
	public static boolean isBiomesOPlentyLoaded() {
		return isBiomesOPlentyLoaded;
	}

	/** @return true if Tinkers is present **/
	public static boolean isTinkersLoaded() {
		return isTinkersLoaded;
	}

	public static void onAddPackFinders(final AddPackFindersEvent event) {
		if(event.getPackType() == PackType.SERVER_DATA) {
			ExtraGolems.LOGGER.debug(ExtraGolems.MODID + ": addPackFinders");
			// register Quark data pack
			if(isQuarkLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Quark, registering data pack now");
				registerAddon(event, QUARK_PACK_NAME);
			}
			// register Mekanism data pack
			if(isMekanismLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Mekanism, registering data pack now");
				registerAddon(event, MEKANISM_PACK_NAME);
			}
			// register Tinkers data pack
			if(isTinkersLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Tinkers Construct, registering data pack now");
				registerAddon(event, TCONSTRUCT_PACK_NAME);
			}
			// register Biomes O Plenty data pack
			if(isBiomesOPlentyLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Biomes O Plenty, registering data pack now");
				registerAddon(event, BIOMESOPLENTY_PACK_NAME);
			}
			// TODO register Thermal data pack
			// TODO register Botania data pack
		}
	}

	private static void registerAddon(final AddPackFindersEvent event, final String packName) {
		event.addRepositorySource((packConsumer, constructor) -> {
			Pack pack = Pack.create(ExtraGolems.MODID + ":" + packName, true, () -> {
				Path path = ModList.get().getModFileById(ExtraGolems.MODID).getFile().findResource("/" + packName);
				return new PathPackResources(packName, path);
			}, constructor, Pack.Position.TOP, PackSource.DEFAULT);

			if (pack != null) {
				packConsumer.accept(pack);
			} else {
				ExtraGolems.LOGGER.error(ExtraGolems.MODID + ": Failed to register data pack \"" + packName + "\"");
			}
		});
	}

}
