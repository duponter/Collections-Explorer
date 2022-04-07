package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.domain.GeekListCollections;
import edu.boardgames.collections.explorer.domain.GeekLists;
import edu.boardgames.collections.explorer.domain.Plays;

public class BoardGameGeekApi implements BoardGameGeek {
	@Override
	public BoardGameCollections collections() {
		throw new UnsupportedOperationException("BoardGameGeekApi.collections");
	}

	@Override
	public GeekBuddyCollections geekBuddyCollections() {
		return new BggGeekBuddyCollections();
	}

    @Override
    public GeekListCollections geekListCollections() {
        return new BggGeekListCollections();
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
		return new GeekListsBggInMemory();
	}

	@Override
	public Plays plays() {
		return new BggPlays();
	}
}
