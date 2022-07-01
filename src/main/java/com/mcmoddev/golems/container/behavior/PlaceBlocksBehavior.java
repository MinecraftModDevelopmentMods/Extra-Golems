package com.mcmoddev.golems.container.behavior;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.container.behavior.parameter.BehaviorParameter;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PlaceBlocksGoal;
import com.mcmoddev.golems.util.ResourcePair;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This behavior allows an entity to place blocks at its current location.
 * This is not used for utility blocks such as light and power.
 **/
@Immutable
public class PlaceBlocksBehavior extends GolemBehavior {

	/**
	 * The average number of ticks between activation of this behavior
	 **/
	private final int interval;
	/**
	 * The block IDs of blocks that can be placed
	 **/
	private final List<ResourceLocation> blocks = new ArrayList<>();
	/**
	 * The block Tags of blocks that can be placed
	 **/
	private final List<ResourceLocation> blockTags = new ArrayList<>();

	/**
	 * The translation key to use for behavior description
	 */
	private final Component component;

	public PlaceBlocksBehavior(CompoundTag tag) {
		super(tag);
		interval = tag.getInt("interval");
		// read blocks
		ListTag blocksTag = (ListTag) tag.get("blocks");
		for (int i = 0, l = blocksTag.size(); i < l; i++) {
			Optional<ResourcePair> result = ResourcePair.read(blocksTag.getString(i)).resultOrPartial(s -> ExtraGolems.LOGGER.error("Error reading 'blocks' from NBT\n" + s));
			result.ifPresent(p -> {
				if (p.flag()) blockTags.add(p.resource());
				else blocks.add(p.resource());
			});
		}
		// translation component
		if (tag.contains(BehaviorParameter.S_TRANSLATION_KEY)) {
			this.component = Component.translatable(tag.getString(BehaviorParameter.S_TRANSLATION_KEY)).withStyle(ChatFormatting.GREEN);
		} else {
			this.component = Component.translatable("entitytip.places_blocks").withStyle(ChatFormatting.GREEN);
		}
	}

	@Override
	public void onRegisterGoals(final GolemBase entity) {
		// resolve blocks and block tags
		List<Block> blockList = new ArrayList<>(GolemContainer.getAllBlocks(blocks, blockTags));
		// add a new PlaceBlocksGoal with the given parameters
		entity.goalSelector.addGoal(2, new PlaceBlocksGoal(entity, interval, blockList.toArray(new Block[0])));
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		if (!list.contains(component)) {
			list.add(component);
		}
	}
}
