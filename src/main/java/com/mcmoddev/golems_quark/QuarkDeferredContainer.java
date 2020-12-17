package com.mcmoddev.golems_quark;

import java.util.function.Predicate;

import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.integration.DeferredContainer;
import com.mcmoddev.golems.util.GolemContainer;

import vazkii.quark.base.module.QuarkModule;

public class QuarkDeferredContainer extends DeferredContainer {
  public Class<? extends QuarkModule> module;
  public Predicate<Class<? extends QuarkModule>> enabled;
  
  public QuarkDeferredContainer(final GolemContainer lContainer, final Class<? extends QuarkModule> lModule, 
      final String[] lBlocks, final Predicate<Class<? extends QuarkModule>> lEnabled) {
    super(lContainer, AddonLoader.QUARK_MODID, lBlocks);
    this.module = lModule;
    this.enabled = lEnabled;
  }
}