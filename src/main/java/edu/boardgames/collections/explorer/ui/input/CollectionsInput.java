package edu.boardgames.collections.explorer.ui.input;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;

public class CollectionsInput implements Input<BoardGameCollection> {
	private final BoardGameCollection collection;

	public CollectionsInput(String collections) {
		this.collection = BggInit.get().collections().withNames(StringUtils.split(collections, ","));
	}

	@Override
	public String asText() {
		return collection.name();
	}

	@Override
	public BoardGameCollection resolve() {
		return collection;
	}
}
