package com.golems.integration.theoneprobe;

import com.golems.entity.GolemBase;
import com.golems.integration.GolemDescriptionManager;
import com.golems.integration.ModIds;
import com.golems.main.ExtraGolems;
import com.google.common.base.Function;
import mcjty.theoneprobe.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

import java.util.List;

/**
 * TheOneProbe integration -- using theoneprobe-1.10-1.1.0.
 **/
@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoEntityProvider", modid = ModIds.TOP)
public final class TOPExtraGolems extends GolemDescriptionManager implements IProbeInfoEntityProvider {

  public TOPExtraGolems() {
    super();
    this.showMultiTexture = true;
    this.showSpecial = true;
  }

  @Override
  @Optional.Method(modid = ModIds.TOP)
  public void addProbeEntityInfo(final ProbeMode mode, final IProbeInfo iprobeInfo, final EntityPlayer player,
      final World world, final Entity entity, final IProbeHitEntityData data) {
    if (entity instanceof GolemBase) {
      final GolemBase golem = (GolemBase) entity;
      // show attack if advanced mode
      this.showFireproof = this.showAttack = (mode == ProbeMode.EXTENDED);

      final List<String> list = this.getEntityDescription(golem);
      for (final String s : list) {
        iprobeInfo.text(s);
      }
    }
  }

  @Override
  @Optional.Method(modid = ModIds.TOP)
  public String getID() {
    return ExtraGolems.MODID;
  }

  @Optional.Interface(iface = "mcjty.theoneprobe.api.ITheOneProbe", modid = ModIds.TOP)
  public static final class GetTheOneProbe implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(final ITheOneProbe input) {
      final IProbeInfoEntityProvider instance = new TOPExtraGolems();
      input.registerEntityProvider(instance);
      return null;
    }
  }
}
