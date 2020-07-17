package edu.boardgames.collections.explorer.infrastructure.bgg;

import io.vavr.collection.Stream;

import java.util.Comparator;
import java.util.StringJoiner;

public class PlayerCountPoll {
	private final int bestVotes;
	private final int recommendedVotes;
	private final int notRecommendedVotes;

	protected PlayerCountPoll(Stream<? extends PlayerCountVotes> votes) {
		if (votes.length() != 3) {
			throw new IllegalArgumentException(String.format("Expecting only 3 vote elements: %s", String.join(", ", votes.map(PlayerCountVotes::toString))));
		}
		Stream<? extends PlayerCountVotes> sorted = votes.sorted(Comparator.comparing(PlayerCountVotes::poll));
		this.bestVotes = sorted.head().voteCount();
		this.recommendedVotes = sorted.tail().last().voteCount();
		this.notRecommendedVotes = sorted.tail().head().voteCount();
	}

	public PlayerCountPollChoice result() {
		if (bestVotes >= 5 && bestVotes >= recommendedVotes && bestVotes >= notRecommendedVotes) {
			return PlayerCountPollChoice.BEST;
		} else if (recommendedVotes >= 5 && recommendedVotes >= notRecommendedVotes) {
			return PlayerCountPollChoice.RECOMMENDED;
		} else {
			return PlayerCountPollChoice.NOT_RECOMMENDED;
		}
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", PlayerCountPoll.class.getSimpleName() + "[", "]")
				.add(String.format("bestVotes=%s", bestVotes))
				.add(String.format("recommendedVotes=%s", recommendedVotes))
				.add(String.format("notRecommendedVotes=%s", notRecommendedVotes))
				.add(String.format("result=%s", result()))
				.toString();
	}
}
