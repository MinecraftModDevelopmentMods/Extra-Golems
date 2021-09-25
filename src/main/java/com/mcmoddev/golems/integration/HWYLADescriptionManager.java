package com.mcmoddev.golems.integration;

import java.util.List;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.util.text.ITextComponent;

@WailaPlugin(ExtraGolems.MODID)
public final class HWYLADescriptionManager extends GolemDescriptionManager implements IEntityComponentProvider, IWailaPlugin {

  public static final HWYLADescriptionManager INSTANCE = new HWYLADescriptionManager();

  public HWYLADescriptionManager() {
	super();
  }

  @Override
  public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
	// settings: hold shift to show attack damage
	this.showAttack = isShiftDown();
	if (accessor.getEntity() instanceof GolemBase) {
	  final GolemBase golem = (GolemBase) accessor.getEntity();
	  tooltip.addAll(this.getEntityDescription(golem));
	}
  }

  @Override
  public void register(IRegistrar register) {
	register.registerComponentProvider((IEntityComponentProvider) INSTANCE, TooltipPosition.BODY, GolemBase.class);
  }
}
