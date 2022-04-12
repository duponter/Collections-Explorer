package edu.boardgames.collections.explorer.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.StringJoiner;

import io.vavr.Lazy;

public class MageKnightSoloPlay {
	private final Play play;
	private final Lazy<MageKnightSoloPlayComments> comments;

	public MageKnightSoloPlay(Play play) {
		this.play = Objects.requireNonNull(play);
		this.comments = Lazy.of(() -> new MageKnightSoloPlayComments(play.comments()));
	}

	public LocalDate date() {
		return play.date();
	}

	public int length() {
		return play.length();
	}

	public PlayOutcome outcome() {
		if (play.incomplete()) {
			return PlayOutcome.INCOMPLETE;
		}
		return player().win() ? PlayOutcome.WIN : PlayOutcome.LOSE;
	}

	public int score() {
		return (int) player().score();
	}

	private Player player() {
		return play.players().get(0);
	}

	public String scenario() {
		return comments.get().scenario();
	}

	public String mageKnight() {
		return comments.get().mageKnight();
	}

	public String dummyPlayer() {
		return comments.get().dummyPlayer();
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", MageKnightSoloPlay.class.getSimpleName() + "[", "]")
				.add("date=" + this.date())
				.add("length=" + this.length())
				.add("outcome=" + this.outcome())
				.add("score=" + this.score())
				.add("scenario=" + this.scenario())
				.add("mageKnight=" + this.mageKnight())
				.add("dummyPlayer=" + this.dummyPlayer())
				.toString();
	}
}
