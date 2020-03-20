package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;

public class BoardGameGeekApi implements BoardGameGeek {
	@Override
	public BoardGameCollections collections() {
		return new BggCollections();
	}

	@Override
	public GeekBuddies geekBuddies() {
		return new GeekBuddiesBggInMemory();
	}

	@Override
	public BoardGames boardGames() {
		return new BggBoardGames();
	}
}
