package edu.boardgames.collections.explorer.domain;

public interface BoardGameGeek {
	BoardGameCollections collections();

	GeekBuddyCollections geekBuddyCollections();

	GeekListCollections geekListCollections();

	GeekBuddies geekBuddies();

	BoardGames boardGames();

	GeekLists geekLists();

	Plays plays();
}
