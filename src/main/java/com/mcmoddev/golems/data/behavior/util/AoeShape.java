package com.mcmoddev.golems.data.behavior.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

public enum AoeShape implements StringRepresentable {
	CUBE("cube"),
	DISC("disc"),
	SPHERE("sphere");

	public static final Codec<AoeShape> CODEC = StringRepresentable.fromEnum(AoeShape::values);

	private final String name;

	AoeShape(String name) {
		this.name = name;
	}

	public Iterable<BlockPos> createPositions(final BlockPos center, final int radius) {
		final Vec3 centerVec = Vec3.atCenterOf(center);
		switch (this) {
			case CUBE: return BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius));
			case DISC:
				final BlockPos.MutableBlockPos filterPos = center.mutable();
				return Iterables.filter(BlockPos.betweenClosed(center.offset(-radius, -1, -radius), center.offset(radius, 1, radius)),
					b -> filterPos.set(center.getX(), b.getY(), center.getZ()).closerToCenterThan(centerVec, radius));
			case SPHERE: return Iterables.filter(BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius)),
					b -> b.closerToCenterThan(centerVec, radius));
		}
		return ImmutableSet.of();
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
