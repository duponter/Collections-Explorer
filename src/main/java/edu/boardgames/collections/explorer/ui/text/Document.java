package edu.boardgames.collections.explorer.ui.text;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record Document(DocumentTitle title, Collection<? extends TextContainer> content) implements TextContainer {
	public Document(DocumentTitle title, TextContainer... content) {
		this(title, List.of(content));
	}

	@Override
	public Stream<Text> toElements() {
		return Stream.concat(
				Stream.of(title, Line.EMPTY),
				content().stream().flatMap(container -> Stream.of(container, Line.EMPTY))
		);
	}
}
