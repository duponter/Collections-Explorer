package edu.boardgames.collections.explorer.ui.text.format;

import java.util.function.Function;

public final class Score implements Function<Double, String> {
	private final String format;
	private final int scale;

	public static Score score5() {
		return new Score("%1.2f", 5);
	}

	public static Score score10() {
		return new Score("%2.1f", 10);
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
