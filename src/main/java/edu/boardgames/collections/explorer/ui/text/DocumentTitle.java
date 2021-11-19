package edu.boardgames.collections.explorer.ui.text;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public record DocumentTitle(String value) implements Text {
	@Override
	public Stream<String> toLines() {
		return Stream.of(value, StringUtils.repeat('=', StringUtils.length(value)));
	}
}
