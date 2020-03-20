package edu.boardgames.collections.explorer.domain;

public interface BoardGameCollections {
	BoardGameCollection owned(GeekBuddy geekBuddy);

	BoardGameCollection wantToPlay(GeekBuddy geekBuddy);
}
