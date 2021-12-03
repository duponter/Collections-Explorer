package edu.boardgames.collections.explorer.ui.input;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.ui.text.format.OwnedBoardGameFormat;

public class OwnedBoardGameFormatInput implements Input<OwnedBoardGameFormat> {
	private final String name;

	public OwnedBoardGameFormatInput(String name) {
		this.name = StringUtils.upperCase(name);
	}

	@Override
	public String asText() {
		return "rendered in %s format".formatted(this.name);
	}

	@Override
	public OwnedBoardGameFormat resolve() {
		return OwnedBoardGameFormat.valueOf(this.name);
	}
}
