package com.mcmoddev.golems_quark;

import java.util.function.Predicate;

import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.integration.DeferredContainer;
import com.mcmoddev.golems.util.GolemContainer;

import vazkii.quark.base.module.Module;

public class QuarkDeferredContainer extends DeferredContainer {
  public Class<? extends Module> module;
  public Predicate<Class<? extends Module>> enabled;
  
  public QuarkDeferredContainer(final GolemContainer lContainer, final Class<? extends Module> lModule, 
      final String[] lBlocks, final Predicate<Class<? extends Module>> lEnabled) {
    super(lContainer, AddonLoader.QUARK_MODID, lBlocks);
    this.module = lModule;
    this.enabled = lEnabled;
  }
}