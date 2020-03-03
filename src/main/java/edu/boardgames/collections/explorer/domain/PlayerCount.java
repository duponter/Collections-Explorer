package edu.boardgames.collections.explorer.domain;

import org.apache.commons.lang3.Validate;

public class PlayerCount {
	private final int count;

	public PlayerCount(int count) {
		Validate.isTrue(count > 0, "RecommendedPlayerCount requires a positive number: %d is not greater than 0", count);
		this.count = count;
	}

	public boolean test(BoardGame boardGame) {
		return boardGame.playerCount().contains(Integer.toString(count));
	}

	public boolean recommendedOnly(BoardGame boardGame) {
		return bestOnly(boardGame) || boardGame.recommendedWithPlayerCount()
				.filter(this::inRange)
				.isPresent();
	}

	public boolean bestOnly(BoardGame boardGame) {
		return boardGame.bestWithPlayerCount()
				.filter(this::inRange)
				.isPresent();
	}

	private boolean inRange(Range<String> range) {
		return range.contains(Integer.toString(count));
	}
}
