package com.mcmoddev.golems.util.config;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

/**
 * <p>A more resource-intensive but flexible way of storing
 * the blocks used to build a Golem. Supports both
 * Block Tags and Block Sets for each of the four
 * building blocks.
 * <p>Call one overload of each of the four methods in order 
 * to build a complete GolemPattern. Only call one overload
 * per body segment.
 * @see #body(ResourceLocation)
 * @see #body(Set)
 * @see #legs(ResourceLocation)
 * @see #legs(Set)
 * @see #arm1(ResourceLocation)
 * @see #arm1(Set)
 * @see #arm2(ResourceLocation)
 * @see #arm2(Set)
 **/
public class GolemPattern {
	
	private ResourceLocation tagBody;
	private ResourceLocation tagLegs;
	private ResourceLocation tagArm1;
	private ResourceLocation tagArm2;
	
	private Set<Block> body = null;
	private Set<Block> legs = null;
	private Set<Block> arm1 = null;
	private Set<Block> arm2 = null;
	
	/**
	 * @param lBody
	 * @param lLegs
	 * @param lArm1
	 * @param lArm2
	 * @return whether this GolemPattern is a valid match for the given blocks
	 **/
	public boolean matches(final Block lBody, final Block lLegs, final Block lArm1, final Block lArm2) {
		return contains(body, tagBody, lBody) && contains(legs, tagLegs, lLegs)
				&& contains(arm1, tagArm1, lArm1) && contains(arm2, tagArm2, lArm2);
	}
	
	/**
	 * Required for correctly loading tags - they must be called as needed and
	 * can not be stored or queried before they are properly loaded and reloaded.
	 *
	 * @param rls a Collection of ResourceLocation IDs that represent Block Tags.
	 * @return a current Collection of Block Tags
	 **/
	private static Tag<Block> loadTag(ResourceLocation rl) {
		if (BlockTags.getCollection().get(rl) != null) {
			return BlockTags.getCollection().get(rl);
		}
		return null;
	}
	
	/**
	 * @param set a Set of Blocks, possibly null
	 * @param rl the name key of a Block Tag, possibly null
	 * @param b the Block to find
	 * @return whether the block is present in either the given Set or BlockTag
	 **/
	private static boolean contains(final Set<Block> set, final ResourceLocation rl, final Block b) {
		if(set != null) {
			return set.contains(b);
		} else if(rl != null) {
			final Tag<Block> tag = loadTag(rl);
			return tag != null && tag.contains(b);
		}
		return false;
	}
	
	public GolemPattern body(final Set<Block> bodyBlocks) {
		body = bodyBlocks;
		return this;
	}
	
	public GolemPattern body(final ResourceLocation bodyBlocks) {
		tagBody = bodyBlocks;
		return this;
	}
	
	public GolemPattern legs(final Set<Block> legsBlocks) {
		legs = legsBlocks;
		return this;
	}
	
	public GolemPattern legs(final ResourceLocation legsBlocks) {
		tagLegs = legsBlocks;
		return this;
	}
	
	public GolemPattern arm1(final Set<Block> arm1Blocks) {
		arm1 = arm1Blocks;
		return this;
	}
	
	public GolemPattern arm1(final ResourceLocation arm1Blocks) {
		tagArm1 = arm1Blocks;
		return this;
	}
	
	public GolemPattern arm2(final Set<Block> arm2Blocks) {
		arm2 = arm2Blocks;
		return this;
	}
	
	public GolemPattern arm2(final ResourceLocation arm2Blocks) {
		tagArm2 = arm2Blocks;
		return this;
	}
}
