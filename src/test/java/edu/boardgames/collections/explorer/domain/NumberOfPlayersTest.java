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
		assertThatThrownBy(() -> new NumberOfPlayers(null))
				.isInstanceOf(NullPointerException.class);
	}

	@ParameterizedTest
	@EmptySource
	@ValueSource(strings = {"  ", "s", "s1"})
	void throwsExceptionWhenConstructorArgumentDoesNotMatchPattern(String input) {
		assertThatThrownBy(() -> new NumberOfPlayers(input))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage(String.format("Input [%s] is not supported as number of players", input));
	}

	@Test
	void throwsExceptionWhenConstructorArgumentHasUnsupportedSuffix() {
		assertThatThrownBy(() -> new NumberOfPlayers("5-"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Suffix [-] is not supported in number of players");
	}

	@ParameterizedTest
	@ValueSource(strings = {"0", "4", "5+", "10", "111"})
	void constructsSuccessfully(String input) {
		assertThat(new NumberOfPlayers(input)).isNotNull();
	}

	@Test
	void comparesCountFirstSuffixNext() {
		List<NumberOfPlayers> numberOfPlayers = List.of(
				new NumberOfPlayers("5"),
				new NumberOfPlayers("4+"),
				new NumberOfPlayers("1"),
				new NumberOfPlayers("5+")
		);

		assertThat(new TreeSet<>(numberOfPlayers)).containsExactly(
				new NumberOfPlayers("1"),
				new NumberOfPlayers("4+"),
				new NumberOfPlayers("5"),
				new NumberOfPlayers("5+")
		);
	}
}
