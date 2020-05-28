package com.mcmoddev.golems_quark.util;

import com.mcmoddev.golems.util.config.GolemContainer;

import vazkii.quark.base.module.Module;

public class DeferredContainer {
  public GolemContainer container;
  public Class<? extends Module> module;
  public String[] blocks;
  
  public DeferredContainer(final GolemContainer lContainer, final Class<? extends Module> lModule, 
      final String[] lBlocks) {
    super();
    this.container = lContainer;
    this.module = lModule;
    this.blocks = lBlocks;
  }
}