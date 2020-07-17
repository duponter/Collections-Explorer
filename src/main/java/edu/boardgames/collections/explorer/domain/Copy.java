package edu.boardgames.collections.explorer.domain;

import java.util.Objects;

public final class Copy {
	private final BoardGame boardGame;
	private final BoardGameCollection collection;

	public Copy(BoardGame boardGame, BoardGameCollection collection) {
		this.boardGame = Objects.requireNonNull(boardGame);
		this.collection = Objects.requireNonNull(collection);
	}

	public BoardGame boardGame() {
		return boardGame;
	}

	public BoardGameCollection collection() {
		return collection;
	}
}
