package com.mcmoddev.golems.container.behavior.parameter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.Map;

@Immutable
public abstract class BehaviorParameter {

	public static final String S_TRANSLATION_KEY = "translation_key";

	public BehaviorParameter() {
	}

	@Nullable
	public static MobEffectInstance readEffect(final CompoundTag effect) {
		// attempt to add byte Id from potion tag
		if (!effect.contains("Id") && effect.contains("Potion")) {
			effect.putByte("Id", (byte) MobEffect.getId(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effect.getString("Potion")))));
		}
		return MobEffectInstance.load(effect);
	}


	protected static MobEffectInstance[] readEffectArray(final ListTag effectList) {
		// create an EffectInstance array of this size
		MobEffectInstance[] effects = new MobEffectInstance[effectList.size()];
		for (int i = 0, l = effectList.size(); i < l; i++) {
			CompoundTag effectTag = (CompoundTag) effectList.get(i);
			effects[i] = readEffect(effectTag);
		}
		return effects;
	}

	protected static ListTag writeEffectArray(MobEffectInstance[] effects) {
		ListTag effectList = new ListTag();
		for (int i = 0, l = effects.length; i < l; i++) {
			if (effects[i] != null) {
				effectList.add(effects[i].save(new CompoundTag()));
			}
		}
		return effectList;
	}

	protected static Map<String, Integer> readStringIntMap(final CompoundTag tag) {
		final Map<String, Integer> map = new HashMap<>();
		for (final String key : tag.getAllKeys()) {
			map.put(key, tag.getInt(key));
		}
		return map;
	}

	public enum Target implements StringRepresentable {
		SELF("self"),
		ENEMY("enemy"),
		AREA("area");

		private final String name;

		Target(final String nameIn) {
			name = nameIn;
		}

		/**
		 * @param nameIn the name representation of the SwimMode
		 * @return the Target with this name, or SELF as a fallback
		 */
		public static Target getByName(final String nameIn) {
			for (final Target t : Target.values()) {
				if (t.getSerializedName().equals(nameIn)) {
					return t;
				}
			}
			// default to SELF
			return SELF;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}
}
