package com.mcmoddev.golems.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

public final class GolemTextureBytes {
	
	private GolemTextureBytes() { }
	
	public static final Map<Block, Byte> CONCRETE = new HashMap<>();
	public static final Map<Block, Byte> WOOL = new HashMap<>();
	public static final Map<Block, Byte> TERRACOTTA = new HashMap<>();
	public static final Map<Block, Byte> GLASS = new HashMap<>();
	public static final Map<Block, Byte> CORAL = new HashMap<>();
	public static final Map<Block, Byte> CORAL_DEAD = new HashMap<>();

	static {
		// Concrete blocks
		CONCRETE.put(Blocks.WHITE_CONCRETE, (byte) 0);
		CONCRETE.put(Blocks.ORANGE_CONCRETE, (byte) 1);
		CONCRETE.put(Blocks.MAGENTA_CONCRETE, (byte) 2);
		CONCRETE.put(Blocks.LIGHT_BLUE_CONCRETE, (byte) 3);
		CONCRETE.put(Blocks.YELLOW_CONCRETE, (byte) 4);
		CONCRETE.put(Blocks.LIME_CONCRETE, (byte) 5);
		CONCRETE.put(Blocks.PINK_CONCRETE, (byte) 6);
		CONCRETE.put(Blocks.GRAY_CONCRETE, (byte) 7);
		CONCRETE.put(Blocks.LIGHT_GRAY_CONCRETE, (byte) 8);
		CONCRETE.put(Blocks.CYAN_CONCRETE, (byte) 9);
		CONCRETE.put(Blocks.PURPLE_CONCRETE, (byte) 10);
		CONCRETE.put(Blocks.BLUE_CONCRETE, (byte) 11);
		CONCRETE.put(Blocks.BROWN_CONCRETE, (byte) 12);
		CONCRETE.put(Blocks.GREEN_CONCRETE, (byte) 13);
		CONCRETE.put(Blocks.RED_CONCRETE, (byte) 14);
		CONCRETE.put(Blocks.BLACK_CONCRETE, (byte) 15);
		// Wool blocks
		WOOL.put(Blocks.BLACK_WOOL, (byte) 0);
		WOOL.put(Blocks.ORANGE_WOOL, (byte) 1);
		WOOL.put(Blocks.MAGENTA_WOOL, (byte) 2);
		WOOL.put(Blocks.LIGHT_BLUE_WOOL, (byte) 3);
		WOOL.put(Blocks.YELLOW_WOOL, (byte) 4);
		WOOL.put(Blocks.LIME_WOOL, (byte) 5);
		WOOL.put(Blocks.PINK_WOOL, (byte) 6);
		WOOL.put(Blocks.GRAY_WOOL, (byte) 7);
		WOOL.put(Blocks.LIGHT_GRAY_WOOL, (byte) 8);
		WOOL.put(Blocks.CYAN_WOOL, (byte) 9);
		WOOL.put(Blocks.PURPLE_WOOL, (byte) 10);
		WOOL.put(Blocks.BLUE_WOOL, (byte) 11);
		WOOL.put(Blocks.BROWN_WOOL, (byte) 12);
		WOOL.put(Blocks.GREEN_WOOL, (byte) 13);
		WOOL.put(Blocks.RED_WOOL, (byte) 14);
		WOOL.put(Blocks.WHITE_WOOL, (byte) 15);
		// Terracotta blocks
		TERRACOTTA.put(Blocks.WHITE_TERRACOTTA, (byte) 0);
		TERRACOTTA.put(Blocks.ORANGE_TERRACOTTA, (byte) 1);
		TERRACOTTA.put(Blocks.MAGENTA_TERRACOTTA, (byte) 2);
		TERRACOTTA.put(Blocks.LIGHT_BLUE_TERRACOTTA, (byte) 3);
		TERRACOTTA.put(Blocks.YELLOW_TERRACOTTA, (byte) 4);
		TERRACOTTA.put(Blocks.LIME_TERRACOTTA, (byte) 5);
		TERRACOTTA.put(Blocks.PINK_TERRACOTTA, (byte) 6);
		TERRACOTTA.put(Blocks.GRAY_TERRACOTTA, (byte) 7);
		TERRACOTTA.put(Blocks.LIGHT_GRAY_TERRACOTTA, (byte) 8);
		TERRACOTTA.put(Blocks.CYAN_TERRACOTTA, (byte) 9);
		TERRACOTTA.put(Blocks.PURPLE_TERRACOTTA, (byte) 10);
		TERRACOTTA.put(Blocks.BLUE_TERRACOTTA, (byte) 11);
		TERRACOTTA.put(Blocks.BROWN_TERRACOTTA, (byte) 12);
		TERRACOTTA.put(Blocks.GREEN_TERRACOTTA, (byte) 13);
		TERRACOTTA.put(Blocks.RED_TERRACOTTA, (byte) 14);
		TERRACOTTA.put(Blocks.BLACK_TERRACOTTA, (byte) 15);
		// Stained Glass blocks
		GLASS.put(Blocks.WHITE_STAINED_GLASS, (byte) 0);
		GLASS.put(Blocks.ORANGE_STAINED_GLASS, (byte) 1);
		GLASS.put(Blocks.MAGENTA_STAINED_GLASS, (byte) 2);
		GLASS.put(Blocks.LIGHT_BLUE_STAINED_GLASS, (byte) 3);
		GLASS.put(Blocks.YELLOW_STAINED_GLASS, (byte) 4);
		GLASS.put(Blocks.LIME_STAINED_GLASS, (byte) 5);
		GLASS.put(Blocks.PINK_STAINED_GLASS, (byte) 6);
		GLASS.put(Blocks.GRAY_STAINED_GLASS, (byte) 7);
		GLASS.put(Blocks.LIGHT_GRAY_STAINED_GLASS, (byte) 8);
		GLASS.put(Blocks.CYAN_STAINED_GLASS, (byte) 9);
		GLASS.put(Blocks.PURPLE_STAINED_GLASS, (byte) 10);
		GLASS.put(Blocks.BLUE_STAINED_GLASS, (byte) 11);
		GLASS.put(Blocks.BROWN_STAINED_GLASS, (byte) 12);
		GLASS.put(Blocks.GREEN_STAINED_GLASS, (byte) 13);
		GLASS.put(Blocks.RED_STAINED_GLASS, (byte) 14);
		GLASS.put(Blocks.BLACK_STAINED_GLASS, (byte) 15);
		// Live Coral Blocks
		CORAL.put(Blocks.TUBE_CORAL_BLOCK, (byte)0);
		CORAL.put(Blocks.BRAIN_CORAL_BLOCK, (byte)1);
		CORAL.put(Blocks.BUBBLE_CORAL_BLOCK, (byte)2);
		CORAL.put(Blocks.FIRE_CORAL_BLOCK, (byte)3);
		CORAL.put(Blocks.HORN_CORAL_BLOCK, (byte)4);
		// Dead Coral Blocks
		CORAL_DEAD.put(Blocks.DEAD_TUBE_CORAL_BLOCK, (byte)0);
		CORAL_DEAD.put(Blocks.DEAD_BRAIN_CORAL_BLOCK, (byte)1);
		CORAL_DEAD.put(Blocks.DEAD_BUBBLE_CORAL_BLOCK, (byte)2);
		CORAL_DEAD.put(Blocks.DEAD_FIRE_CORAL_BLOCK, (byte)3);
		CORAL_DEAD.put(Blocks.DEAD_HORN_CORAL_BLOCK, (byte)4);
	}
	
	/**
	 * Determine what texture number should be applied for this block
	 * @param map the map to use (should be one of the maps in this class)
	 * @param block the block in question
	 * @return the correct texture number, or 0 if this block is not found.
	 **/
	public static byte getByBlock(final Map<Block, Byte> map, final Block block) {
		return map.containsKey(block) ? map.get(block).byteValue() : 0;
	}
	
	/**
	 * @param map the map to search (should be one of the maps in this class)
	 * @param textureId the current texture byte of the golem
	 * @return the correct numbered Block, or Blocks.AIR if none is found
	 **/
	public static Block getByByte(final Map<Block, Byte> map, final byte textureId) {
		for(Entry<Block, Byte> entry : map.entrySet()) {
			if(entry.getValue().byteValue() == textureId) {
				return entry.getKey();
			}
		}
		return Blocks.AIR;
	}
}
