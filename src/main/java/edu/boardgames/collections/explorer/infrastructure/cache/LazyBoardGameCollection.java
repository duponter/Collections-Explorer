package edu.boardgames.collections.explorer.infrastructure.cache;

import static java.util.Objects.requireNonNull;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import io.vavr.Lazy;

import java.util.List;

public final class LazyBoardGameCollection implements BoardGameCollection {
	private final String id;
	private final String name;
	private final Lazy<List<BoardGame>> boardGames;

	LazyBoardGameCollection(String id, String name, Lazy<List<BoardGame>> boardGames) {
		this.id = requireNonNull(id);
		this.name = requireNonNull(name);
		this.boardGames = requireNonNull(boardGames);
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<BoardGame> boardGames() {
		return boardGames.get();
	}
}
