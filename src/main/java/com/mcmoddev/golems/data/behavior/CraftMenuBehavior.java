package com.mcmoddev.golems.data.behavior;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.data.behavior.util.TooltipPredicate;
import com.mcmoddev.golems.entity.IExtraGolem;
import com.mcmoddev.golems.menu.GolemCraftingMenu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to open a crafting menu
 * when the player interacts with it
 **/
@Immutable
public class CraftMenuBehavior extends Behavior {

	public static final Codec<CraftMenuBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.apply(instance, CraftMenuBehavior::new));

	public CraftMenuBehavior(MinMaxBounds.Ints variant, TooltipPredicate tooltipPredicate) {
		super(variant, tooltipPredicate);
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.CRAFT_MENU.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(final IExtraGolem entity, final Player player, final InteractionHand hand) {
		if (!player.isCrouching() && player instanceof ServerPlayer) {
			// update menu player
			if(entity.getPlayerInMenu() != null) {
				entity.getPlayerInMenu().closeContainer();
			}
			entity.setPlayerInMenu(player);
			// display crafting grid by sending request to server
			final Mob mob = entity.asMob();
			NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
					(windowId, inv, menuPlayer) -> new GolemCraftingMenu(windowId, inv, entity, ContainerLevelAccess.create(mob.level(), mob.blockPosition())),
					mob.getName()));
			player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			player.swing(hand);
		}
	}

	@Override
	public void onTick(IExtraGolem entity) {
		if(null == entity.getPlayerInMenu()) {
			return;
		}
		// stop moving and look at player
		entity.asMob().getNavigation().stop();
		entity.asMob().getLookControl().setLookAt(entity.getPlayerInMenu());
		// ensure the container closes when the player is too far away
		if(!entity.isPlayerInRangeForMenu(8.0D)) {
			entity.getPlayerInMenu().closeContainer();
			entity.setPlayerInMenu(null);
		}
	}

	@Override
	public List<Component> createDescriptions(RegistryAccess registryAccess) {
		return ImmutableList.of(Component.translatable(PREFIX + "crafting_menu").withStyle(ChatFormatting.BLUE));
	}
}
