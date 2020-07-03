package edu.boardgames.collections.explorer.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.EnumSet;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class NumberOfPlayers implements Comparable<NumberOfPlayers> {
	private static final Pattern PATTERN = Pattern.compile("(\\d+)(.*)");

	private final int count;
	private final Suffix suffix;

	public NumberOfPlayers(String input) {
		Matcher matcher = PATTERN.matcher(input);
		if (matcher.matches()) {
			this.count = Integer.parseInt(matcher.group(1));
			this.suffix = Suffix.parse(matcher.group(2));
		} else {
			throw new IllegalArgumentException(String.format("Input [%s] is not supported as number of players", input));
		}
	}

	@Override
	public int compareTo(NumberOfPlayers other) {
		if (other == null) {
			return 1;
		}
		if (this.count == other.count) {
			return this.suffix.compareTo(other.suffix);
		} else {
			return this.count - other.count;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		NumberOfPlayers that = (NumberOfPlayers) other;
		return count == that.count &&
				suffix == that.suffix;
	}

	@Override
	public int hashCode() {
		return Objects.hash(count, suffix);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", NumberOfPlayers.class.getSimpleName() + "[", "]")
				.add(String.format("count=%s", count))
				.add(String.format("suffix=%s", suffix))
				.toString();
	}

	private enum Suffix {
		NONE(""),
		PLUS("+");

		private final String value;

		Suffix(String value) {
			this.value = value;
		}

		String value() {
			return value;
		}

		private static Suffix parse(String input) {
			return EnumSet.allOf(Suffix.class).stream()
			              .filter(suffix -> StringUtils.equals(suffix.value, input))
			              .findFirst()
			              .orElseThrow(() -> new IllegalArgumentException(String.format("Suffix [%s] is not supported in number of players", input)));
		}
	}
}
