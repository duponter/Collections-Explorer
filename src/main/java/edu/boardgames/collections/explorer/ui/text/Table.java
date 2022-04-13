package edu.boardgames.collections.explorer.ui.text;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Table<T>(List<T> rows, List<Column<T>> columns) implements Paragraph {
	private static final String VALUE_PREFIX = " ";
	private static final String VALUE_SUFFIX = " |";

	@Override
	public Stream<Text> toElements() {
		return Stream.of(toHeaderElement(), toHeaderLine(), toRows())
				.flatMap(Function.identity())
				.map(Line::of);
	}

	private Stream<String> toHeaderElement() {
		return Stream.of(toLine(col -> col.renderHeader(VALUE_PREFIX, VALUE_SUFFIX)));
	}

	private Stream<String> toHeaderLine() {
		return Stream.of(toLine(col -> col.renderLine(VALUE_PREFIX, VALUE_SUFFIX)));
	}

	private Stream<String> toRows() {
		return rows().stream().map(row -> toLine(col -> col.renderValue(VALUE_PREFIX, VALUE_SUFFIX, row)));
	}

	private String toLine(Function<Column<T>, String> cellPopulator) {
		return columns().stream()
						.map(cellPopulator)
						.collect(Collectors.joining("", "|", ""))
		;
	}
}
