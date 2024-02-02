package com.mcmoddev.golems.data.modifier.golem;

import com.google.common.collect.ImmutableList;
import com.mcmoddev.golems.EGRegistry;
import com.mcmoddev.golems.ExtraGolems;
import com.mcmoddev.golems.data.golem.Golem;
import com.mcmoddev.golems.data.modifier.Modifier;
import com.mcmoddev.golems.util.EGCodecUtils;
import com.mcmoddev.golems.util.EGComponentUtils;
import com.mcmoddev.golems.util.PredicateUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Removes entries in the {@link Golem.Builder} description list
 * that pass any of the given {@link RemovePredicate}s
 */
@Immutable
public class RemoveDescriptionModifier extends Modifier {

	public static final Codec<RemoveDescriptionModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EGCodecUtils.listOrElementCodec(RemovePredicate.CODEC).optionalFieldOf("predicate", ImmutableList.of()).forGetter(RemoveDescriptionModifier::getPredicates)
	).apply(instance, RemoveDescriptionModifier::new));

	private final List<RemovePredicate> predicates;
	private final Predicate<String> predicate;

	public RemoveDescriptionModifier(List<RemovePredicate> predicates) {
		this.predicates = predicates;
		this.predicate = predicates.isEmpty() ? (o -> true) : PredicateUtils.or(predicates);
	}

	//// GETTERS ////

	/** @return The predicates to test String entries after removing formatting codes. If this list is empty, all entries will be removed. **/
	public List<RemovePredicate> getPredicates() {
		return predicates;
	}

	//// METHODS ////

	@Override
	public void apply(Golem.Builder builder) {
		builder.descriptions(list -> list.removeIf(this.predicate));
	}

	@Override
	public Codec<? extends Modifier> getCodec() {
		return EGRegistry.GolemModifierReg.REMOVE_DESCRIPTION.get();
	}

	//// CLASSES ////

	public static class RemovePredicate implements Predicate<String> {

		public static final Codec<RemovePredicate> CODEC = Codec.STRING.xmap(RemovePredicate::new, o -> o.regex);

		/** The regex to test a string to remove. Formatting codes are removed before testing. **/
		private final String regex;
		private final Pattern pattern;

		public RemovePredicate(String regex) {
			this.regex = regex;
			Pattern p;
			try {
				p = Pattern.compile(regex);
			} catch (PatternSyntaxException e) {
				ExtraGolems.LOGGER.error("Invalid regular expression in RemoveDescriptionModifier: " + regex);
				ExtraGolems.LOGGER.error(e.getMessage());
				p = Pattern.compile("");
			}
			this.pattern = p;
		}

		@Override
		public boolean test(String string) {
			return pattern.matcher(EGComponentUtils.stripFormatting(string)).matches();
		}
	}
}
