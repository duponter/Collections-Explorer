package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;

import java.lang.System.Logger.Level;

public final class BoardGameRender {
	private static final System.Logger LOGGER = System.getLogger(BoardGameRender.class.getName());

	/**
	 * Private constructor to prevent this static class from being instantiated.
	 */
	private BoardGameRender() {
		throw new UnsupportedOperationException("This static class cannot be instantiated.");
	}

	static String playInfo(BoardGame boardGame) {
		LOGGER.log(Level.INFO, String.format("Rendering BoardGame %s", boardGame.name()));
		Range<String> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElseGet(boardGame::playerCount);
		return String.format("%s (%s) - %s>%sp - %s Min - %2.1f / 10 - %1.2f / 5", boardGame.name(), boardGame.year(), boardGame.playerCount().formatted(), communityPlayerCount.formatted(), boardGame.playtime().formatted(), boardGame.bggScore(), boardGame.averageWeight());
	}

	static String playInfo(BoardGame boardGame, String owners) {
		return String.format("%s owned by %s", playInfo(boardGame), owners);
	}
}
