package edu.boardgames.collections.explorer.ui.text;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public record ChapterTitle(String value) implements Text {
	@Override
	public Stream<String> toLines() {
		return Stream.of(value, StringUtils.repeat('-', StringUtils.length(value) + StringUtils.countMatches(value, '\t') * 5));
	}
}
