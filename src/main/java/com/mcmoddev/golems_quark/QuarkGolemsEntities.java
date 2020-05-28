package com.mcmoddev.golems_quark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.config.GolemContainer;
import com.mcmoddev.golems.util.config.GolemRegistrar;
import com.mcmoddev.golems_quark.util.DeferredContainer;
import com.mcmoddev.golems_quark.util.QuarkGolemNames;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.building.module.DuskboundBlocksModule;
import vazkii.quark.building.module.MidoriModule;
import vazkii.quark.world.module.NewStoneTypesModule;

public final class QuarkGolemsEntities {
  
  public static final String MODID = "golems_quark";
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();
  
  private QuarkGolemsEntities() {}
  
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Quark Golem Entity Types");
    // DEBUG
    // print out which modules are enabled
    final boolean duskbound = ModuleLoader.INSTANCE.isModuleEnabled(DuskboundBlocksModule.class);
    ExtraGolems.LOGGER.debug("Duskbound Module loaded: " + duskbound);
    final boolean midori = ModuleLoader.INSTANCE.isModuleEnabled(MidoriModule.class);
    ExtraGolems.LOGGER.debug("Midori Module loaded: " + midori);
    
    // MARBLE GOLEM
    softRegister(NewStoneTypesModule.class, new GolemContainer.Builder(QuarkGolemNames.MARBLE_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).setKnockback(0.6D).basicTexture(),
        "polished_marble", "marble_pillar", "chiseled_marble_bricks", "marble_pavement");
    // MIDORI GOLEM
    softRegister(MidoriModule.class, new GolemContainer.Builder(QuarkGolemNames.MIDORI_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(22.0D).setAttack(4.0D).setSpeed(0.28D).setKnockback(0.6D).basicTexture(),
        "midori_block", "midori_pillar");
    
    
  }
  
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) {
    for(final DeferredContainer d : QuarkGolemsEntities.deferred) {
      final boolean enabled = ModuleLoader.INSTANCE.isModuleEnabled(d.module);
      d.container.setEnabled(enabled);
      // try to add the blocks
      for(final String s : d.blocks) {
        // see if the block exists
        final Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation("quark", s));
        // add that block as a building block for the golem
        if(block != null) {
          d.container.addBlocks(block);
        }
      }
    }
  }
  
  public static void softRegister(final Class<? extends Module> module, final GolemContainer.Builder builder, final String... blockNames) {
    // DEBUG
    ExtraGolems.LOGGER.debug("registering builder for module " + module.getName() + ", for blocks " + Arrays.deepToString(blockNames));
    // store the container for updating config later
    final GolemContainer cont = builder.build();
    deferred.add(new DeferredContainer(cont, module, blockNames));
    // actually register the container
    GolemRegistrar.registerGolem(cont);
  }  
}
