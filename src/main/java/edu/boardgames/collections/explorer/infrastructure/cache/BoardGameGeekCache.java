package edu.boardgames.collections.explorer.infrastructure.cache;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import edu.boardgames.collections.explorer.domain.GeekLists;

import java.util.Objects;

public class BoardGameGeekCache implements BoardGameGeek {
	private final BoardGameCollections collections;
	private final GeekBuddyCollections geekBuddyCollections;
	private final GeekBuddies geekBuddies;
	private final BoardGames boardGames;
	private final GeekLists geekLists;

	public BoardGameGeekCache(BoardGameGeek delegate) {
		Objects.requireNonNull(delegate);
		this.collections = delegate.collections();
		this.geekBuddyCollections = new GeekBuddyCollectionsCache(delegate.geekBuddyCollections());
		this.geekBuddies = delegate.geekBuddies();
		this.boardGames = new BoardGamesCache(delegate.boardGames());
		this.geekLists = new GeekListsCache(delegate.geekLists());
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
}
