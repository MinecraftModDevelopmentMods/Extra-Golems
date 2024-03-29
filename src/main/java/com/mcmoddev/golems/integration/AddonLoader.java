package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

	/** Mod Id for Biomes O Plenty (by Glitchfiend) **/
	public static final String BIOMESOPLENTY = "biomesoplenty";
	/** Datapack name for Biomes O Plenty addon **/
	private static final String BIOMESOPLENTY_PACK_NAME = "golems_addon_biomesoplenty";

	/** Mod Id for Thermal Series (by TeamCoFH) **/
	public static final String THERMAL = "thermal";
	/** Datapack name for Thermal Series addon **/
	private static final String THERMAL_PACK_NAME = "golems_addon_thermal";

	private static boolean isQuarkLoaded;
	private static boolean isMekanismLoaded;
	private static boolean isBiomesOPlentyLoaded;
	private static boolean isThermalLoaded;

	/**
	 * Called from FMLCommonSetupEvent to determine which mods are loaded
	 */
	public static void register() {
		isQuarkLoaded = ModList.get().isLoaded(QUARK);
		isMekanismLoaded = ModList.get().isLoaded(MEKANISM);
		isBiomesOPlentyLoaded = ModList.get().isLoaded(BIOMESOPLENTY);
		isThermalLoaded = ModList.get().isLoaded(THERMAL);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(AddonLoader::onAddPackFinders);
	}

	/** @return true if Quark is present **/
	public static boolean isQuarkLoaded() {
		return isQuarkLoaded;
	}

	/** @return true if Mekanism is present **/
	public static boolean isMekanismLoaded() {
		return isMekanismLoaded;
	}

	/** @return true if Biomes O Plenty is present **/
	public static boolean isBiomesOPlentyLoaded() {
		return isBiomesOPlentyLoaded;
	}

	/** @return true if Thermal Series is loaded **/
	public static boolean isThermalLoaded() {
		return isThermalLoaded;
	}

	public static void onAddPackFinders(final AddPackFindersEvent event) {
		if(event.getPackType() == PackType.SERVER_DATA) {
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
			// register Biomes O Plenty data pack
			if(isBiomesOPlentyLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Biomes O Plenty, registering data pack now");
				registerAddon(event, BIOMESOPLENTY_PACK_NAME);
			}
			// register Thermal data pack
			if(isThermalLoaded()) {
				ExtraGolems.LOGGER.info("Extra Golems detected Thermal Series, registering data pack now");
				registerAddon(event, THERMAL_PACK_NAME);
			}
			// TODO register Botania data pack
			// TODO register Biomes Youll Go data pack
		}
	}

	private static void registerAddon(final AddPackFindersEvent event, final String packName) {
		event.addRepositorySource(packConsumer -> {
			// create pack data
			final String packId = ExtraGolems.MODID + ":" + packName;
			final Component packTitle = Component.literal(packName);
			final Path path = ModList.get().getModFileById(ExtraGolems.MODID).getFile().findResource("/" + packName);
			final Pack.Info info = new Pack.Info(packTitle, SharedConstants.DATA_PACK_FORMAT, FeatureFlagSet.of());
			// create the pack
			Pack pack = Pack.create(packId, packTitle, true, s -> new PathPackResources(packName, false, path), info,
					PackType.SERVER_DATA, Pack.Position.TOP, true, PackSource.DEFAULT);
			// consume the pack
			packConsumer.accept(pack);
		});
	}

}
