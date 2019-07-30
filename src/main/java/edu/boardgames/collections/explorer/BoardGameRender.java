package edu.boardgames.collections.explorer;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;

public class BoardGameRender {
	static String playInfo(BoardGame boardGame) {
		Range<Integer> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElse(boardGame.playerCount().map(Integer::parseInt));

		return String.format("%s (%s) - %s>%sp - %s Min - %2.1f / 10 - %1.2f / 5", boardGame.name(), boardGame.year(), boardGame.playerCount().formatted(), communityPlayerCount.formatted(), boardGame.playtime().formatted(), boardGame.bggScore(), boardGame.averageWeight());
	}
}
