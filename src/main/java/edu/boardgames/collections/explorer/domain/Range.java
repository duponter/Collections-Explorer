package edu.boardgames.collections.explorer.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.Function;

public class Range<T> {
	private final T lowerBound;
	private final T upperBound;

	public static <V> Range<V> of(V lowerBound, V upperBound) {
		if (lowerBound.equals(upperBound)) {
			return new Range<>(lowerBound, upperBound) {
				@Override
				public String formatted() {
					return StringUtils.defaultIfEmpty(lowerBound.toString(), "0");
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
		return Range.of(mapper.apply(lowerBound), mapper.apply(upperBound));
	}

	public String formatted() {
		return String.format("%s-%s", lowerBound, upperBound);
	}
}