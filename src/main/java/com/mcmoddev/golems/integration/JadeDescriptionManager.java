package com.mcmoddev.golems.integration;

import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.GolemContainer;
import com.mcmoddev.golems.entity.GolemBase;
import com.mcmoddev.golems.entity.IExtraGolem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ItemStackElement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@WailaPlugin
public class JadeDescriptionManager implements IWailaPlugin {

	private static final ResourceLocation CONFIG_FG_COLOR = new ResourceLocation(ExtraGolems.MODID, "fg_color");
	private static final ResourceLocation CONFIG_SHOW_ICON = new ResourceLocation(ExtraGolems.MODID, "show_icon");

	@Override
	public void register(IWailaCommonRegistration registration) {
		// nothing
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(new EntityProvider(), GolemBase.class);
		registration.addConfig(CONFIG_FG_COLOR, ChatFormatting.WHITE);
		registration.addConfig(CONFIG_SHOW_ICON, false);
	}

	private static class EntityProvider extends GolemDescriptionManager implements IEntityComponentProvider {

		private static final ResourceLocation UID = new ResourceLocation(ExtraGolems.MODID, "jade_entity_provider");

		@Override
		public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
			this.extended = entityAccessor.getPlayer().isShiftKeyDown();
			// load entity
			if(entityAccessor.getEntity() instanceof IExtraGolem entity) {
				// load golem container
				final Optional<GolemContainer> oContainer = entity.getContainer(entityAccessor.getEntity().level().registryAccess());
				if(oContainer.isEmpty()) {
					return;
				}
				// determine color
				final ChatFormatting fgColor = iPluginConfig.getEnum(CONFIG_FG_COLOR);
				// create elements
				final List<Component> lines = getEntityDescription(entity, oContainer.get());
				for(Component line : lines) {
					iTooltip.add(iTooltip.getElementHelper().text(Component.literal(line.getString()).withStyle(fgColor)));
				}
			}
		}

		@Override
		public @Nullable IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
			if(config.get(CONFIG_SHOW_ICON) && accessor.getEntity() instanceof IExtraGolem entity) {
				// load golem container
				final Optional<GolemContainer> oContainer = entity.getContainer(accessor.getEntity().level().registryAccess());
				if(oContainer.isPresent()) {
					// load pick result
					ItemStack itemStack = oContainer.get().getGolem().getBlocks().getPickResult();
					if(!itemStack.isEmpty()) {
						return ItemStackElement.of(itemStack);
					}
				}
			}
			return IEntityComponentProvider.super.getIcon(accessor, config, currentIcon);
		}

		@Override
		public ResourceLocation getUid() {
			return UID;
		}
	}
}
