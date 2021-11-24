package edu.boardgames.collections.explorer.ui.text;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public interface Line extends Text {
	Line EMPTY = Line.of(StringUtils.EMPTY);

	static Line of(String value) {
		return () -> value;
	}

	String line();

	@Override
	default Stream<String> toLines() {
		return Stream.of(line());
	}

	@Override
	default String toText() {
		return line();
	}
}
