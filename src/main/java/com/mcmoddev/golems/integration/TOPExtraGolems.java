package com.mcmoddev.golems.integration;

import java.util.List;

import com.google.common.base.Function;
import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;

import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

/**
 * TheOneProbe integration
 **/
public final class TOPExtraGolems extends GolemDescriptionManager implements IProbeInfoEntityProvider {

	public TOPExtraGolems() {
		super();
		this.showMultiTexture = true;
		this.showSpecial = true;
	}

	@Override
	public void addProbeEntityInfo(final ProbeMode mode, final IProbeInfo iprobeInfo, final EntityPlayer player,
				       final World world, final Entity entity, final IProbeHitEntityData data) {
		if (entity instanceof GolemBase) {
			final GolemBase golem = (GolemBase) entity;
			// show attack if advanced mode
			this.showAttack = (mode == ProbeMode.EXTENDED);

			final List<ITextComponent> list = this.getEntityDescription(golem);
			for (final ITextComponent s : list) {
				iprobeInfo.text(s.getFormattedText());
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
			final IProbeInfoEntityProvider instance = new TOPExtraGolems();
			input.registerEntityProvider(instance);
			return null;
		}
	}
}
