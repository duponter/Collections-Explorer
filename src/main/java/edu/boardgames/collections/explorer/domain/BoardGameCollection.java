package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface BoardGameCollection {
	String id();

	String name();

	List<BoardGame> boardGames();

	default List<Copy> boardGameCopies() {
		return this.boardGames().stream()
				.map(bg -> new Copy(bg, this))
				.toList();
	}
}
