package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekBuddy {
	String username();

	String name();

	List<BoardGame> ownedCollection();

	List<BoardGame> wantToPlayCollection();
}
