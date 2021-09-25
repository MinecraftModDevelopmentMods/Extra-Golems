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
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

/**
 * This behavior allows an entity to place blocks at its current location.
 * This is not used for utility blocks such as light and power.
 **/
@Immutable
public class PlaceBlocksBehavior extends GolemBehavior {
  
  /** The average number of ticks between activation of this behavior **/
  private final int interval;
  /** The block IDs of blocks that can be placed **/
  private final List<ResourceLocation> blocks = new ArrayList<>();
  /** The block Tags of blocks that can be placed **/
  private final List<ResourceLocation> blockTags = new ArrayList<>();

  public PlaceBlocksBehavior(CompoundNBT tag) {
    super(tag);
    interval = tag.getInt("interval");
    // read blocks
    ListNBT blocksTag = (ListNBT) tag.get("blocks");
    for(int i = 0, l = blocksTag.size(); i < l; i++) {
      Optional<ResourcePair> result = ResourcePair.read(blocksTag.getString(i)).resultOrPartial(s -> ExtraGolems.LOGGER.error("Error reading 'blocks' from NBT\n" + s));
      result.ifPresent(p -> {
        if(p.flag()) blockTags.add(p.resource());
        else blocks.add(p.resource());
      });
    }
  }
  
  @Override
  public void onRegisterGoals(final GolemBase entity) {
    // resolve blocks and block tags
    List<Block> blockList = new ArrayList<>();
    blockList.addAll(GolemContainer.getAllBlocks(blocks, blockTags));
    // add a new PlaceBlocksGoal with the given parameters
    entity.goalSelector.addGoal(2, new PlaceBlocksGoal(entity, interval, blockList.toArray(new Block[0])));
  }
  
  @Override
  public void onAddDescriptions(List<ITextComponent> list) {
    // TODO
  }
}