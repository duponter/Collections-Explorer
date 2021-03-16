package edu.boardgames.collections.explorer.domain;

import java.time.LocalDate;
import java.time.Month;

public class MageKnightSoloPlayStats {
	private final String mageKnight;
	private final int count;
	private final int wins;
	private final int losses;
	private final int incomplete;
	private final LocalDate lastPlayed;
	private final int avgScore;
	private final int avgLength;

	private MageKnightSoloPlayStats(Builder builder) {
		this.mageKnight = builder.mageKnight;
		this.count = builder.count;
		this.wins = builder.wins;
		this.losses = builder.losses;
		this.incomplete = builder.incomplete;
		this.lastPlayed = builder.lastPlayed;
		this.avgScore = builder.avgScore;
		this.avgLength = builder.avgLength;
	}

	public String formatted() {
		return String.format(
				"%1$2d plays (%2$2d W, %3$2d L, %4$2d I) - %5$te-%5$tb-%5$tY - %6$3d pts - %7$3d mins",
				this.count(),
				this.wins(),
				this.losses(),
				this.incomplete(),
				this.lastPlayed(),
				this.avgScore(),
				this.avgLength());
	}

	public String mageKnight() {
		return mageKnight;
	}

	public int count() {
		return count;
	}

	public int wins() {
		return wins;
	}

	public int losses() {
		return losses;
	}

	public int incomplete() {
		return incomplete;
	}

	public LocalDate lastPlayed() {
		return lastPlayed;
	}

	public int avgScore() {
		return avgScore;
	}

	public int avgLength() {
		return avgLength;
	}

	public static final class Builder {
		private String mageKnight;
		private int count;
		private int wins;
		private int losses;
		private int incomplete;
		private LocalDate lastPlayed = LocalDate.of(2000, Month.JANUARY, 1);
		private int avgScore;
		private int avgLength;

		public Builder() {
		}

		public Builder(MageKnightSoloPlay play) {
			this.withMageKnight(play.mageKnight())
					.withCount(1)
					.withLastPlayed(play.date())
					.withAvgLength(play.length())
					.withWins(play.outcome() == PlayOutcome.WIN ? 1 : 0)
					.withLosses(play.outcome() == PlayOutcome.LOSE ? 1 : 0)
					.withIncomplete(play.outcome() == PlayOutcome.INCOMPLETE ? 1 : 0)
					.withAvgScore(play.score());
		}

		public Builder withMageKnight(String mageKnight) {
			this.mageKnight = mageKnight;
			return this;
		}

		public Builder withCount(int count) {
			this.count = count;
			return this;
		}

		public Builder withLastPlayed(LocalDate lastPlayed) {
			this.lastPlayed = lastPlayed;
			return this;
		}

		public Builder withAvgLength(int avgLength) {
			this.avgLength = avgLength;
			return this;
		}

		public Builder withWins(int wins) {
			this.wins = wins;
			return this;
		}

		public Builder withLosses(int losses) {
			this.losses = losses;
			return this;
		}

		public Builder withIncomplete(int incomplete) {
			this.incomplete = incomplete;
			return this;
		}

		public Builder withAvgScore(int avgScore) {
			this.avgScore = avgScore;
			return this;
		}

		public MageKnightSoloPlayStats build() {
			return new MageKnightSoloPlayStats(this);
		}
	}
}
