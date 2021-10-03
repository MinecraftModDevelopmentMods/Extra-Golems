package com.mcmoddev.golems.golem_stats.behavior.parameter;

import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChangeIdBehaviorParameter extends BehaviorParameter {

  private final Map<String, ChangeIdData> idMap;
  private final Optional<Double> chance;
  private final Optional<Boolean> consume;

  public ChangeIdBehaviorParameter(final CompoundNBT tag, String mapName) {
	this(tag, mapName, mapName);
  }

  public ChangeIdBehaviorParameter(final CompoundNBT tag, String mapName, String dataName) {
    super();
	this.chance = tag.contains("chance") ? Optional.of(tag.getDouble("chance")) : Optional.empty();
	this.consume = tag.contains("consume") ? Optional.of(tag.getBoolean("consume")) : Optional.empty();
	this.idMap = ImmutableMap.copyOf(readStringDataMap(tag.getCompound(mapName), dataName));
  }

  /** @return the base chance for this conditional ID change to apply **/
  public double getChance() { return chance.orElse(1.0D); }

  /**
   * @return the input-specific chance for this conditional ID change to apply
   **/
  public double getChance(final String input) {
	return idMap.containsKey(input) ? idMap.get(input).getChance() : getChance();
  }

  /** @return the base value of whether to consume items **/
  public boolean consume() { return consume.orElse(false); }

  /** @return the input-specific value of whether to consume items **/
  public boolean consume(final String input) {
	return idMap.containsKey(input) ? idMap.get(input).consume() : consume();
  }

  /**
   * Accepts an object
   * @param input the string key
   * @param fallback the value to return if the string key is absent
   * @return the texture ID contained in the map for the given input
   */
  public String getId(final String input, final String fallback) {
    return idMap.containsKey(input) ? idMap.get(input).getId()	: fallback;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("ChangeId:");
	chance.ifPresent(c -> b.append(" chance[").append(c).append("]"));
	consume.ifPresent(c -> b.append(" consume[").append(c).append("]"));
	b.append(" map[").append(idMap.toString()).append("]");
	return b.toString();
  }


  protected Map<String, ChangeIdData> readStringDataMap(final CompoundNBT tag, final String dataKey) {
	final Map<String, ChangeIdData> map = new HashMap<>();
	for(final String key : tag.keySet()) {
	  if(tag.contains(key, Constants.NBT.TAG_COMPOUND)) {
		map.put(key, new ChangeIdData(tag.getCompound(key), dataKey));
	  } else {
		map.put(key, new ChangeIdData(getChance(), consume(), tag.getString(key)));
	  }
	}
	return map;
  }

  /**
   * Holds some information about the ID change
   * (chance, whether to consume, and the ID string)
   */
  private static class ChangeIdData extends BehaviorParameter {
	private final double chance;
	private final boolean consume;
	private final String id;

	private ChangeIdData(final CompoundNBT tag, String idName) {
	  super();
	  this.chance = tag.contains("chance") ? tag.getDouble("chance") : 1.0D;
	  this.consume = tag.getBoolean("consume");
	  if(tag.contains(idName, Constants.NBT.TAG_ANY_NUMERIC)) {
		this.id = String.valueOf(tag.getInt(idName));
	  } else {
		this.id = tag.getString(idName);
	  }
	}

	private ChangeIdData(final double chanceIn, final boolean consumeIn, final String idIn) {
	  chance = chanceIn;
	  consume = consumeIn;
	  id = idIn;
	}

	/** @return the chance for this conditional id to apply **/
	public double getChance() { return chance; }

	/** @return true if the item should be consumed **/
	public boolean consume() { return consume; }

	public String getId() { return id; }

	@Override
	public String toString() {
	  return "ChangeIdData: chance[" + chance + "] consume[" + consume + "] id[" + id + "]";
	}
  }

}
