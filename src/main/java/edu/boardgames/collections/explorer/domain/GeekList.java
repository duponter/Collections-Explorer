package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekList {
	String id();

	String name();

	List<BoardGame> boardGames();
}
