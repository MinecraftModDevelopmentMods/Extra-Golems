package com.golems.util;

import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.common.config.Configuration;

/**
 * This class loads several values from the config for a specific golem,
 * including spawn permissions, health, and attack.
 * It can load any number of fields as a boolean, int, or float.
 **/
public class GolemConfigSet
{	
	private Configuration config;
	private String golemName;
	private String category;

	private TObjectIntMap<String> mapInt;
	private TObjectFloatMap<String> mapFloat;
	private TObjectByteMap<String> mapByte; // used as boolean map
	
	private boolean canSpawn;
	private double maxHealth;
	private float baseAttack;
	
	private final boolean DEF_SPAWN;
	private final double DEF_HEALTH;
	private final float DEF_ATTACK;
	
	// default objects -- methods will return these if the map has no entry
	private static final int DEF_INT = 0;
	private static final float DEF_FLOAT = 0.0F;
	private static final boolean DEF_BOOL = false;

	public GolemConfigSet(Configuration configFile, String name, boolean spawn, double health, float attack)
	{
		this.mapInt = new TObjectIntHashMap(1);
		this.mapFloat = new TObjectFloatHashMap(1);
		this.mapByte = new TObjectByteHashMap(1);
		this.config = configFile;
		this.golemName = name;
		this.category = this.golemName.toLowerCase().replace(' ', '_');
		this.DEF_SPAWN = spawn;
		this.DEF_HEALTH = health;
		this.DEF_ATTACK = attack;
		this.loadFromConfig();
	}

	public GolemConfigSet(Configuration configFile, String name, double health, float attack)
	{
		this(configFile, name, true, health, attack);
	}

	/** Load some values like spawn permission, health, and attack right away **/
	public GolemConfigSet loadFromConfig()
	{
		this.canSpawn = config.getBoolean("Allow Golem", this.category, this.DEF_SPAWN, 
				"Whether the " + golemName + " can be built");
		this.maxHealth = config.getFloat("Golem Health", this.category, (float)this.DEF_HEALTH, 0.0F, 999.0F, 
				"Max health for this golem");
		this.baseAttack = config.getFloat("Golem Attack", this.category, this.DEF_ATTACK, 0.0F, 300.0F, 
				"Base attack damage dealt by this golem");
		return this;
	}

	public int addKey(String key, int defaultValue, int min, int max, String comment)
	{
		int value = config.getInt(key, this.category, defaultValue, min, max, comment);
		this.mapInt.put(key, value);
		return value;
	}

	public float addKey(String key, float defaultValue, float min, float max, String comment)
	{
		float value = config.getFloat(key, this.category, defaultValue, min, max, comment);
		this.mapFloat.put(key, value);
		return value;
	}

	public boolean addKey(String key, boolean defaultValue, String comment)
	{
		boolean value = config.getBoolean(key, this.category, defaultValue, comment);
		this.mapByte.put(key, value ? (byte)1 : (byte)0);
		return value;
	}

	public int getInt(String key)
	{
		if(this.mapInt.containsKey(key))
		{
			return this.mapInt.get(key);
		}
		else
		{
			String error = "Did not find an int value matching '" + key + "' in GolemConfigSet '" + this.golemName + "' - defaulting to " + this.DEF_INT;
			System.out.println(error);
			this.mapInt.put(key, DEF_INT);
			return DEF_INT;
		}
	}

	public float getFloat(String key)
	{
		if(this.mapFloat.containsKey(key))
		{
			return this.mapFloat.get(key);
		}
		else
		{
			String error = "Did not find a float value matching '" + key + "' in GolemConfigSet '" + this.golemName + "' - defaulting to " + this.DEF_FLOAT;
			System.out.println(error);
			this.mapFloat.put(key, DEF_FLOAT);
			return DEF_FLOAT;
		}
	}

	public boolean getBoolean(String key)
	{
		if(this.mapByte.containsKey(key))
		{
			return this.mapByte.get(key) == (byte)1;
		}
		else
		{
			String error = "Did not find a boolean value matching '" + key + "' in GolemConfigSet '" + this.golemName + "' - defaulting to " + this.DEF_BOOL;
			System.out.println(error);
			this.mapByte.put(key, DEF_BOOL ? (byte)1 : (byte)0);
			return DEF_BOOL;
		}
	}
	
	public boolean canSpawn()
	{
		return this.canSpawn;
	}

	public double getMaxHealth()
	{
		return this.maxHealth;
	}

	public float getBaseAttack()
	{
		return this.baseAttack;
	}

	public Configuration getConfig()
	{
		return this.config;
	}
}
