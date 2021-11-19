package edu.boardgames.collections.explorer.ui.text;

import java.util.stream.Stream;

public interface TextContainer extends Text {
	Stream<Text> toElements();

	@Override
	default Stream<String> toLines() {
		return toElements().flatMap(Text::toLines);
	}
}
