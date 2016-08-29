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

	public void addKey(String key, int defaultValue, int min, int max, String comment)
	{
		String category = this.getCategory();
		int value = config.getInt(key, category, defaultValue, min, max, comment);
		this.keys.put(key, Integer.valueOf(value));
	}

	public void addKey(String key, float defaultValue, float min, float max, String comment)
	{
		String category = this.getCategory();
		float value = config.getFloat(key, category, defaultValue, min, max, comment);
		this.keys.put(key, Float.valueOf(value));
	}

	public void addKey(String key, boolean defaultValue, String comment)
	{
		String category = this.getCategory();
		boolean value = config.getBoolean(key, category, defaultValue, comment);
		this.keys.put(key, Boolean.valueOf(value));
	}

	public int getInt(String key)
	{
		if(this.keys.containsKey(key))
		{
			if(this.keys.get(key) instanceof Integer)
			{
				return ((Integer)this.keys.get(key)).intValue();
			}
			else 
			{
				String error = "Expected an Integer to be mapped to '" + key + "' in GolemConfigSet '" + this.golemName + "' but got " + this.keys.get(key) + " instead";
				throw new IllegalArgumentException(error);
			}
		}
		else throw new IllegalArgumentException("Did not find an integer value matching '" + key + "' in GolemConfigSet '" + this.golemName + "'");
	}

	public float getFloat(String key)
	{
		if(this.keys.containsKey(key))
		{
			if(this.keys.get(key) instanceof Float)
			{
				return ((Float)this.keys.get(key)).floatValue();
			}
			else 
			{
				String error = "Expected a Float to be mapped to '" + key + "' in GolemConfigSet '" + this.golemName + "' but got " + this.keys.get(key) + " instead";
				throw new IllegalArgumentException(error);
			}
		}
		else throw new IllegalArgumentException("Did not find a float value matching '" + key + "' in GolemConfigSet '" + this.golemName + "'");
	}

	public boolean getBoolean(String key)
	{
		if(this.keys.containsKey(key))
		{
			if(this.keys.get(key) instanceof Boolean)
			{
				return ((Boolean)this.keys.get(key)).booleanValue();
			}
			else 
			{
				String error = "Expected a Boolean to be mapped to '" + key + "' in GolemConfigSet '" + this.golemName + "' but got " + this.keys.get(key) + " instead";
				throw new IllegalArgumentException(error);
			}
		}
		else throw new IllegalArgumentException("Did not find a boolean value matching '" + key + "' in GolemConfigSet '" + this.golemName + "'");
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
