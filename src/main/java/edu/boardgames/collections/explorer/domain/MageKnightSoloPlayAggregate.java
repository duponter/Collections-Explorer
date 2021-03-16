package edu.boardgames.collections.explorer.domain;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class MageKnightSoloPlayAggregate {
	private final Collection<MageKnightSoloPlay> plays;

	public MageKnightSoloPlayAggregate(Collection<MageKnightSoloPlay> plays) {
		this.plays = plays;
	}

	public Map<String, Map<String, MageKnightSoloPlayAggregate>> group() {
		return this.plays.stream()
				.collect(Collectors.groupingBy(
						MageKnightSoloPlay::scenario,
						Collectors.groupingBy(
								MageKnightSoloPlay::mageKnight,
								Collectors.collectingAndThen(
										Collectors.toList(),
										MageKnightSoloPlayAggregate::new
								)
						)
				));
	}

	public MageKnightSoloPlayStats stats() {
		Map<PlayOutcome, Long> outcomeCounts = this.plays.stream().collect(Collectors.groupingBy(MageKnightSoloPlay::outcome, Collectors.counting()));
		return new MageKnightSoloPlayStats.Builder()
				.withCount(this.plays.size())
				.withLastPlayed(this.plays.stream().map(MageKnightSoloPlay::date).max(Comparator.naturalOrder()).orElse(null))
				.withWins(outcomeCounts.getOrDefault(PlayOutcome.WIN, 0L).intValue())
				.withLosses(outcomeCounts.getOrDefault(PlayOutcome.LOSE, 0L).intValue())
				.withIncomplete(outcomeCounts.getOrDefault(PlayOutcome.INCOMPLETE, 0L).intValue())
				.withAvgScore(this.plays.stream().collect(Collectors.averagingInt(MageKnightSoloPlay::score)).intValue())
				.withAvgLength(this.plays.stream().collect(Collectors.averagingInt(MageKnightSoloPlay::length)).intValue())
				.build();
	}
}
