package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Objects;

public final class BoardGameCollection {
	private final GeekBuddy owner;
	private final List<BoardGame> boardGames;

	public BoardGameCollection(GeekBuddy owner, List<BoardGame> boardGames) {
		this.owner = Objects.requireNonNull(owner);
		this.boardGames = Objects.requireNonNull(boardGames);
	}

	public GeekBuddy owner() {
		return owner;
	}

	public List<BoardGame> boardGames() {
		return boardGames;
	}
}
