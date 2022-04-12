package edu.boardgames.collections.explorer.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

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
    public ImmutableList<CollectedBoardGame> boardGames() {
        return Lists.immutable.of(groupedCollections)
                .flatCollect(BoardGameCollection::boardGames);
    }

    @Override
    public BoardGameCollection withName(String name) {
        return new BoardGameCollectionGroup(name, this.groupedCollections);
    }

    /**
     * Keep this method to render the collection names correctly.
     *
     * @return a stream of the grouped collections
     */
    @Override
    public Stream<Copy> copyStream() {
        return Arrays.stream(this.groupedCollections)
                .flatMap(BoardGameCollection::copyStream);
    }
}
