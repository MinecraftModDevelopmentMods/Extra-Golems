package com.mcmoddev.golems.container.behavior.parameter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

@Immutable
public abstract class BehaviorParameter {
  
  public BehaviorParameter() { }
  
  protected static EffectInstance[] readEffectArray(final ListNBT effectList) {
    // create an EffectInstance array of this size
    EffectInstance[] effects = new EffectInstance[effectList.size()];
    for(int i = 0, l = effectList.size(); i < l; i++) {
      CompoundNBT effect = (CompoundNBT) effectList.get(i);
      // attempt to add byte Id from potion tag
      if(!effect.contains("Id") && effect.contains("Potion")) {
        effect.putByte("Id", (byte) Effect.getId(ForgeRegistries.POTIONS.getValue(new ResourceLocation(effect.getString("Potion")))));
      }
      effects[i] = EffectInstance.read(effect);
    }
    return effects;
  }
  
  protected static ListNBT writeEffectArray(EffectInstance[] effects) {
    ListNBT effectList = new ListNBT();
    for(int i = 0, l = effects.length; i < l; i++) {
      if(effects[i] != null) {
        effectList.add(effects[i].write(new CompoundNBT()));
      }
    }
    return effectList;
  }
  
  protected static Map<String, Integer> readStringIntMap(final CompoundNBT tag) {
    final Map<String, Integer> map = new HashMap<>();
    for(final String key : tag.keySet()) {
      map.put(key, tag.getInt(key));
    }
    return map;
  }
  
  public static enum Target implements IStringSerializable {
    SELF("self"),
    ENEMY("enemy");
    
    private final String name;
    
    private Target(final String nameIn) {
      name = nameIn;
    }
    
    /**
     * @param nameIn the name representation of the SwimMode
     * @return the Target with this name, or SELF as a fallback
     */
    public static Target getByName(final String nameIn) {
      for(final Target swim : Target.values()) {
        if(swim.getString().equals(nameIn)) {
          return swim;
        }
      }
      // default to SELF
      return SELF;
    }

    @Override
    public String getString() {
      return name;
    }
  }
}
