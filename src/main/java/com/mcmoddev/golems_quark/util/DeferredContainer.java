package com.mcmoddev.golems_quark.util;

import java.util.function.Predicate;

import com.mcmoddev.golems.util.GolemContainer;

import vazkii.quark.base.module.Module;

public class DeferredContainer {
  public GolemContainer container;
  public Class<? extends Module> module;
  public Predicate<Class<? extends Module>> enabled;
  public String[] blocks;
  
  public DeferredContainer(final GolemContainer lContainer, final Class<? extends Module> lModule, 
      final String[] lBlocks, final Predicate<Class<? extends Module>> lEnabled) {
    this.container = lContainer;
    this.module = lModule;
    this.blocks = lBlocks;
    this.enabled = lEnabled;
  }
}