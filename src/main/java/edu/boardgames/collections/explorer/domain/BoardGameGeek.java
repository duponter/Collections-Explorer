package edu.boardgames.collections.explorer.domain;

public interface BoardGameGeek {
	BoardGameCollections collections();

	GeekBuddyCollections geekBuddyCollections();

	GeekBuddies geekBuddies();

	BoardGames boardGames();

	GeekLists geekLists();

	Plays plays();
}
