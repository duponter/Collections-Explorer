package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameGeek;
import io.vavr.Lazy;

public final class BggInit {
	private static final Lazy<BoardGameGeek> INSTANCE = Lazy.of(BoardGameGeekApi::new);

	public static BoardGameGeek get() {
		return INSTANCE.get();
	}

	/**
	 * Private constructor to prevent this static class from being instantiated.
	 */
	private BggInit() {
		throw new UnsupportedOperationException("This static class cannot be instantiated.");
	}
}
