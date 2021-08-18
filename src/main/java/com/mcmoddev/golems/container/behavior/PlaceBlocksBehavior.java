package com.mcmoddev.golems.container.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.container.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.PlaceBlocksGoal;
import com.mcmoddev.golems.util.ResourcePair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

@Immutable
public class PlaceBlocksBehavior extends GolemBehavior {
    
  private final int interval;
  
  private final List<ResourceLocation> blocks = new ArrayList<>();
  private final List<ResourceLocation> blockTags = new ArrayList<>();
  
  private final List<ResourceLocation> supports = new ArrayList<>();
  private final List<ResourceLocation> supportTags = new ArrayList<>();

  public PlaceBlocksBehavior(CompoundTag tag) {
    super(tag, GolemBehaviors.PLACE_BLOCKS);
    interval = tag.getInt("interval");
    // read blocks
    ListTag blocksTag = (ListTag) tag.get("blocks");
    for(int i = 0, l = blocksTag.size(); i < l; i++) {
      Optional<ResourcePair> result = ResourcePair.read(blocksTag.getString(i)).resultOrPartial(s -> ExtraGolems.LOGGER.error("Error reading 'blocks' from NBT\n" + s));
      result.ifPresent(p -> {
        if(p.flag()) blockTags.add(p.resource());
        else blocks.add(p.resource());
      });
    }
    // read supports
    ListTag supportsTag = (ListTag) tag.get("supports");
    for(int i = 0, l = supportsTag.size(); i < l; i++) {
      Optional<ResourcePair> result = ResourcePair.read(blocksTag.getString(i)).resultOrPartial(s -> ExtraGolems.LOGGER.error("Error reading 'supports' from NBT\n" + s));
      result.ifPresent(p -> {
        if(p.flag()) supportTags.add(p.resource());
        else supports.add(p.resource());
      });
    }
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    // resolve blocks and block tags
    List<Block> blockList = new ArrayList<>();
    blockList.addAll(GolemContainer.getAllBlocks(blocks, blockTags));
    // resolve supports and support tags
    List<Block> supportList = new ArrayList<>();
    supportList.addAll(GolemContainer.getAllBlocks(supports, supportTags));
    // add a new PlaceBlocksGoal with the given parameters
    entity.goalSelector.addGoal(2, new PlaceBlocksGoal(entity, interval, blockList.toArray(new Block[0]), supportList.toArray(new Block[0])));
  }
}
