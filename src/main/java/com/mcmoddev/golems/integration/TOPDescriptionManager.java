package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.elements.ElementEntity;
import mcjty.theoneprobe.apiimpl.elements.ElementItemStack;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import snownee.jade.api.ui.IElement;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * TheOneProbe integration
 **/
public final class TOPDescriptionManager extends GolemDescriptionManager implements IProbeInfoEntityProvider {

	public TOPDescriptionManager() {
		super();
	}

	@Override
	public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level,
								   Entity entity, IProbeHitEntityData entityData) {
		if (entity instanceof IExtraGolem golem) {
			// load container
			final Optional<GolemContainer> oContainer = golem.getContainer(level.registryAccess());
			if(oContainer.isEmpty()) {
				return;
			}
			// create pick result item stack
			final ItemStack itemStack = oContainer.get().getGolem().getBlocks().getPickResult();
			// replace entity icon with item icon
			for(int i = 0, n = probeInfo.getElements().size(); i < n; i++) {
				if(probeInfo.getElements().get(i) instanceof ElementEntity element) {
					ElementItemStack itemStackElement = new ElementItemStack(itemStack, new ItemStyle()
							.bounds(element.getWidth(), element.getWidth()));
					//probeInfo.getElements().set(i, itemStackElement);
					break;
				}
			}
			// show behavior data if advanced mode
			this.extended = (mode == ProbeMode.EXTENDED);
			// add entity descriptions
			final List<Component> list = this.getEntityDescription(golem, oContainer.get());
			for (final Component c : list) {
				probeInfo.text(c);
			}
		}
	}

	@Override
	public String getID() {
		return ExtraGolems.MODID;
	}


	public static final class GetTheOneProbe implements Function<ITheOneProbe, Void> {

		@Override
		public Void apply(final ITheOneProbe input) {
			final IProbeInfoEntityProvider instance = new TOPDescriptionManager();
			input.registerEntityProvider(instance);
			return null;
		}
	}
}
