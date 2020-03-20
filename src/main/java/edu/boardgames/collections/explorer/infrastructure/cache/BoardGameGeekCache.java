package edu.boardgames.collections.explorer.infrastructure.cache;

import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.GeekBuddies;

import java.util.Objects;

public class BoardGameGeekCache implements BoardGameGeek {
	private final BoardGameCollections collections;
	private final GeekBuddies geekBuddies;
	private final BoardGames boardGames;

	public BoardGameGeekCache(BoardGameGeek delegate) {
		Objects.requireNonNull(delegate);
		this.collections = new CollectionsCache(delegate.collections());
		this.geekBuddies = delegate.geekBuddies();
		this.boardGames = new BoardGamesCache(delegate.boardGames());
	}

	@Override
	public BoardGameCollections collections() {
		return collections;
	}

	@Override
	public GeekBuddies geekBuddies() {
		return geekBuddies;
	}

	@Override
	public BoardGames boardGames() {
		return boardGames;
	}
}
