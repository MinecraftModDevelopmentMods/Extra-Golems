package com.mcmoddev.golems.integration;

import java.util.List;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.golem_stats.behavior.GolemBehaviors;
import com.mcmoddev.golems.entity.GolemBase;
import mcp.mobius.waila.api.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

@WailaPlugin(ExtraGolems.MODID)
public final class HwylaDescriptionManager extends GolemDescriptionManager implements IEntityComponentProvider, IWailaPlugin, IServerDataProvider<Entity> {

  public static final HwylaDescriptionManager INSTANCE = new HwylaDescriptionManager();

  public HwylaDescriptionManager() {
	super();
  }

  @Override
  public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
	// settings: hold shift to show attack damage
	this.showAttack = isShiftDown();
	if (accessor.getEntity() instanceof GolemBase) {
	  final GolemBase golem = (GolemBase) accessor.getEntity();
	  golem.setMaterial(new ResourceLocation(accessor.getServerData().getString(GolemBase.KEY_MATERIAL)));
	  golem.loadFuel(accessor.getServerData());
	  tooltip.addAll(this.getEntityDescription(golem));
	}
  }

  @Override
  public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, Entity entity) {
	if(entity instanceof GolemBase) {
	  GolemBase golem = (GolemBase) entity;
	  data.putString(GolemBase.KEY_MATERIAL, golem.getMaterial().toString());
	  if(golem.getContainer().hasBehavior(GolemBehaviors.USE_FUEL)) {
		data.putInt(GolemBase.KEY_FUEL, golem.getFuel());
	  }
	  if(golem.getContainer().hasBehavior(GolemBehaviors.SHOOT_ARROWS)) {
		data.putInt("Arrows", golem.getArrowsInInventory());
	  }
	}
  }

  @Override
  public void register(IRegistrar register) {
	register.registerComponentProvider(INSTANCE, TooltipPosition.BODY, GolemBase.class);
	register.registerEntityDataProvider(INSTANCE, GolemBase.class);
  }
}
