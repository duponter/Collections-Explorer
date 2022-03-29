package edu.boardgames.collections.explorer.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class BoardGameCollectionGroup implements BoardGameCollection {
    private final String id;
	private final String name;
	private final BoardGameCollection[] groupedCollections;

	public BoardGameCollectionGroup(String name, BoardGameCollection... groupedCollections) {
		this.name = Objects.requireNonNull(name);
        this.id = "GROUP[%s]".formatted(this.name);
		this.groupedCollections = groupedCollections;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<BoardGame> boardGames() {
		return Arrays.stream(this.groupedCollections)
				.map(BoardGameCollection::boardGames)
				.flatMap(Collection::stream)
				.toList();
	}

	@Override
	public Stream<Copy> copyStream() {
		return Arrays.stream(this.groupedCollections)
				.flatMap(BoardGameCollection::copyStream);
	}
}
