package edu.boardgames.collections.explorer.ui.text;

import java.util.stream.Stream;

public record Line(String value) implements Text {
	public static Line EMPTY = new Line("");

	@Override
	public Stream<String> toLines() {
		return Stream.of(value);
	}
}
