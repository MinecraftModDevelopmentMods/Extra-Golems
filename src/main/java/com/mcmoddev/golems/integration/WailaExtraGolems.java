package com.mcmoddev.golems.integration;

import java.util.List;

import javax.annotation.Nonnull;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;

import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.util.text.ITextComponent;

/**
 * WAILA integration -- using Hwyla:1.8.23-B38_1.12.
 **/
@WailaPlugin(ExtraGolems.MODID)
public final class WailaExtraGolems extends GolemDescriptionManager implements IEntityComponentProvider {

	public WailaExtraGolems() {
		super();
	}

	public static void callbackRegister(final IRegistrar register) {
		WailaExtraGolems instance = new WailaExtraGolems();
		register.registerComponentProvider(instance, TooltipPosition.BODY, GolemBase.class);
	}

	@Override
	@Nonnull
	public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
		if (accessor.getEntity() instanceof GolemBase) {
			final GolemBase golem = (GolemBase) accessor.getEntity();
			tooltip.addAll(this.getEntityDescription(golem));
		}
	}
}
