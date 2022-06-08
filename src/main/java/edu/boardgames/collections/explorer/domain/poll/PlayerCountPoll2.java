package edu.boardgames.collections.explorer.domain.poll;


import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import edu.boardgames.collections.explorer.domain.Range;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.ordered.OrderedIterable;

public final class PlayerCountPoll2 {
    private final Poll<PlayerCountPollResult> pollResults;

    public PlayerCountPoll2(Poll<PlayerCountPollResult> pollResults) {
        this.pollResults = Objects.requireNonNull(pollResults);
    }

    public Optional<Range<String>> bestOnly() {
        return mostVoted(
            asPercentageList().select(VotesPercentage::isBest)
        );
    }

    public Optional<Range<String>> recommended() {
        return mostVoted(
            asPercentageList().groupBy(VotesPercentage::playerCount)
                .multiValuesView()
                .collect(votes -> new VotesPercentage("Recommended", votes.getAny().playerCount(), votes.sumOfDouble(VotesPercentage::percentage)), Lists.mutable.empty())
        );
    }

    private ImmutableList<VotesPercentage> asPercentageList() {
        return pollResults.results()
            .reject(p -> p.votes() < 5)
            .collect(r -> new VotesPercentage(r.value(), r.numberOfPlayers(), ((double) r.votes() / pollResults.totalVotes())))
            .reject(VotesPercentage::isNotRecommended)
            .toImmutableSortedListBy(VotesPercentage::percentage);
    }

    private Optional<Range<String>> mostVoted(OrderedIterable<VotesPercentage> votes) {
        VotesPercentage last = votes.getLast();
        return Range.of(
            votes.toMap(VotesPercentage::playerCount, t -> t.percentage() / last.percentage())
                .select((k, v) -> v >= 0.9)
                .keySet()
        );
    }

    private record VotesPercentage(String value, String playerCount, Double percentage) implements Comparable<VotesPercentage> {
        private static final Comparator<VotesPercentage> COMPARATOR = Comparator.comparing(VotesPercentage::percentage).reversed()
            .thenComparing(VotesPercentage::value)
            .thenComparing(VotesPercentage::playerCount);

        private boolean isBest() {
            return StringUtils.equals(value(), "Best");
        }

        private boolean isNotRecommended() {
            return StringUtils.equals(value(), "Not Recommended");
        }

        @Override
        public int compareTo(VotesPercentage other) {
            return COMPARATOR.compare(this, other);
        }
    }
}
