package edu.boardgames.collections.explorer.domain.poll;

import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.StringJoiner;

public final class Answer<T> {
	private final T value;
	private final int votes;

	public Answer(T value, int votes) {
		this.value = Objects.requireNonNull(value, "Answer value should not be null");
		Validate.isTrue(votes >= 0, "Votes should be positive or zero, which makes %d invalid", votes);
		this.votes = votes;
	}

	public double ratio(int totalVotes) {
		Validate.isTrue(totalVotes >= this.votes, "TotalVotes %d should be greater or equal to Votes %d", totalVotes, votes);
		return this.votes == 0 ? 0 : (this.votes / (double) totalVotes);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", Answer.class.getSimpleName() + "[", "]")
				.add(String.format("value=%s", value))
				.add(String.format("votes=%s", votes))
				.toString();
	}
}
