package edu.boardgames.collections.explorer.domain;

import java.util.Objects;
import java.util.function.Function;

public class Range<T> {
	private final T lowerBound;
	private final T upperBound;

	public static <V extends Comparable<V>> Range<V> of(V lowerBound, V upperBound) {
		if (lowerBound.equals(upperBound)) {
			return new Range<>(lowerBound, upperBound) {
				@Override
				public String formatted() {
					return lowerBound.toString();
				}
			};
		} else {
			return new Range<>(lowerBound, upperBound);
		}
	}

	private Range(T lowerBound, T upperBound) {
		this.lowerBound = Objects.requireNonNull(lowerBound);
		this.upperBound = Objects.requireNonNull(upperBound);
	}

	public <R> Range<R> map(Function<T, R> mapper) {
		return new Range<>(mapper.apply(lowerBound), mapper.apply(upperBound));
	}

	public String formatted() {
		return String.format("%s-%s", lowerBound, upperBound);
	}
}