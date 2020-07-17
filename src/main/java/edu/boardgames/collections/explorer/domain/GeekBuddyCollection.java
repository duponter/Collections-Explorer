package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Objects;

public final class GeekBuddyCollection implements BoardGameCollection {
	private final GeekBuddy owner;
	private final List<BoardGame> boardGames;

	public GeekBuddyCollection(GeekBuddy owner, List<BoardGame> boardGames) {
		this.owner = Objects.requireNonNull(owner);
		this.boardGames = Objects.requireNonNull(boardGames);
	}

	@Override
	public String id() {
		return owner.username();
	}

	@Override
	public String name() {
		return owner.name();
	}

	@Override
	public List<BoardGame> boardGames() {
		return boardGames;
	}
}
