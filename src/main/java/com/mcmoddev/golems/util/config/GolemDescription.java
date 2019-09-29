package com.mcmoddev.golems.util.config;

import com.mcmoddev.golems.util.config.special.GolemSpecialContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Holds an {@link ITextComponent} and an
 * optional {@link GolemSpecialContainer} key
 * to use when deciding whether to add the
 * text to a given {@code List<ITextComponent>}
 *
 * @see com.mcmoddev.golems.integration.GolemDescriptionManager
 **/
public class GolemDescription {

	private final ITextComponent text;
	private final String configKey;
	private final Predicate<ForgeConfigSpec.ConfigValue> configValue;

	/**
	 * Creates a new {@link GolemDescription} with a text component
	 * and String key to use in the config.
	 *
	 * @param textIn        a pre-formatted text component to display
	 * @param configKeyIn   an optional String to match against the config before
	 * @param configValueIn a {@code Predicate} to determine if the config value is
	 *                      acceptable in order to display this description.
	 *                      adding this text to any description lists.
	 **/
	public GolemDescription(final ITextComponent textIn, final String configKeyIn,
			@Nullable final Predicate<ForgeConfigSpec.ConfigValue> configValueIn) {
		this.text = textIn;
		this.configKey = configKeyIn != null ? configKeyIn : "";
		this.configValue = configValueIn != null ? configValueIn : c -> true;
	}

	/**
	 * Creates a new {@link GolemDescription} with a text component
	 * and String key to use in the config. Assumes that the key links to a
	 * {@code ConfigValue<Boolean>} - if this is not the case, use
	 * {@link #GolemDescription(ITextComponent, String, Predicate)}
	 *
	 * @param textIn      a pre-formatted text component to display
	 * @param configKeyIn an optional String to match against the config before
	 *                    adding this text to any description lists.
	 **/
	public GolemDescription(final ITextComponent textIn, final String configKeyIn) {
		this(textIn, configKeyIn, (c) -> c != null && c.get() instanceof Boolean && (Boolean) c.get());
	}

	/**
	 * Creates a new {@link GolemDescription} with a text component
	 * that will always be displayed.
	 *
	 * @param textIn a pre-formatted text component to display
	 **/
	public GolemDescription(final ITextComponent textIn) {
		this(textIn, "", c -> true);
	}

	/**
	 * Adds the text to the description without any permissions checking.
	 * If you want to check the config first, use
	 * {@link #addDescription(List, GolemContainer)}
	 *
	 * @param list a list to which text will be added
	 * @return True, as specified in interface {@code Collection<E>}
	 **/
	public boolean addDescription(final List<ITextComponent> list) {
		return list.add(this.text);
	}

	/**
	 * @param list      a list to which text will be added if possible
	 * @param container the GolemContainer to query for config permissions
	 * @return true if the description was added.
	 * @see #addDescription(List)
	 **/
	public boolean addDescription(final List<ITextComponent> list, final GolemContainer container) {
		// check the config
		if (isEnabledFor(container)) {
			return addDescription(list);
		}
		return false;
	}

	/**
	 * Checks if this tooltip should be added to any list. If an empty key
	 * or no key was provided, then this method always returns true.
	 *
	 * @param container the GolemContainer to query for config permissions
	 * @return True if this tooltip should be added.
	 **/
	public boolean isEnabledFor(final GolemContainer container) {
		return this.configKey.isEmpty() || this.configValue.test(
				ExtraGolemsConfig.GOLEM_CONFIG.specials.get(
						container.specialContainers.get(this.configKey)).value);
	}
}
