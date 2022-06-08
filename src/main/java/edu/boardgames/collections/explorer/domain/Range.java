package edu.boardgames.collections.explorer.domain;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

public record Range<T extends Comparable<T>>(T lowerBound, T upperBound) {
    public static <V extends Comparable<V>> Optional<Range<V>> of(Iterable<V> bounds) {
        ImmutableList<V> sorted = Lists.immutable.withAll(bounds);
        return sorted.minOptional().map(head -> new Range<>(head, sorted.max()));
    }

    public Range(T lowerBound, T upperBound) {
        this.lowerBound = Objects.requireNonNull(lowerBound);
        this.upperBound = Objects.requireNonNull(upperBound);
    }

    public boolean contains(T value) {
        if (value == null) {
            return false;
        }
        return value.compareTo(this.lowerBound) >= 0 && value.compareTo(this.upperBound) <= 0;
    }

    public <R extends Comparable<R>> Range<R> map(Function<T, R> mapper) {
        return new Range<>(mapper.apply(lowerBound), mapper.apply(upperBound));
    }

    public String formatted() {
        if (lowerBound.equals(upperBound)) {
            return StringUtils.defaultIfEmpty(lowerBound.toString(), "0");
        } else {
            return String.format("%s-%s", lowerBound, upperBound);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Range.class.getSimpleName() + "[", "]")
            .add(String.format("lowerBound=%s", lowerBound))
            .add(String.format("upperBound=%s", upperBound))
            .toString();
    }
}
