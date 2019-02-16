//package com.golems.integration.waila;
//
//import com.golems.entity.GolemBase;
//import com.golems.integration.GolemDescriptionManager;
//import com.golems.integration.ModIds;
//import mcp.mobius.waila.api.IWailaConfigHandler;
//import mcp.mobius.waila.api.IWailaEntityAccessor;
//import mcp.mobius.waila.api.IWailaEntityProvider;
//import mcp.mobius.waila.api.IWailaRegistrar;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayerMP;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.world.World;
//import net.minecraftforge.fml.common.Optional;
//
//import javax.annotation.Nonnull;
//import java.util.List;
//
///**
// * WAILA integration -- using Hwyla:1.8.23-B38_1.12.
// **/
//@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaEntityProvider", modid = ModIds.WAILA)
//public final class WailaExtraGolems extends GolemDescriptionManager implements IWailaEntityProvider {
//
//	public static final String CONFIG_SHOW_ATTACK_DAMAGE = "extragolems.show_attack_damage_tip";
//	public static final String CONFIG_SHOW_SPECIAL_ABILITIES = "extragolems.show_special_abilities_tip";
//	public static final String CONFIG_SHOW_KNOCKBACK_RESIST = "extragolems.show_knockback_resistance_tip";
//	public static final String CONFIG_SHOW_MULTI_TEXTURE = "extragolems.show_multitexture_tip";
//	public static final String CONFIG_SHOW_FIREPROOF = "extragolems.show_fireproof_tip";
//
//	public WailaExtraGolems() {
//		super();
//	}
//
//	@Optional.Method(modid = ModIds.WAILA)
//	public static void callbackRegister(final IWailaRegistrar register) {
//		WailaExtraGolems instance = new WailaExtraGolems();
//
//		register.registerBodyProvider(instance, GolemBase.class);
//
//		final String EXTRA_GOLEMS = "Extra-Golems";
//		register.addConfig(EXTRA_GOLEMS, CONFIG_SHOW_ATTACK_DAMAGE, true);
//		register.addConfig(EXTRA_GOLEMS, CONFIG_SHOW_SPECIAL_ABILITIES, true);
//		register.addConfig(EXTRA_GOLEMS, CONFIG_SHOW_KNOCKBACK_RESIST, false);
//		register.addConfig(EXTRA_GOLEMS, CONFIG_SHOW_MULTI_TEXTURE, true);
//		register.addConfig(EXTRA_GOLEMS, CONFIG_SHOW_FIREPROOF, true);
//	}
//
//	@Override
//	@Optional.Method(modid = ModIds.WAILA)
//	@Nonnull
//	public NBTTagCompound getNBTData(final EntityPlayerMP player, final Entity entity, final NBTTagCompound tag,
//					 final World world) {
//		final NBTTagCompound tag2 = new NBTTagCompound();
//		entity.writeToNBT(tag2);
//		return tag2;
//	}
//
//	@Override
//	@Optional.Method(modid = ModIds.WAILA)
//	@Nonnull
//	public List<String> getWailaBody(final Entity entity, final List<String> tip, final IWailaEntityAccessor accessor,
//					 final IWailaConfigHandler config) {
//		if (entity instanceof GolemBase) {
//			final GolemBase golem = (GolemBase) entity;
//
//			this.showAttack = config.getConfig(CONFIG_SHOW_ATTACK_DAMAGE)
//				&& accessor.getPlayer().isSneaking();
//			this.showMultiTexture = config.getConfig(CONFIG_SHOW_MULTI_TEXTURE);
//			this.showSpecial = config.getConfig(CONFIG_SHOW_SPECIAL_ABILITIES);
//			this.showFireproof = config.getConfig(CONFIG_SHOW_FIREPROOF)
//				&& accessor.getPlayer().isSneaking();
//			this.showKnockbackResist = config.getConfig(CONFIG_SHOW_KNOCKBACK_RESIST);
//
//			tip.addAll(this.getEntityDescription(golem));
//		}
//		return tip;
//	}
//
//	@Override
//	@Optional.Method(modid = ModIds.WAILA)
//	@Nonnull
//	public List<String> getWailaHead(final Entity entity, final List<String> currenttip,
//					 final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
//		return currenttip;
//	}
//
//	@Override
//	@Optional.Method(modid = ModIds.WAILA)
//	public Entity getWailaOverride(final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
//		return accessor.getEntity();
//	}
//
//	@Override
//	@Optional.Method(modid = ModIds.WAILA)
//	@Nonnull
//	public List<String> getWailaTail(final Entity entity, final List<String> currenttip,
//					 final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
//		return currenttip;
//	}
//}
