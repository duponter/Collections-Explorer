package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Comparator;
import java.util.StringJoiner;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;

public class PlayerCountPoll {
	private final int bestVotes;
	private final int recommendedVotes;
	private final int notRecommendedVotes;

	protected PlayerCountPoll(RichIterable<? extends PlayerCountVotes> votes) {
		if (votes.size() != 3) {
			throw new IllegalArgumentException("Expecting only 3 vote elements: %s".formatted(String.join(", ", votes.collect(PlayerCountVotes::toString))));
		}
		MutableList<? extends PlayerCountVotes> sorted = votes.toSortedList(Comparator.comparing(PlayerCountVotes::poll));
		this.bestVotes = sorted.getFirst().voteCount();
		this.recommendedVotes = sorted.getLast().voteCount();
		this.notRecommendedVotes = sorted.get(1).voteCount();
	}

	public int bestVotes() {
		return bestVotes;
	}

	public int recommendedVotes() {
		return recommendedVotes;
	}

	public int notRecommendedVotes() {
		return notRecommendedVotes;
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
