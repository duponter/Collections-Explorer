package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Objects;

import edu.boardgames.collections.explorer.infrastructure.bgg.BggInit;

public final class GeekBuddyCollection implements BoardGameCollection {
	private final String id;
    private final String name;
	private final List<CollectedBoardGame> boardGames;

    public GeekBuddyCollection(String id, String name, List<CollectedBoardGame> boardGames) {
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
	public List<BoardGame> boardGames() {
        return BggInit.get().boardGames().withIds(boardGames.stream().map(CollectedBoardGame::id));
	}
}
