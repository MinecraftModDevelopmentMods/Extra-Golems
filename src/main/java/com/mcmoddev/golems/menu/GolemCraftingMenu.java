package com.mcmoddev.golems.menu;

import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class GolemCraftingMenu extends CraftingMenu {

	private final @Nullable IExtraGolem entity;
	private final ContainerLevelAccess access;

	public GolemCraftingMenu(final int i, final Inventory inv) {
		this(i, inv, null, ContainerLevelAccess.create(inv.player.level(), inv.player.blockPosition()));
	}

	public GolemCraftingMenu(final int i, final Inventory inv, @Nullable final IExtraGolem entity, ContainerLevelAccess access) {
		super(i, inv, access);
		this.entity = entity;
		this.access = access;
	}

	@Override
	public boolean stillValid(final Player player) {
		return access.evaluate((level, blockPos) -> player.position().closerThan(Vec3.atCenterOf(blockPos), 8.0D), true);
	}

	@Override
	public void removed(Player player) {
		if(this.entity != null && this.entity.getPlayerInMenu() == player) {
			this.entity.setPlayerInMenu(null);
		}
		super.removed(player);
	}

	@Override
	public MenuType<?> getType() {
		return super.getType();
	}
}
