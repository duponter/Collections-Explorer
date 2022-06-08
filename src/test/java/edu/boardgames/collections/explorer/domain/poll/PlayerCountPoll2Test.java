package edu.boardgames.collections.explorer.domain.poll;

import edu.boardgames.collections.explorer.domain.Range;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerCountPoll2Test {
    @Nested
    class Construction {
        @Test
        void failsWhenPollIsNull() {
            assertThatThrownBy(() -> new PlayerCountPoll2(null))
                .isExactlyInstanceOf(NullPointerException.class);
        }

        @Test
        void succeedsWhenPollIsNotNull() {
            assertThat(new PlayerCountPoll2(poll())).isNotNull();
        }
    }

    @Test
    void bestOnlyReturnsTheMostFavorablePlayerCounts() {
        PlayerCountPoll2 poll = new PlayerCountPoll2(poll());
        assertThat(poll.bestOnly()).contains(new Range<>("3", "3"));
    }

    @Test
    void recommendedReturnsRecommendedPlayerCounts() {
        PlayerCountPoll2 poll = new PlayerCountPoll2(poll());
        assertThat(poll.recommended()).contains(new Range<>("2", "4"));
    }

    private Poll<PlayerCountPollResult> poll() {
        return new Poll<>() {
            @Override
            public String name() {
                return null;
            }

            @Override
            public String title() {
                return null;
            }

            @Override
            public int totalVotes() {
                return 176;
            }

            @Override
            public ImmutableList<PlayerCountPollResult> results() {
                return Lists.immutable.of(
                    new PollResult("1", "Best", 0),
                    new PollResult("1", "Recommended", 10),
                    new PollResult("1", "Not Recommended", 72),
                    new PollResult("2", "Best", 26),
                    new PollResult("2", "Recommended", 81),
                    new PollResult("2", "Not Recommended", 25),
                    new PollResult("3", "Best", 115),
                    new PollResult("3", "Recommended", 34),
                    new PollResult("3", "Not Recommended", 2),
                    new PollResult("4", "Best", 34),
                    new PollResult("4", "Recommended", 84),
                    new PollResult("4", "Not Recommended", 4),
                    new PollResult("5", "Best", 13),
                    new PollResult("5", "Recommended", 53),
                    new PollResult("5", "Not Recommended", 24),
                    new PollResult("5+", "Best", 0),
                    new PollResult("5+", "Recommended", 0),
                    new PollResult("5+", "Not Recommended", 58)
                );
            }
        };
    }

    private record PollResult(String numberOfPlayers, String value, int votes) implements PlayerCountPollResult {
    }
}
