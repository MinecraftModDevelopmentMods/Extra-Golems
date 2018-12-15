package com.golems.util;

import com.golems.main.ExtraGolems;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraftforge.common.config.Configuration;

/**
 * This class loads several values from the config for a specific golem, including spawn
 * permissions, health, and attack. It can load any number of fields as a boolean, int, or float.
 **/
public class GolemConfigSet {

	private static final String GOLEM_PERMS = "Allow Golem";
	private static final String GOLEM_HEALTH = "Golem Health";
	private static final String GOLEM_ATTACK = "Golem Attack";

	private static final String DEFAULTING_TO = "' - defaulting to ";
	private static final String IN_GOLEM_CONFIG_SET = "' in GolemConfigSet '";
	private Configuration config;
	private String golemName;
	private String category;

	private final TObjectIntMap<String> mapInt;
	private final TObjectFloatMap<String> mapFloat;
	private final TObjectByteMap<String> mapBoolean;

	private boolean canSpawn;
	private double maxHealth;
	private float baseAttack;

	private final boolean defSpawn;
	private final double defHealth;
	private final float defAttack;

	// default objects -- methods will return these if the map has no entry
	private static final int DEF_INT = 0;
	private static final float DEF_FLOAT = 0.0F;
	private static final boolean DEF_BOOL = false;
	private static final byte TRUE = 1;
	private static final byte FALSE = 0;

	public GolemConfigSet(final Configuration configFile, final String name, final boolean spawn, final double health,
			final float attack) {
		this.mapInt = new TObjectIntHashMap<>(1);
		this.mapFloat = new TObjectFloatHashMap<>(1);
		this.mapBoolean = new TObjectByteHashMap<>(1);
		this.config = configFile;
		this.golemName = name;
		this.category = this.golemName.toLowerCase().replace(' ', '_');
		this.defSpawn = spawn;
		this.defHealth = health;
		this.defAttack = attack;
		this.loadFromConfig();
	}

	public GolemConfigSet(final Configuration configFile, final String name, final double health, final float attack) {
		this(configFile, name, true, health, attack);
	}

	/** Load some values like spawn permission, health, and attack right away. **/
	public GolemConfigSet loadFromConfig() {
		this.canSpawn = config.getBoolean(GOLEM_PERMS, this.category, this.defSpawn,
				"Whether the " + golemName + " can be built");
		this.maxHealth = config.getFloat(GOLEM_HEALTH, this.category, (float) this.defHealth, 0.0F,
				999.0F, "Max health for this golem");
		this.baseAttack = config.getFloat(GOLEM_ATTACK, this.category, this.defAttack, 0.0F,
				300.0F, "Base attack damage dealt by this golem");
		return this;
	}

	public GolemConfigSet addKey(final String key, final int defaultValue, final int min, final int max, final String comment) {
		final int value = this.config.getInt(key, this.category, defaultValue, min, max, comment);
		this.mapInt.put(key, value);
		return this;
	}

	public GolemConfigSet addKey(final String key, final float defaultValue, final float min, final float max, final String comment) {
		final float value = config.getFloat(key, this.category, defaultValue, min, max, comment);
		this.mapFloat.put(key, value);
		return this;
	}

	public GolemConfigSet addKey(final String key, final boolean defaultValue, final String comment) {
		final boolean value = config.getBoolean(key, this.category, defaultValue, comment);
		this.mapBoolean.put(key, value ? TRUE : FALSE);
		return this;
	}

	public int getInt(final String key) {
		if (this.mapInt.containsKey(key)) {
			return this.mapInt.get(key);
		} else {
			final String error = "Did not find an int value matching '" + key + IN_GOLEM_CONFIG_SET
					+ this.golemName + DEFAULTING_TO + DEF_INT;
			ExtraGolems.LOGGER.error(error);
			this.mapInt.put(key, DEF_INT);
			return DEF_INT;
		}
	}

	public float getFloat(final String key) {
		if (this.mapFloat.containsKey(key)) {
			return this.mapFloat.get(key);
		} else {
			final String error = "Did not find a float value matching '" + key + IN_GOLEM_CONFIG_SET
					+ this.golemName + DEFAULTING_TO + DEF_FLOAT;
			ExtraGolems.LOGGER.error(error);
			this.mapFloat.put(key, DEF_FLOAT);
			return DEF_FLOAT;
		}
	}

	public boolean getBoolean(final String key) {
		if (this.mapBoolean.containsKey(key)) {
			return this.mapBoolean.get(key) == TRUE;
		} else {
			final String error = "Did not find a boolean value matching '" + key + IN_GOLEM_CONFIG_SET
					+ this.golemName + DEFAULTING_TO + DEF_BOOL;
			ExtraGolems.LOGGER.error(error);
			this.mapFloat.put(key, DEF_BOOL ? TRUE : FALSE);
			return DEF_BOOL;
		}
	}

	public boolean canSpawn() {
		return this.canSpawn;
	}

	public double getMaxHealth() {
		return this.maxHealth;
	}

	public float getBaseAttack() {
		return this.baseAttack;
	}

	public Configuration getConfig() {
		return this.config;
	}
}
