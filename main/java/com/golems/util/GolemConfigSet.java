package com.golems.util;

import java.util.HashMap;
import java.util.Map;

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

	private Map<String, Object> keys;
	private boolean canSpawn;
	private double maxHealth;
	private float baseAttack;
	
	// default objects -- methods will return these if the map has no entry
	private static final Boolean DEF_BOOL = Boolean.valueOf(false);
	private static final Integer DEF_INT = Integer.valueOf(0);
	private static final Float DEF_FLOAT = Float.valueOf(0.0F);

	public GolemConfigSet(Configuration configFile, String name, boolean spawn, double health, float attack)
	{
		this.keys = new HashMap(4);
		this.config = configFile;
		this.golemName = name;
		this.setCanSpawn(spawn);
		this.setMaxHealth(health);
		this.setBaseAttack(attack);
		this.loadFromConfig();
	}

	public GolemConfigSet(Configuration configFile, String name, double health, float attack)
	{
		this(configFile, name, true, health, attack);
	}

	/** Load some values like spawn permission, health, and attack right away **/
	public GolemConfigSet loadFromConfig()
	{
		String category = this.getCategory();
		boolean spawn = config.getBoolean("Allow Golem", category, true, 
				"Whether the " + golemName + " can be built");
		double health = config.getFloat("Golem Health", category, (float)this.getMaxHealth(), 0.0F, 999.0F, 
				"Max health for this golem");
		float attack = config.getFloat("Golem Attack", category, this.getBaseAttack(), 0.0F, 300.0F, 
				"Base attack damage dealt by this golem");
		this.setCanSpawn(spawn);
		this.setMaxHealth(health);
		this.setBaseAttack(attack);
		return this;
	}

	public int addKey(String key, int defaultValue, int min, int max, String comment)
	{
		String category = this.getCategory();
		int value = config.getInt(key, category, defaultValue, min, max, comment);
		this.keys.put(key, Integer.valueOf(value));
		return value;
	}

	public float addKey(String key, float defaultValue, float min, float max, String comment)
	{
		String category = this.getCategory();
		float value = config.getFloat(key, category, defaultValue, min, max, comment);
		this.keys.put(key, Float.valueOf(value));
		return value;
	}

	public boolean addKey(String key, boolean defaultValue, String comment)
	{
		String category = this.getCategory();
		boolean value = config.getBoolean(key, category, defaultValue, comment);
		this.keys.put(key, Boolean.valueOf(value));
		return value;
	}

	public int getInt(String key)
	{
		if(!this.keys.containsKey(key))
		{
			String error = "Did not find an integer value matching '" + key + "' in GolemConfigSet '" + this.golemName + "' - defaulting to " + this.DEF_INT.toString();
			System.err.print(error);
			this.keys.put(key, DEF_INT);
			return this.DEF_INT.intValue();
		}
		Object value = this.keys.get(key);
		if(value instanceof Integer)
		{
			return ((Integer)value).intValue();
		}
		else System.err.print("Expected an Integer to be mapped to '" + key + "' in GolemConfigSet '" + this.golemName + "' but got " + value.toString() + " instead");
		return this.DEF_INT.intValue();
	}

	public float getFloat(String key)
	{
		if(!this.keys.containsKey(key))
		{
			String error = "Did not find a float value matching '" + key + "' in GolemConfigSet '" + this.golemName + "' - defaulting to " + this.DEF_FLOAT.toString();
			System.err.print(error);
			this.keys.put(key, DEF_FLOAT);
			return this.DEF_FLOAT.floatValue();
		}
		Object value = this.keys.get(key);
		if(value instanceof Float)
		{
			return ((Float)value).floatValue();
		}
		else System.err.print("Expected a Float to be mapped to '" + key + "' in GolemConfigSet '" + this.golemName + "' but got " + value.toString() + " instead");
		return this.DEF_FLOAT.floatValue();
	}

	public boolean getBoolean(String key)
	{
		if(!this.keys.containsKey(key))
		{
			String error = "Did not find a boolean value matching '" + key + "' in GolemConfigSet '" + this.golemName + "' - defaulting to " + this.DEF_BOOL.toString();
			System.err.print(error);
			this.keys.put(key, DEF_BOOL);
			return this.DEF_BOOL.booleanValue();
		}
		Object value = this.keys.get(key);
		if(value instanceof Boolean)
		{
			return ((Boolean)value).booleanValue();
		}
		else System.err.print("Expected a Boolean to be mapped to '" + key + "' in GolemConfigSet '" + this.golemName + "' but got " + value.toString() + " instead");
		return this.DEF_BOOL.booleanValue();
	}

	public void setCanSpawn(boolean toSet)
	{
		this.canSpawn = toSet;
	}

	public boolean canSpawn()
	{
		return this.canSpawn;
	}

	public void setMaxHealth(double toSet)
	{
		this.maxHealth = toSet;
	}

	public double getMaxHealth()
	{
		return this.maxHealth;
	}

	public void setBaseAttack(float toSet)
	{
		this.baseAttack = toSet;
	}

	public float getBaseAttack()
	{
		return this.baseAttack;
	}

	public String getCategory()
	{
		return this.golemName.toLowerCase().replace(' ', '_');
	}

	public Configuration getConfig()
	{
		return this.config;
	}
}
