package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.domain.GeekLists;
import edu.boardgames.collections.explorer.domain.Plays;
import edu.boardgames.collections.explorer.infrastructure.cache.BoardGameCollectionsCache;
import edu.boardgames.collections.explorer.infrastructure.cache.BoardGamesCache;
import edu.boardgames.collections.explorer.infrastructure.cache.GeekBuddyCollectionsCache;
import edu.boardgames.collections.explorer.infrastructure.cache.GeekListsCache;

public class BoardGameGeekApi implements BoardGameGeek {
    private final BoardGameCollections collections;
    private final GeekBuddyCollections geekBuddyCollections;
    private final GeekBuddies geekBuddies;
    private final BoardGames boardGames;
    private final GeekLists geekLists;
    private final Plays plays;

    public BoardGameGeekApi() {
        this.boardGames = new BoardGamesCache(new BggBoardGames());
        this.geekBuddies = new GeekBuddiesBggInMemory();
        this.geekLists = new GeekListsCache(new BggGeekLists());
        this.collections = new BoardGameCollectionsCache(this.geekBuddies, this.geekLists);
        this.plays = new BggPlays();
        this.geekBuddyCollections = new GeekBuddyCollectionsCache(new BggGeekBuddyCollections(this.boardGames()));
    }

    @Override
    public BoardGameCollections collections() {
        return collections;
    }

    @Override
    public GeekBuddyCollections geekBuddyCollections() {
        return geekBuddyCollections;
    }

    @Override
	public GeekBuddies geekBuddies() {
        return geekBuddies;
	}

	@Override
	public BoardGames boardGames() {
        return boardGames;
	}

	@Override
	public GeekLists geekLists() {
        return geekLists;
	}

	@Override
	public Plays plays() {
        return plays;
	}
}
