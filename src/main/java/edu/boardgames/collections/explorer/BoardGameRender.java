package edu.boardgames.collections.explorer;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlayerCountPoll;
import edu.boardgames.collections.explorer.ui.text.format.Score;

public final class BoardGameRender {
	/**
	 * Private constructor to prevent this static class from being instantiated.
	 */
	private BoardGameRender() {
		throw new UnsupportedOperationException("This static class cannot be instantiated.");
	}

	static String playInfo(BoardGame boardGame) {
		Range<String> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElseGet(boardGame::playerCount);
		return String.join(" - ",
				"%s (%s)".formatted(boardGame.name(), boardGame.year()),
				"%s>%sp".formatted(boardGame.playerCount().formatted(), communityPlayerCount.formatted()),
				"%s min".formatted(boardGame.playtime().formatted()),
				"%2.1f / 10".formatted(boardGame.bggScore()),
				"%1.2f / 5".formatted(boardGame.averageWeight())
		);
	}

	static String ranking(BoardGame boardGame, String owners, int playerCount) {
		Score percentage = Score.percentage();
		Optional<PlayerCountPoll> playerCountPoll = boardGame.playerCountVotes(playerCount);
		double playerCountVoteCount = playerCountPoll
				.map(poll -> poll.bestVotes() + poll.recommendedVotes() + poll.notRecommendedVotes())
				.orElse(1) / 100.0;
		return String.join("\t",
				"%-70s".formatted(StringUtils.abbreviate(boardGame.name(), 70)),
				boardGame.year(),
				String.format("%5sp", boardGame.playerCount().formatted()),
				String.format("%7s min", boardGame.playtime().formatted()),
				Score.score5().apply(boardGame.averageWeight()),
				Score.score10().apply(boardGame.bggScore()),
				String.format("%d", boardGame.playerCountTotalVoteCount()),
				percentage.apply(playerCountPoll.map(PlayerCountPoll::bestVotes).orElse(0).doubleValue() / playerCountVoteCount),
				percentage.apply(playerCountPoll.map(PlayerCountPoll::recommendedVotes).orElse(0).doubleValue() / playerCountVoteCount),
				percentage.apply(playerCountPoll.map(PlayerCountPoll::notRecommendedVotes).orElse(0).doubleValue() / playerCountVoteCount),
				owners
		);
	}
}
