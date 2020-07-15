package edu.boardgames.collections.explorer.domain;

public interface GeekBuddyCollections {
	BoardGameCollection owned(GeekBuddy geekBuddy);

	BoardGameCollection wantToPlay(GeekBuddy geekBuddy);
}
