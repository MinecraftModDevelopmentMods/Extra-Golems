package com.mcmoddev.golems.data.behavior.util;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

public enum AoeShape implements StringRepresentable {
	CUBE("cube"),
	GRID("grid"),
	DISC("disc"),
	SPHERE("sphere");

	public static final Codec<AoeShape> CODEC = StringRepresentable.fromEnum(AoeShape::values);

	private final String name;

	AoeShape(String name) {
		this.name = name;
	}

	public Iterable<BlockPos> createPositions(final BlockPos center, final int radius) {
		final Vec3 centerVec = Vec3.atCenterOf(center);
		final Iterable<BlockPos> betweenClosed = BlockPos.betweenClosed(getFromPos(center, radius), getToPos(center, radius));
		final BlockPos.MutableBlockPos filterPos = center.mutable();
		switch (this) {
			default: case CUBE: return betweenClosed;
			case SPHERE: return Iterables.filter(betweenClosed, b -> b.closerToCenterThan(centerVec, radius));
			case GRID: return Iterables.filter(betweenClosed, b -> b.getX() % 2 == 0 && b.getY() % 2 == 0 && b.getZ() % 2 == 0);
			case DISC: return Iterables.filter(betweenClosed, b -> filterPos.set(b.getX(), center.getY(), b.getZ()).closerToCenterThan(centerVec, radius));

		}
	}

	public BlockPos getFromPos(final BlockPos center, final int radius) {
		switch (this) {
			default: case SPHERE: case CUBE: case GRID: return center.offset(-radius, -radius, -radius);
			case DISC: return center.offset(-radius, -1, -radius);
		}
	}

	public BlockPos getToPos(final BlockPos center, final int radius) {
		switch (this) {
			default: case SPHERE: case CUBE: case GRID: return center.offset(radius, radius, radius);
			case DISC: return center.offset(radius, 1, radius);
		}
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}
}
