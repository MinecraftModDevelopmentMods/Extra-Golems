package com.golems.integration.waila;

import java.util.List;

import com.golems.entity.EntityBedrockGolem;
import com.golems.entity.EntityBookshelfGolem;
import com.golems.entity.EntityCoalGolem;
import com.golems.entity.EntityCraftingGolem;
import com.golems.entity.EntityEndstoneGolem;
import com.golems.entity.EntityIceGolem;
import com.golems.entity.EntityLapisGolem;
import com.golems.entity.EntityLeafGolem;
import com.golems.entity.EntityMagmaGolem;
import com.golems.entity.EntityMelonGolem;
import com.golems.entity.EntityMushroomGolem;
import com.golems.entity.EntityNetherBrickGolem;
import com.golems.entity.EntityNetherWartGolem;
import com.golems.entity.EntityRedstoneGolem;
import com.golems.entity.EntitySlimeGolem;
import com.golems.entity.EntitySpongeGolem;
import com.golems.entity.EntityTNTGolem;
import com.golems.entity.GolemBase;
import com.golems.entity.GolemLightProvider;
import com.golems.entity.GolemMultiTextured;
import com.golems.integration.GolemDescriptionManager;
import com.golems.integration.ModIds;
import com.golems.main.Config;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

/**
 * WAILA integration -- using Waila-1.7.0-B3_1.9.4
 **/
@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaEntityProvider", modid = ModIds.WAILA)
public class WailaExtraGolems extends GolemDescriptionManager implements IWailaEntityProvider
{
	public static final String configShowAttackDamage = "extragolems.show_attack_damage_tip";
	public static final String configShowSpecialAbilities = "extragolems.show_special_abilities_tip";
	public static final String configShowKnockbackResist = "extragolems.show_knockback_resistance_tip";
	public static final String configShowMultiTexture = "extragolems.show_multitexture_tip";
	public static final String configShowFireproof = "extragolems.show_fireproof_tip";
	
	public WailaExtraGolems()
	{
		super();
	}

	@Optional.Method(modid = ModIds.WAILA)
	public static void callbackRegister(IWailaRegistrar register) 
	{
		WailaExtraGolems instance = new WailaExtraGolems();

		register.registerBodyProvider(instance, GolemBase.class);

		register.addConfig("Extra-Golems", configShowAttackDamage, true);
		register.addConfig("Extra-Golems", configShowSpecialAbilities, true);
		register.addConfig("Extra-Golems", configShowKnockbackResist, false);
		register.addConfig("Extra-Golems", configShowMultiTexture, true);
		register.addConfig("Extra-Golems", configShowFireproof, true);
	}

	@Override
	@Optional.Method(modid = ModIds.WAILA)
	public NBTTagCompound getNBTData(EntityPlayerMP player, Entity entity, NBTTagCompound tag, World world) 
	{
		NBTTagCompound tag2 = new NBTTagCompound();
		entity.writeToNBT(tag2);
		return tag2;
	}

	@Override
	@Optional.Method(modid = ModIds.WAILA)
	public List<String> getWailaBody(Entity entity, List<String> tip, IWailaEntityAccessor accessor, IWailaConfigHandler config) 
	{
		if(entity instanceof GolemBase)
		{
			GolemBase golem = (GolemBase)entity;
			
			this.showAttack = config.getConfig(configShowAttackDamage);
			this.showMultiTexture = config.getConfig(configShowMultiTexture);
			this.showSpecial = config.getConfig(configShowSpecialAbilities);
			this.showFireproof = config.getConfig(configShowFireproof);
			this.showKnockbackResist = config.getConfig(configShowKnockbackResist);
			
			tip.addAll(this.getEntityDescription(golem));
		}
		return tip;
	}

	@Override
	@Optional.Method(modid = ModIds.WAILA)
	public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,	IWailaConfigHandler config) 
	{
		return currenttip;
	}

	@Override
	@Optional.Method(modid = ModIds.WAILA)
	public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) 
	{
		return accessor.getEntity();
	}

	@Override
	@Optional.Method(modid = ModIds.WAILA)
	public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,	IWailaConfigHandler config) 
	{
		return currenttip;
	}
}
