package edu.boardgames.collections.explorer.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BoardGameCollectionGroup implements BoardGameCollection {
	private final String name;
	private final BoardGameCollection[] groupedCollections;

	public BoardGameCollectionGroup(String name, BoardGameCollection... groupedCollections) {
		this.name = Objects.requireNonNull(name);
		this.groupedCollections = groupedCollections;
	}

	@Override
	public String id() {
		return String.format("GROUP[%s]", name);
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
		             .collect(Collectors.toList());
	}

	@Override
	public List<Copy> boardGameCopies() {
		return Arrays.stream(this.groupedCollections)
		             .map(BoardGameCollection::boardGameCopies)
		             .flatMap(Collection::stream)
		             .collect(Collectors.toList());
	}
}
