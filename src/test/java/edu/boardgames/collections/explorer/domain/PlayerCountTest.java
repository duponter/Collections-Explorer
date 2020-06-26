package edu.boardgames.collections.explorer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PlayerCountTest {
	@Nested
	class Construction {
		@Test
		void failsWhenNegativeCount() {
			assertThatThrownBy(() -> new PlayerCount(-1))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("RecommendedPlayerCount requires a positive number: -1 is not greater than 0");
		}

		@Test
		void failsWhenZero() {
			assertThatThrownBy(() -> new PlayerCount(0))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("RecommendedPlayerCount requires a positive number: 0 is not greater than 0");
		}

		@Test
		void succeedsWhenPositive() {
			assertThat(new PlayerCount(10)).isNotNull();
		}
	}
}
