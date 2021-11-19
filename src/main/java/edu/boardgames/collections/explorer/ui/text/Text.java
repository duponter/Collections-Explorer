package edu.boardgames.collections.explorer.ui.text;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Text {
	Stream<String> toLines();

	default String toText() {
		return toLines().collect(Collectors.joining(System.lineSeparator()));
	}
}
