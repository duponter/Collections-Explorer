package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Copy {
	private final BoardGame boardGame;
	private final GeekBuddy owner;

	public static List<Copy> from(GeekBuddy owner, List<BoardGame> boardGames) {
		return boardGames.stream()
				.map(bg -> new Copy(bg, owner))
				.collect(Collectors.toList());
	}

	public Copy(BoardGame boardGame, GeekBuddy owner) {
		this.boardGame = Objects.requireNonNull(boardGame);
		this.owner = Objects.requireNonNull(owner);
	}

	public BoardGame boardGame() {
		return boardGame;
	}

	public GeekBuddy owner() {
		return owner;
	}
}
