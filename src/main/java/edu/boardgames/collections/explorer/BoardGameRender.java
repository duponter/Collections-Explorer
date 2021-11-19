package edu.boardgames.collections.explorer;

import java.lang.System.Logger.Level;
import java.util.Locale;
import java.util.Optional;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.Range;
import edu.boardgames.collections.explorer.infrastructure.bgg.PlayerCountPoll;

public final class BoardGameRender {
	private static final System.Logger LOGGER = System.getLogger(BoardGameRender.class.getName());

	/**
	 * Private constructor to prevent this static class from being instantiated.
	 */
	private BoardGameRender() {
		throw new UnsupportedOperationException("This static class cannot be instantiated.");
	}

	static String playInfo(BoardGame boardGame) {
		LOGGER.log(Level.DEBUG, String.format("Rendering BoardGame %s", boardGame.name()));
		Range<String> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElseGet(boardGame::playerCount);
		return String.format("%s (%s) - %s>%sp - %s Min - %2.1f / 10 - %1.2f / 5", boardGame.name(), boardGame.year(), boardGame.playerCount().formatted(), communityPlayerCount.formatted(), boardGame.playtime().formatted(), boardGame.bggScore(), boardGame.averageWeight());
	}

	static String slimInfo(BoardGame boardGame) {
		String formatted = String.format("%s (%s) - %2.1f / 10", boardGame.name(), boardGame.year(), boardGame.bggScore());
		LOGGER.log(Level.DEBUG, String.format("\\[Render] %s", formatted));
		return formatted;
	}

	static String playInfo(BoardGame boardGame, String owners) {
		return String.format("%s owned by %s", playInfo(boardGame), owners);
	}

	static String tabularPlayInfo(BoardGame boardGame, String owners) {
		Range<String> communityPlayerCount = boardGame.bestWithPlayerCount()
				.or(boardGame::recommendedWithPlayerCount)
				.orElseGet(boardGame::playerCount);
		return String.join("\t",
				"%-100s".formatted(boardGame.name()),
				boardGame.year(),
				"%4s>%-4s".formatted(boardGame.playerCount().formatted(), communityPlayerCount.formatted()),
				"%7s Min".formatted(boardGame.playtime().formatted()),
				"%2.1f / 10".formatted(boardGame.bggScore()),
				"%1.2f / 5".formatted(boardGame.averageWeight()),
				owners);
	}

	static String ranking(BoardGame boardGame, String owners, int playerCount) {
		LOGGER.log(Level.DEBUG, String.format("Rendering BoardGame %s", boardGame.name()));

		Optional<PlayerCountPoll> playerCountPoll = boardGame.playerCountVotes(playerCount);
		double playerCountVoteCount = playerCountPoll
				.map(poll -> poll.bestVotes() + poll.recommendedVotes() + poll.notRecommendedVotes())
				.orElse(1) / 100.0;
		return String.join(";",
				String.format("%s (%s)", boardGame.name(), boardGame.year()),
				String.format("%sp", boardGame.playerCount().formatted()),
				String.format("%s min", boardGame.playtime().formatted()),
				String.format(Locale.FRENCH, "%1.2f", boardGame.averageWeight()),
				String.format(Locale.FRENCH, "%2.1f", boardGame.bggScore()),
				String.format("%d", boardGame.playerCountTotalVoteCount()),
				String.format(Locale.FRENCH, "%1.2f", playerCountPoll.map(PlayerCountPoll::bestVotes).orElse(0).doubleValue() / playerCountVoteCount),
				String.format(Locale.FRENCH, "%1.2f", playerCountPoll.map(PlayerCountPoll::recommendedVotes).orElse(0).doubleValue() / playerCountVoteCount),
				String.format(Locale.FRENCH, "%1.2f", playerCountPoll.map(PlayerCountPoll::notRecommendedVotes).orElse(0).doubleValue() / playerCountVoteCount),
				owners
		);
	}
}
