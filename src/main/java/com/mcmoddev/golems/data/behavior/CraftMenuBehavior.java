package com.mcmoddev.golems.data.behavior;

import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.menu.PortableCraftingMenu;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * This behavior allows an entity to open a crafting menu
 * when the player interacts with it
 **/
@Immutable
public class CraftMenuBehavior extends Behavior<GolemBase> {

	public static final Codec<CraftMenuBehavior> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
			.apply(instance, CraftMenuBehavior::new));

	/** The behavior description **/
	private final Component description;

	public CraftMenuBehavior(MinMaxBounds.Ints variant) {
		super(variant);
		this.description = Component.translatable("entitytip.crafting_menu").withStyle(ChatFormatting.BLUE);
	}

	//// GETTERS ////

	@Override
	public Codec<? extends Behavior> getCodec() {
		return EGRegistry.BehaviorReg.CRAFT_MENU.get();
	}

	//// METHODS ////

	@Override
	public void onMobInteract(final GolemBase entity, final Player player, final InteractionHand hand) {
		if(!canApply(entity.getTextureId())) {
			return;
		}
		if (!player.isCrouching() && player instanceof ServerPlayer) {
			// update menu player
			if(entity.getPlayerInMenu() != null) {
				entity.getPlayerInMenu().closeContainer();
			}
			entity.setPlayerInMenu(player);
			// display crafting grid by sending request to server
			NetworkHooks.openScreen((ServerPlayer) player, new PortableCraftingMenu.Provider());
			player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
			player.swing(hand);
		}
	}

	@Override
	public void onTick(GolemBase entity) {
		if(!canApply(entity.getTextureId())) {
			return;
		}
		if(null == entity.getPlayerInMenu()) {
			return;
		}
		// stop moving and look at player
		entity.getNavigation().stop();
		entity.getLookControl().setLookAt(entity.getPlayerInMenu());
		// ensure the container closes when the player is too far away
		if(!entity.isPlayerInRangeForMenu(8.0D)) {
			entity.getPlayerInMenu().closeContainer();
			entity.setPlayerInMenu(null);
		}
	}

	@Override
	public void onAddDescriptions(List<Component> list) {
		list.add(this.description);
	}
}
