package com.mcmoddev.golems.integration;

import java.util.List;
import java.util.function.Function;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.entity.GolemBase;

import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** TheOneProbe integration **/
public final class TOPDescriptionManager extends GolemDescriptionManager implements IProbeInfoEntityProvider {

  public TOPDescriptionManager() {
    super();
    this.showAttack = false;
    this.showSpecial = true;
  }
  
  @Override
  public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level level, 
      Entity entity, IProbeHitEntityData entityData) {
    if (entity instanceof GolemBase) {
      final GolemBase golem = (GolemBase) entity;
      // show attack if advanced mode
      this.showAttack = this.extended = (mode == ProbeMode.EXTENDED);
      final List<Component> list = this.getEntityDescription(golem);
      for (final Component c : list) {
        probeInfo.text(c);
      }
    }
  }

  @Override
  public String getID() { return ExtraGolems.MODID; }


  public static final class GetTheOneProbe implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(final ITheOneProbe input) {
      final IProbeInfoEntityProvider instance = new TOPDescriptionManager();
      input.registerEntityProvider(instance);
      return null;
    }
  }
}
