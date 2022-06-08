package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.TreeSet;

import edu.boardgames.collections.explorer.domain.poll.NumberOfPlayers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberOfPlayersTest {
	@Test
	void throwsExceptionWhenConstructorArgumentIsNull() {
		assertThatThrownBy(() -> NumberOfPlayers.of(null))
				.isInstanceOf(NullPointerException.class);
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = {"  ", "s", "s1"})
	void throwsExceptionWhenConstructorArgumentDoesNotMatchPattern(String input) {
		assertThatThrownBy(() -> NumberOfPlayers.of(input))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(String.format("Input [%s] is not supported as number of players", input));
	}

	@Test
	void throwsExceptionWhenConstructorArgumentHasUnsupportedSuffix() {
		assertThatThrownBy(() -> NumberOfPlayers.of("5-"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Input [5-] is not supported as number of players");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "4", "5+", "10", "111"})
	void constructsSuccessfully(String input) {
		assertThat(NumberOfPlayers.of(input)).isNotNull();
	}

	@Test
	void comparesCountFirstSuffixNext() {
		List<NumberOfPlayers> numberOfPlayers = List.of(
				NumberOfPlayers.of("5"),
				NumberOfPlayers.of("4+"),
				NumberOfPlayers.of("1"),
				NumberOfPlayers.of("5+")
		);

		assertThat(new TreeSet<>(numberOfPlayers)).containsExactly(
				NumberOfPlayers.of("1"),
				NumberOfPlayers.of("4+"),
				NumberOfPlayers.of("5"),
				NumberOfPlayers.of("5+")
		);
	}
}
