package edu.boardgames.collections.explorer.infrastructure.cache;

import java.util.Objects;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.domain.GeekLists;
import edu.boardgames.collections.explorer.domain.Plays;

public class BoardGameGeekCache implements BoardGameGeek {
	private final BoardGameCollections collections;
	private final GeekBuddyCollections geekBuddyCollections;
	private final GeekBuddies geekBuddies;
	private final BoardGames boardGames;
	private final GeekLists geekLists;
	private final Plays plays;

	public BoardGameGeekCache(BoardGameGeek delegate) {
		Objects.requireNonNull(delegate);
		this.geekBuddyCollections = new GeekBuddyCollectionsCache(delegate.geekBuddyCollections());
		this.geekBuddies = delegate.geekBuddies();
		this.boardGames = new BoardGamesCache(delegate.boardGames());
		this.geekLists = new GeekListsCache(delegate.geekLists());
		this.collections = new BoardGameCollectionsCache(this.geekBuddies, this.geekLists);
		this.plays = delegate.plays();
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
