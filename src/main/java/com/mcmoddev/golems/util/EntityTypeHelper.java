package com.mcmoddev.golems.util;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.main.ExtraGolems;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.function.Function;

public class EntityTypeHelper {
	public static EntityType provideEntityType(final Class<? extends GolemBase> entityClass,
						   Function<? super World, ? extends GolemBase> factoryIn,
						   final String name, Block... blocks) {
		// register block(s) with GolemLookup
		if (blocks != null && blocks.length > 0) {
			GolemLookup.addGolem(entityClass, blocks);
		}
		EntityType.Builder<GolemBase> builder = EntityType.Builder.create(entityClass, factoryIn);
		// build an EntityType to return
		builder.tracker(48, 3, true);
		return builder.build(new ResourceLocation(ExtraGolems.MODID, name).toString());
	}
}
