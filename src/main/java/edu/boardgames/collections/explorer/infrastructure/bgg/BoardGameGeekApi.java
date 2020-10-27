package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.domain.GeekLists;

public class BoardGameGeekApi implements BoardGameGeek {
	@Override
	public BoardGameCollections collections() {
		throw new UnsupportedOperationException("BoardGameGeekApi.collections");
	}

	@Override
	public GeekBuddyCollections geekBuddyCollections() {
		return new BggGeekBuddyCollections(this.boardGames());
	}

	@Override
	public GeekBuddies geekBuddies() {
		return new GeekBuddiesBggInMemory();
	}

	@Override
	public BoardGames boardGames() {
		return new BggBoardGames();
	}

	@Override
	public GeekLists geekLists() {
		return new BggGeekLists();
	}
}
