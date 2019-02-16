package com.golems.util;

import java.util.function.Consumer;

import com.golems.entity.EntityConcreteGolem;
import com.golems.entity.EntityMushroomGolem;
import com.golems.entity.EntityStainedClayGolem;
import com.golems.entity.EntityStainedGlassGolem;
import com.golems.entity.EntityWoodenGolem;
import com.golems.entity.EntityWoolGolem;
import com.golems.main.ExtraGolems;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

public class ConsumerLootTables implements Consumer<String> {
		
	@Override
		public void accept(final String NAME) {
			switch(NAME) {
			case GolemNames.BEDROCK_GOLEM:
				return;
			case GolemNames.WOOL_GOLEM:
				registerLootTables(ExtraGolems.MODID, NAME, EntityWoolGolem.coloredWoolTypes);
				return;
			case GolemNames.WOODEN_GOLEM:
				registerLootTables(ExtraGolems.MODID, NAME, EntityWoodenGolem.woodTypes);
				return;
			case GolemNames.MUSHROOM_GOLEM:
				registerLootTables(ExtraGolems.MODID, NAME, EntityMushroomGolem.SHROOM_TYPES);
				return;			
			case GolemNames.STAINEDGLASS_GOLEM:
				registerLootTables(ExtraGolems.MODID, NAME, EntityStainedGlassGolem.COLOR_ARRAY.length);
				return;
			case GolemNames.STAINEDTERRACOTTA_GOLEM:
				registerLootTables(ExtraGolems.MODID, NAME, EntityStainedClayGolem.COLOR_ARRAY.length);
				return;
			case GolemNames.CONCRETE_GOLEM:
				registerLootTables(ExtraGolems.MODID, NAME, EntityConcreteGolem.COLOR_ARRAY.length);
				return;
			default:
				LootTableList.register(new ResourceLocation(ExtraGolems.MODID, "entities/" + NAME));
				return;
			}
		}
		
		/**
		 * Registers multiple loot tables for each of the textures specified. They are registered under
		 * the subfile [name] and individually named according to each element in [textures]
		 */
		private static void registerLootTables(final String MODID, final String name, final String[] textures) {
			for(String s : textures) {
				LootTableList.register(new ResourceLocation(MODID, "entities/" + name + "/" + s));
			}
		}
		
		/**
		 * Registers loot tables for GolemColorizedMultiTextured, with loot tables 
		 * registered under the subfile [name] and individually named '0' through '[max-1]'
		 */
		private static void registerLootTables(final String MODID, final String name, final int max) {
			String[] array = new String[max];
			for (int i = 0; i < max; i++) {
				array[i] = Integer.toString(i);
			}
			registerLootTables(MODID, name, array);
		}
}
