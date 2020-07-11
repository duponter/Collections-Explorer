package edu.boardgames.collections.explorer.domain.poll;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.Negative;
import net.jqwik.api.constraints.Positive;

class AnswerTest {
	@Group
	class Construction {
		@Example
		void failsWhenValueIsNull() {
			assertThatThrownBy(() -> new Answer<>(null, 80))
					.isInstanceOf(NullPointerException.class)
					.hasMessage("Answer value should not be null");
		}

		@Property
		void succeedsWhenValueIsNotNull(@ForAll Object value) {
			assertThat(new Answer<>(value, 80)).isNotNull();
		}

		@Property
		void failsWhenVotesAreNegative(@ForAll @Negative int votes) {
			assertThatThrownBy(() -> new Answer<>("value", votes))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(String.format("Votes should be positive or zero, which makes %d invalid", votes));
		}

		@Property
		void succeedsWhenVotesArePositive(@ForAll @Positive int votes) {
			assertThat(new Answer<>("value", votes)).isNotNull();
		}

		@Example
		void succeedsWhenZeroVotes() {
			assertThat(new Answer<>("value", 0)).isNotNull();
		}
	}

	@Group
	class Ratio {
		@Property
		void failsWhenTotalCountIsSmallerThanVotes(@ForAll @Positive int votes) {
			int totalVotes = Arbitraries.integers()
			                            .lessOrEqual(votes - 1)
			                            .sample();

			assertThatThrownBy(() -> new Answer<>("value", votes).ratio(totalVotes))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(String.format("TotalVotes %d should be greater or equal to Votes %d", totalVotes, votes));
		}

		@Property
		void returnsZeroWhenVotesIsZero(@ForAll @Positive int totalVotes) {
			assertThat(new Answer<>("value", 0).ratio(totalVotes)).isZero();
		}

		@Property
		void returnsPositiveResultWhenTotalCountGreaterOrEqualToVotes(@ForAll @Positive int votes) {
			int totalVotes = Arbitraries.integers()
			                            .greaterOrEqual(votes)
			                            .sample();
			assertThat(new Answer<>("value", votes).ratio(totalVotes)).isGreaterThan(0.0d);
		}
	}
}
