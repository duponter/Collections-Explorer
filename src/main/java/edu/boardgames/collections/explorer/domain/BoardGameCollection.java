package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface BoardGameCollection {
	String name();

	List<BoardGame> boardGames();
}
