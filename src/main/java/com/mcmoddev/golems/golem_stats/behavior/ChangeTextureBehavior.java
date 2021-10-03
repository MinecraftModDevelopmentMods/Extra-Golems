package com.mcmoddev.golems.golem_stats.behavior;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.goal.ChangeTextureGoal;
import com.mcmoddev.golems.golem_stats.behavior.parameter.ChangeIdBehaviorParameter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;


/**
 * This behavior allows an entity to change its texture based on
 * several conditions, such as a random chance each tick or while
 * wet, dry, fueled, or empty of fuel.
 **/
@Immutable
public class ChangeTextureBehavior extends GolemBehavior {
  
  /** The map to use for per-tick changes. Accepts texture ID as string only **/
  private final Optional<ChangeIdBehaviorParameter> tickTextures;
  
  /** The map to use when the entity is wet **/
  private final Optional<ChangeIdBehaviorParameter> wetTextures;
  /** The map to use when the entity is dry **/
  private final Optional<ChangeIdBehaviorParameter> dryTextures;
  
  /** The map to use when the entity has fuel. Only used when UseFuelBehavior is present **/
  private final Optional<ChangeIdBehaviorParameter> fueledTextures;
  /** The map to use when the entity has no fuel. Only used when UseFuelBehavior is present **/
  private final Optional<ChangeIdBehaviorParameter> emptyTextures;

  /** The map to use when an item is used on the entity **/
  private final Optional<ChangeIdBehaviorParameter> itemTextures;
    
  public ChangeTextureBehavior(final CompoundNBT tag) {
    super(tag);
    tickTextures = tag.contains("tick") ? Optional.of(new ChangeIdBehaviorParameter(tag.getCompound("tick"), "textures")) : Optional.empty();
    wetTextures = tag.contains("wet") ? Optional.of(new ChangeIdBehaviorParameter(tag.getCompound("wet"), "textures")) : Optional.empty();
    dryTextures = tag.contains("dry") ? Optional.of(new ChangeIdBehaviorParameter(tag.getCompound("dry"), "textures")) : Optional.empty();
    fueledTextures = tag.contains("fuel") ? Optional.of(new ChangeIdBehaviorParameter(tag.getCompound("fuel"), "textures")) : Optional.empty();
    emptyTextures = tag.contains("fuel_empty") ? Optional.of(new ChangeIdBehaviorParameter(tag.getCompound("fuel_empty"), "textures")) : Optional.empty();
	itemTextures = tag.contains("use_item") ? Optional.of(new ChangeIdBehaviorParameter(tag.getCompound("use_item"), "items", "texture")) : Optional.empty();
  }

  @Override
  public void onRegisterGoals(final GolemBase entity) {
    if(entity.getContainer().getMultitexture().isPresent()) {
      entity.goalSelector.addGoal(1, new ChangeTextureGoal<>(entity, tickTextures, wetTextures, dryTextures, fueledTextures, emptyTextures));
    }
  }

  @Override
  public void onMobInteract(final GolemBase entity, final PlayerEntity player, final Hand hand) {
	ItemStack item = player.getHeldItem(hand);
	if (itemTextures.isPresent() && !item.isEmpty()) {
	  // attempt to process item name
	  if(processKey(entity, player, item, itemTextures.get(), item.getItem().getRegistryName().toString())) {
		return;
	  }
	  // if item name is not in map, attempt to process each tag with '#' prefix
	  for(ResourceLocation r : item.getItem().getTags()) {
		if(processKey(entity, player, item, itemTextures.get(), "#".concat(r.toString()))) {
		  return;
		}
	  }
	}
  }

  /**
   * Attempts to change the entity texture based on the string key
   * @param entity the Golem
   * @param player the Player
   * @param held the Player's held item
   * @param itemTextures the ExtendedChangeIdBehaviorParameter
   * @param key the String key (either an item name or item tag)
   * @return True if the entity texture was changed
   */
  private static boolean processKey(final GolemBase entity, final PlayerEntity player, final ItemStack held,
									ChangeIdBehaviorParameter itemTextures, final String key) {
	final String currentId = String.valueOf(entity.getTextureId());
	String textureId = itemTextures.getId(key, currentId);
	double chance = itemTextures.getChance(key);
	boolean consume = itemTextures.consume(key);
	if(entity.world.getRandom().nextFloat() < chance && textureId != null && !textureId.isEmpty() && !currentId.equals(textureId)) {
	  // the string key was present and changes the texture, so do that
	  entity.setTextureId(Byte.parseByte(textureId));
	  // attempt to replace the item either by shrinking and spawning container item
	  if(consume) {
		ItemStack cont = held.getContainerItem();
		// shrink the item stack
		if(!player.isCreative()) {
		  held.shrink(1);
		  // attempt to spawn container item
		  if(!cont.isEmpty()) {
			player.addItemStackToInventory(cont);
		  }
		}
	  }
	  return true;
	}
	return false;
  }

}
