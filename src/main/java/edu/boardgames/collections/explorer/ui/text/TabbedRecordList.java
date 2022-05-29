package edu.boardgames.collections.explorer.ui.text;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record TabbedRecordList<T>(List<? extends T> rows, List<Column<T>> columns) implements Paragraph {
	private static final String VALUE_PREFIX = "";
	private static final String VALUE_SUFFIX = "";

	@Override
	public Stream<Text> toElements() {
		return toRows().map(Line::of);
	}

	private Stream<String> toRows() {
		return rows().stream().map(row -> toLine(col -> col.renderValue(VALUE_PREFIX, VALUE_SUFFIX, row)));
	}

	private String toLine(Function<Column<T>, String> cellPopulator) {
		return columns().stream()
						.map(cellPopulator)
						.collect(Collectors.joining("\t", "", ""))
		;
	}
}
