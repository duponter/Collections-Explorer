package edu.boardgames.collections.explorer.ui.text.format;

import java.util.function.Function;

public final class Score implements Function<Double, String> {
	private final String format;
	private final int scale;

	public static Score score5() {
		return new Score("%4.2f", 5);
	}

	public static Score score10() {
		return new Score("%4.1f", 10);
	}

	public static Score percentage() {
		return new Score("%6.2f%%", 100);
	}

	private Score(String format, int scale) {
		this.format = format;
		this.scale = scale;
	}

	@Override
	public String apply(Double value) {
		return String.format(format, value);
	}

	public String fullString(Double value) {
		return "%s / %d".formatted(this.apply(value), scale);
	}
}
