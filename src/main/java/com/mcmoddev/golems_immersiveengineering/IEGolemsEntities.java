package com.mcmoddev.golems_immersiveengineering;

import java.util.ArrayList;
import java.util.List;

import com.mcmoddev.golems.entity.GenericGolem;
import com.mcmoddev.golems.entity.NetheriteGolem;
import com.mcmoddev.golems.integration.AddonLoader;
import com.mcmoddev.golems.integration.DeferredContainer;
import com.mcmoddev.golems.main.ExtraGolems;
import com.mcmoddev.golems.util.GolemBuilders;
import com.mcmoddev.golems.util.GolemContainer;
import com.mcmoddev.golems.util.GolemDescription;
import com.mcmoddev.golems.util.GolemNames;
import com.mcmoddev.golems.util.GolemRegistrar;
import com.mcmoddev.golems_clib.ClibGolemNames;

import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

public final class IEGolemsEntities {
  public static final String IE = AddonLoader.IE_MODID;
  public static final String MODID = AddonLoader.IE_GOLEMS_MODID;
  
  private static final List<DeferredContainer> deferred = new ArrayList<>();

  private IEGolemsEntities() {}
  
  /**
   * Called just after other Extra Golems entity types are registered.
   **/
  public static void initEntityTypes() {
    ExtraGolems.LOGGER.debug("Extra Golems: Immersive Engineering - initEntityTypes");
    
    final IFormattableTextComponent descResist = new TranslationTextComponent("effect.minecraft.resistance").mergeStyle(TextFormatting.DARK_GRAY);
    
    // ALUMINUM GOLEM
    register(GolemBuilders.aluminumGolem().setDynamicTexture(IE, "metal/storage_aluminum")
        .build(), "storage_aluminum");
    // COAL COKE
    register(GolemBuilders.coalCokeGolem().setDynamicTexture(IE, "stone_decoration/coke")
        .build(), "coke", "cokebrick");
    // CONCRETE GOLEM
    register(new GolemContainer.Builder(IEGolemNames.CONCRETE_GOLEM, NetheriteGolem.class, NetheriteGolem::new)
        .setModId(MODID).setHealth(59.0D).setAttack(5.0D).setSpeed(0.26D).setKnockbackResist(0.6D)
        .addSpecial(NetheriteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes", descResist)
        .immuneToExplosions().setDynamicTexture(IE, "stone_decoration/concrete_tile")
        .build(), "concrete", "concrete_tile");
    // CONSTANTAN GOLEM
    register(GolemBuilders.constantanGolem().setDynamicTexture(IE, "metal/storage_constantan")
        .build(), "storage_constantan");
    // COPPER GOLEM
    register(GolemBuilders.copperGolem().setDynamicTexture(IE, "metal/storage_copper")
        .build(), "storage_copper");
    // COPPER COIL GOLEM
    register(new GolemContainer.Builder(IEGolemNames.COPPERCOIL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(64.0D).setAttack(4.6D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(IE, "metal_decoration/coil_lv_side")
        .build(), "coil_lv");
    // CUSHION GOLEM
    register(new GolemContainer.Builder(IEGolemNames.CUSHION_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(44.0D).setAttack(2.6D).setKnockbackResist(0).setSound(SoundEvents.BLOCK_WOOL_STEP)
        .setDynamicTexture(IE, "cushion")
        .build(), "cushion");
    // ELECTRUM GOLEM
    register(GolemBuilders.electrumGolem().setDynamicTexture(IE, "metal/storage_electrum")
        .build(), "storage_electrum");
    // ELECTRUM COIL GOLEM
    register(new GolemContainer.Builder(IEGolemNames.ELECTRUMCOIL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(71.0D).setAttack(5.6D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(IE, "metal_decoration/coil_mv_side")
        .build(), "coil_mv");
    // HV COIL GOLEM
    register(new GolemContainer.Builder(IEGolemNames.HVCOIL_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(78.0D).setAttack(6.6D).setSound(SoundEvents.BLOCK_METAL_STEP)
        .setDynamicTexture(IE, "metal_decoration/coil_hv_side")
        .build(), "coil_hv");
    // INSULATING GLASS GOLEM
    register(new GolemContainer.Builder(IEGolemNames.INSULATINGGLASS_GOLEM, GenericGolem.class, GenericGolem::new)
        .setModId(MODID).setHealth(20.0D).setAttack(8.9D).setSound(SoundEvents.BLOCK_GLASS_STEP)
        .setDynamicTexture(IE, "stone_decoration/insulating_glass").transparent()
        .build(), "insulating_glass");
    // LEAD GOLEM
    register(GolemBuilders.leadGolem().setDynamicTexture(IE, "metal/storage_lead")
        .build(), "storage_lead");
    // LEADED CONCRETE GOLEM
    register(new GolemContainer.Builder(IEGolemNames.LEADEDCONCRETE_GOLEM, NetheriteGolem.class, NetheriteGolem::new)
        .setModId(MODID).setHealth(67.0D).setAttack(5.2D).setSpeed(0.24D).setKnockbackResist(1.0D)
        .addSpecial(NetheriteGolem.ALLOW_RESIST, true, "Whether this golem reduces damage it takes", descResist)
        .immuneToExplosions().setDynamicTexture(IE, "stone_decoration/concrete_leaded")
        .build(), "concrete_leaded");
    // NICKEL GOLEM
    register(GolemBuilders.nickelGolem().setDynamicTexture(IE, "metal/storage_nickel")
        .build(), "storage_nickel");
    // SILVER GOLEM
    register(GolemBuilders.silverGolem().setDynamicTexture(IE, "metal/storage_silver")
        .build(), "storage_silver");
    // STEEL GOLEM
    register(GolemBuilders.steelGolem().setDynamicTexture(IE, "metal/storage_steel")
        .build(), "storage_steel");
    // URANIUM GOLEM
    register(GolemBuilders.uraniumGolem().setDynamicTexture(IE, "metal/storage_uranium_side")
        .build(), "storage_uranium");
  }
  
  protected static void register(final GolemContainer cont, final String... blockNames) {
    // store the container for updating config later
    deferred.add(new DeferredContainer(cont, IE, blockNames));
    // actually register the container
    GolemRegistrar.registerGolem(cont);
  }
  
  
  /**
   * Called when the InterModEnqueueEvent is sent to the main mod file. 
   * @param event the event, not actually used here
   **/
  public static void interModEnqueueEvent(final InterModEnqueueEvent event) { }

  /**
   * Called when the FMLCommonSetupEvent is sent to the main mod file. 
   * @param event the event, not actually used here
   **/
  public static void setupEvent(FMLCommonSetupEvent event) {
    for(final DeferredContainer d : deferred) {
      d.addBlocks();
    }
  }
}
