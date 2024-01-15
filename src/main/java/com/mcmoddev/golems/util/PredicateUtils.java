package com.mcmoddev.golems.util;

import com.mcmoddev.golems.data.behavior.util.WorldPredicate;
import com.mcmoddev.golems.entity.GolemBase;

import java.util.List;
import java.util.function.Predicate;

public final class PredicateUtils {

	/**
	 * @param predicates a list of {@link GolemBase} predicatess
	 * @return a single {@link Predicate} that requires all of the given predicates to pass, or {@code false} for an empty list
	 */
	public static <U, T extends Predicate<U>> Predicate<U> and(final List<T> predicates) {
		if(predicates.isEmpty()) {
			return e -> false;
		}
		Predicate<U> predicate = predicates.get(0);
		for(int i = 1, n = predicates.size(); i < n; i++) {
			predicate = predicate.and(predicates.get(i));
		}
		return predicate;
	}

	/**
	 * @param predicates a list of {@link WorldPredicate}s
	 * @return a single {@link Predicate} that requires any of the given predicates to pass, or {@code false} for an empty list
	 */
	public static <U, T extends Predicate<U>> Predicate<U> or(final List<T> predicates) {
		if(predicates.isEmpty()) {
			return e -> false;
		}
		Predicate<U> predicate = predicates.get(0);
		for(int i = 1, n = predicates.size(); i < n; i++) {
			predicate = predicate.or(predicates.get(i));
		}
		return predicate;
	}
}
