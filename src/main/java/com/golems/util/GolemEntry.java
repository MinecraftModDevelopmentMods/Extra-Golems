package com.golems.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.golems.entity.GolemBase;
import com.golems.entity.GolemMultiTextured;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class GolemEntry implements INBTSerializable<NBTTagCompound> {
		
	private String RL_BLOCK;
	private String GOLEM_NAME_UNLOCAL;
	private String GOLEM_NAME;

	private boolean MULTI_TEXTURE;
	private boolean FIREPROOF;
	private int HEALTH;
	private float ATTACK;
		
	/* NBT tags for (de)serializing */
	private static final String TAG_BLOCK = "Block";
	private static final String TAG_NAME = "Name";
	private static final String TAG_MULTITEXTURE = "isMultiTexture";
	private static final String TAG_FIREPROOF = "isFireproof";
	private static final String TAG_HEALTH = "Health";
	private static final String TAG_ATTACK = "Attack";
	private static final String TAG_DESC = "Specials";
	
	// Comparator that sorts Golem Entries by attack power
	private static final Comparator<GolemEntry> SORTER = new Comparator<GolemEntry>() {
		@Override
		public int compare(GolemEntry arg0, GolemEntry arg1) {
			float attack0 = arg0.getAttack();
			float attack1 = arg1.getAttack();
			return attack0 < attack1 ? -1 : (attack0 - attack1 < 0.01F ? 0 : 1);
		}
	};
	
	public GolemEntry(Block golemBlock, String golemUnlocalName, int health, float attack, boolean isMultiTextureIn, boolean isFireproofIn) {
		Block block = golemBlock != null ? golemBlock : Blocks.AIR;
		this.RL_BLOCK = block.getRegistryName().toString();
		this.GOLEM_NAME = golemUnlocalName;
		this.GOLEM_NAME_UNLOCAL = "entity." + golemUnlocalName + ".name";
		this.HEALTH = health;
		this.ATTACK = attack;
		this.MULTI_TEXTURE = isMultiTextureIn;
		this.FIREPROOF = isFireproofIn;
	}
	
	public GolemEntry(NBTTagCompound nbt) {
		this.deserializeNBT(nbt);
	}
	
	/** Using the result of Entity#getEntityName, returns the Golem Class or null if not found **/
	@Nullable
	public Class<? extends GolemBase> getGolemClass() {
		Class<? extends Entity> c = EntityList.getClassFromName(GOLEM_NAME);
		if(GolemBase.class.isAssignableFrom(c)) {
			return (Class<? extends GolemBase>) c;
		} else return null;
	}
	
	/** @return the result of Entity#getEntityName, formatted as it appears in the .lang files **/
	public String getGolemNameUnlocal() {
		return GOLEM_NAME_UNLOCAL;
	}
	
	/** @return the String form of the ResourceLocation of this golem's block **/
	public String getBlockRl() {
		return this.RL_BLOCK;
	}
	
	/** @return the golem's health **/
	public int getHealth() {
		return this.HEALTH;
	}
	
	/** @return the golem's attack power **/
	public float getAttack() {
		return this.ATTACK;
	}
	
	/** @return if the golem is multi-textured **/
	public boolean isMultiTexture() {
		return this.MULTI_TEXTURE;
	}
	
	/** @return if the golem is fireproof **/
	public boolean isFireproof() {
		return this.FIREPROOF;
	}
	
	/** @return a String containing both the block name and golem name, for future use **/
	public String getSearchableString() {
		return RL_BLOCK + " " + GOLEM_NAME.toLowerCase();
	}
	
	@Override
	public String toString() {
		return "\nBlock:  " + this.RL_BLOCK
				+ "\nGolem Name: " + this.getGolemNameUnlocal()
				+ " (HEALTH=" + this.HEALTH + ", ATTACK=" + this.ATTACK + ")";
	}

	@Override
	public NBTTagCompound serializeNBT() {
		final NBTTagCompound nbt = new NBTTagCompound();
		// add basic values with appropriate tags
		nbt.setString(TAG_BLOCK, RL_BLOCK);
		nbt.setString(TAG_NAME, GOLEM_NAME);
		nbt.setBoolean(TAG_MULTITEXTURE, MULTI_TEXTURE);
		nbt.setBoolean(TAG_FIREPROOF, FIREPROOF);
		nbt.setInteger(TAG_HEALTH, HEALTH);
		nbt.setFloat(TAG_ATTACK, ATTACK);		
		return nbt;
	}

	@Override
	public void deserializeNBT(final NBTTagCompound nbt) {
		this.RL_BLOCK = nbt.getString(TAG_BLOCK);
		this.GOLEM_NAME = nbt.getString(TAG_NAME);
		this.GOLEM_NAME_UNLOCAL = "entity." + this.GOLEM_NAME + ".name";
		this.MULTI_TEXTURE = nbt.getBoolean(TAG_MULTITEXTURE);
		this.FIREPROOF = nbt.getBoolean(TAG_FIREPROOF);
		this.HEALTH = nbt.getInteger(TAG_HEALTH);
		this.ATTACK = nbt.getFloat(TAG_ATTACK);		
	}
	
	/**
	 * Populates the given list with GolemEntry objects for each golem that is registered.
	 * @param world
	 * @param entries
	 */
	public static final List<GolemEntry> getGolemEntries(final World world) {
		final List<GolemEntry> entries = new ArrayList();
		// CREATE ALL GOLEM ENTRIES
		// make a map of golems and their respective blocks
		final List<GolemBase> golemList = getDummyGolemList(world);
		// use the list to create new GolemEntry objects for the list
		for(GolemBase golem : golemList) {
			// get all necessary constructor ingredients and add GolemEntry to the list
			Block block = GolemLookup.getBuildingBlock(golem.getClass());
			String unlocalGolem = EntityList.getEntityString(golem);
			int health = (int)golem.getMaxHealth();
			float attack = golem.getBaseAttackDamage();
			boolean isMultiTexture = (golem instanceof GolemMultiTextured || golem.doesInteractChangeTexture());
			boolean isFireproof = golem.isImmuneToFire();
			System.out.println("Making GolemEntry for " + unlocalGolem
					+ " (HEALTH=" + health + ", ATTACK=" + attack + ")");
			entries.add(new GolemEntry(block, unlocalGolem, health, attack, isMultiTexture, isFireproof));
		}
		// sort by attack power
		Collections.sort(entries, SORTER);
		
		return entries;
	}
	
	public static final List<GolemBase> getDummyGolemList(final World world) {
		final List<GolemBase> list = new LinkedList();
		// for each entity, find out if it's a golem and add it to the list
		final Set<ResourceLocation> set = EntityList.getEntityNameList();
		for(EntityEntry entry : ForgeRegistries.ENTITIES) {
			if(GolemBase.class.isAssignableFrom(entry.getEntityClass())) {
				list.add((GolemBase)entry.newInstance(world));
			}
		}
		
		return list;
	}
}
