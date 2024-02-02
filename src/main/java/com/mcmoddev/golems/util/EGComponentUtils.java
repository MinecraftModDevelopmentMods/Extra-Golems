package com.mcmoddev.golems.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class EGComponentUtils {

	private static final String DELIMITER = "(?i)&([0-9A-FK-OR])";
	private static final String FORMATTING_REGEX = "((?<=" + DELIMITER + ")|(?=" + DELIMITER + "))";

	public static List<Component> parseComponents(final List<String> strings) {
		final List<Component> list = new ArrayList<>();
		// iterate over all entries
		for(String s : strings) {
			list.add(parseComponent(Component.translatable(s).getString()));
		}
		return list;
	}

	public static Component parseComponent(final String string) {
		final Component component = Component.empty();
		// split into multiple lines
		String[] spans = string.split(FORMATTING_REGEX);
		ChatFormatting formatting = null;
		for(int i = 0, n = spans.length; i < n; i++) {
			String span = spans[i];
			// detect formatting code
			if(span.length() == 2 && span.matches(DELIMITER)) {
				formatting = ChatFormatting.getByCode(span.charAt(1));
				continue;
			}
			// create component
			MutableComponent spanComponent = Component.literal(span);
			// add formatting, if any
			if(formatting != null) {
				spanComponent = spanComponent.withStyle(formatting);
			}
			// add to parent
			component.getSiblings().add(spanComponent);
		}
		return component;
	}

	public static String stripFormatting(final String string) {
		return string.replaceAll(DELIMITER, "");
	}

	/**
	 * @param list a list of objects to combine
	 * @param getDescriptionId the function to get a translation key from the object
	 * @param <T> the object
	 * @return a component in the form "X and Y and Z" for all items in the list
	 */
	public static <T> Optional<Component> combineWithAnd(final List<T> list, final Function<T, String> getDescriptionId) {
		if(list.isEmpty()) {
			return Optional.empty();
		}
		// initialize text as first predicate in list
		Component text = Component.translatable(getDescriptionId.apply(list.get(0)));
		// add remaining items from list
		for(int i = 1, n = list.size(); i < n; i++) {
			T t = list.get(i);
			text = Component.translatable("golem.description.predicates.and", text, Component.translatable(getDescriptionId.apply(t)));
		}
		return Optional.of(text);
	}
}
