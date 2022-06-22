package edu.boardgames.collections.explorer.domain.poll;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

public record VotesPercentage(String value, String playerCount, Double percentage) implements Comparable<VotesPercentage> {
    private static final Comparator<VotesPercentage> COMPARATOR = Comparator.comparing(VotesPercentage::percentage).reversed()
        .thenComparing(VotesPercentage::value)
        .thenComparing(VotesPercentage::playerCount);

    public boolean isBest() {
        return StringUtils.equals(value(), "Best");
    }

    public boolean isRecommended() {
        return StringUtils.equals(value(), "Recommended");
    }

    public boolean isNotRecommended() {
        return StringUtils.equals(value(), "Not Recommended");
    }

    @Override
    public int compareTo(VotesPercentage other) {
        return COMPARATOR.compare(this, other);
    }
}
