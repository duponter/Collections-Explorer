package edu.boardgames.collections.explorer.domain;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

public class MageKnightSoloPlayAggregate {
    private final ImmutableList<MageKnightSoloPlay> plays;

	public MageKnightSoloPlayAggregate(Collection<MageKnightSoloPlay> plays) {
        this.plays = Lists.immutable.ofAll(plays);
	}

	public MageKnightSoloPlayStats stats() {
        ImmutableBag<PlayOutcome> outcomeCounts = plays.countBy(MageKnightSoloPlay::outcome);
		return new MageKnightSoloPlayStats.Builder()
                .withCount(this.plays.size())
                .withLastPlayed(this.plays.stream().map(MageKnightSoloPlay::date).max(Comparator.naturalOrder()).orElse(LocalDate.of(2000, Month.JANUARY, 1)))
                .withWins(outcomeCounts.occurrencesOf(PlayOutcome.WIN))
                .withLosses(outcomeCounts.occurrencesOf(PlayOutcome.LOSE))
                .withIncomplete(outcomeCounts.occurrencesOf(PlayOutcome.INCOMPLETE))
				.withAvgScore(this.plays.stream().collect(Collectors.averagingInt(MageKnightSoloPlay::score)).intValue())
				.withAvgLength(this.plays.stream().collect(Collectors.averagingInt(MageKnightSoloPlay::length)).intValue())
				.build();
	}
}
