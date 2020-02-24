package com.mcmoddev.golems.util.config.special;

import net.minecraftforge.common.ForgeConfigSpec;

public class GolemSpecialSection {

  public final String name;
  public ForgeConfigSpec.ConfigValue<?> value;

  /**
   * Should be pushed before all constructors are called and popped after all
   * construtctors are called
   *
   * @param specialContainer
   * @param builder
   */
  public GolemSpecialSection(GolemSpecialContainer specialContainer, ForgeConfigSpec.Builder builder) {
    this.name = specialContainer.name;

    value = builder.comment(specialContainer.comment).define(name, specialContainer.value);
  }
}
