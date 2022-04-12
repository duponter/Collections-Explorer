package edu.boardgames.collections.explorer.ui.text;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public record LinesParagraph(Collection<? extends Line> lines) implements Paragraph {
	public LinesParagraph(Line... lines) {
		this(List.of(lines));
	}

	@Override
	public Stream<Text> toElements() {
		return lines.stream().map(Line.class::cast);
	}
}
