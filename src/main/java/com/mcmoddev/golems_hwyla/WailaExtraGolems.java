package com.mcmoddev.golems_hwyla;

import java.util.List;

import com.mcmoddev.golems.entity.base.GolemBase;
import com.mcmoddev.golems.integration.GolemDescriptionManager;
import com.mcmoddev.golems.main.ExtraGolems;

//import mcp.mobius.waila.api.IEntityAccessor;
//import mcp.mobius.waila.api.IEntityComponentProvider;
//import mcp.mobius.waila.api.IPluginConfig;
//import mcp.mobius.waila.api.IRegistrar;
//import mcp.mobius.waila.api.IWailaPlugin;
//import mcp.mobius.waila.api.TooltipPosition;
//import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * WAILA integration
 **/
//@WailaPlugin(ExtraGolems.MODID)
//public final class WailaExtraGolems extends GolemDescriptionManager implements IEntityComponentProvider, IWailaPlugin {
//
//  public static final WailaExtraGolems INSTANCE = new WailaExtraGolems();
//
//  public WailaExtraGolems() {
//    super();
//    this.showFireproof = false;
//  }
//
//  @Override
//  public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
//    // settings: hold shift to show attack damage
//    this.showAttack = isShiftDown();
//    if (accessor.getEntity() instanceof GolemBase) {
//      final GolemBase golem = (GolemBase) accessor.getEntity();
//      tooltip.addAll(this.getEntityDescription(golem));
//    }
//  }
//
//  @Override
//  public void appendTail(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
//    // Fix the mod name displayed in Waila tooltip
//    for(int i = 0, l = tooltip.size(); i < l; i++) {
//      ITextComponent old = tooltip.get(i);
//      String s = old.getUnformattedComponentText();
//      if(s.contains("golems_quark")) {
//        // replace the raw text with a mod name
//        s = s.replaceFirst("golems_quark", "Extra Golems: Quark");
//        ITextComponent replace = new StringTextComponent(s);
//        // copy siblings
//        replace.getSiblings().addAll(old.getSiblings());
//        tooltip.set(i, replace);
//        // finish up
//        return;
//      }
//    }
//  }
//
//  @Override
//  public void register(IRegistrar register) {
//    register.registerComponentProvider((IEntityComponentProvider) INSTANCE, TooltipPosition.BODY, GolemBase.class);
//    register.registerComponentProvider((IEntityComponentProvider) INSTANCE, TooltipPosition.TAIL, GolemBase.class);
//  }
//}
