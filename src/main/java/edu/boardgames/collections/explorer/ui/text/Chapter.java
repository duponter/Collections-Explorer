package edu.boardgames.collections.explorer.ui.text;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record Chapter(ChapterTitle title, Collection<Paragraph> paragraphs) implements TextContainer {
	public Chapter(ChapterTitle title, Paragraph... paragraphs) {
		this(title, List.of(paragraphs));
	}

	@Override
	public Stream<Text> toElements() {
		return Stream.concat(
				Stream.of(title, Line.EMPTY),
				paragraphs().stream().flatMap(paragraph -> Stream.of(paragraph, Line.EMPTY))
		);
	}
}
