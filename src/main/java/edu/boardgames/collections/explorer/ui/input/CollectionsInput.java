package edu.boardgames.collections.explorer.ui.input;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;

public class CollectionsInput implements Input<BoardGameCollection> {
	private final BoardGameCollection collection;

	public CollectionsInput(String collections) {
        this(StringUtils.split(collections, ","));
	}

	public CollectionsInput(List<String> collections) {
        this(collections.stream().flatMap(s -> Arrays.stream(StringUtils.split(s, ","))).toArray(String[]::new));
	}

    private CollectionsInput(String... collections) {
        this.collection = BggInit.get().collections().withNames(collections);
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
