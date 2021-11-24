package edu.boardgames.collections.explorer.domain.poll;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Filter<T> extends Predicate<T> {
	static <R> Filter<R> of(Predicate<R> predicate) {
		return predicate::test;
	}

	default <R> Filter<R> lift(Function<R, T> mapper) {
		return subject -> this.test(mapper.apply(subject));
	}
}
