package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Objects;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

public final class DetailedBoardGameCollection implements BoardGameCollection {
	private final String id;
    private final String name;
	private final ImmutableList<CollectedBoardGame> boardGames;

    public DetailedBoardGameCollection(String id, String name, List<CollectedBoardGame> boardGames) {
		this(id, name, Lists.immutable.withAll(boardGames));
	}

    public DetailedBoardGameCollection(String id, String name, ImmutableList<CollectedBoardGame> boardGames) {
		this.id = Objects.requireNonNull(id);
		this.name = Objects.requireNonNull(name);
        this.boardGames = Objects.requireNonNull(boardGames);
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
        return this.boardGames;
    }
}
