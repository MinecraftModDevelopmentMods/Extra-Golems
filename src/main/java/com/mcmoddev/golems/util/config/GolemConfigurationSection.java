package com.mcmoddev.golems.util.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Adapted from BetterAnimalsPlus by its_meow. Used with permission.
 */
public class GolemConfigurationSection {

  public final String entityName;
  public ForgeConfigSpec.DoubleValue health;
  public ForgeConfigSpec.DoubleValue attack;

  public ForgeConfigSpec.BooleanValue enabled;

  protected GolemConfigurationSection(GolemContainer container, ForgeConfigSpec.Builder builder) {
    this.entityName = container.getName();
    loadConfig(builder, container);
  }

  public void loadConfig(ForgeConfigSpec.Builder builder, GolemContainer container) {
    enabled = builder.comment("Disables in-world building of the golem.").worldRestart().define("enabled", true);
    health = builder.worldRestart().defineInRange("health", container.getHealth(), 1, 999);
    attack = builder.worldRestart().defineInRange("attack", container.getAttack(), 1, 999);
  }
}
