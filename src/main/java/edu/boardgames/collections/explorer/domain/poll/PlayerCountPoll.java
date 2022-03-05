package edu.boardgames.collections.explorer.domain.poll;

import java.util.Comparator;
import java.util.Optional;
import java.util.StringJoiner;

import edu.boardgames.collections.explorer.domain.Range;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;

public class PlayerCountPoll {
    /**
     * List<PlayerCountPollResult(int numplayers, String value, int numvotes)>
     */
    public static final String XML_INPUT = """
            <poll name="suggested_numplayers" title="User Suggested Number of Players" totalvotes="67">
                <results numplayers="1">
                    <result value="Best" numvotes="13"/>
                    <result value="Recommended" numvotes="23"/>
                    <result value="Not Recommended" numvotes="5"/>
                </results>
                <results numplayers="2">
                    <result value="Best" numvotes="30"/>
                    <result value="Recommended" numvotes="21"/>
                    <result value="Not Recommended" numvotes="2"/>
                </results>
                <results numplayers="3">
                    <result value="Best" numvotes="17"/>
                    <result value="Recommended" numvotes="29"/>
                    <result value="Not Recommended" numvotes="1"/>
                </results>
                <results numplayers="4">
                    <result value="Best" numvotes="15"/>
                    <result value="Recommended" numvotes="12"/>
                    <result value="Not Recommended" numvotes="17"/>
                </results>
                <results numplayers="4+">
                    <result value="Best" numvotes="0"/>
                    <result value="Recommended" numvotes="0"/>
                    <result value="Not Recommended" numvotes="20"/>
                </results>
            </poll>
            """;

    private final int bestVotes;
    private final int recommendedVotes;
    private final int notRecommendedVotes;

    public PlayerCountPoll(RichIterable<? extends PlayerCountVotes> votes) {
        if (votes.size() != 3) {
            throw new IllegalArgumentException("Expecting only 3 vote elements: %s".formatted(String.join(", ", votes.collect(PlayerCountVotes::toString))));
        }
        MutableList<? extends PlayerCountVotes> sorted = votes.toSortedList(Comparator.comparing(PlayerCountVotes::poll));
        this.bestVotes = sorted.getFirst().voteCount();
        this.recommendedVotes = sorted.getLast().voteCount();
        this.notRecommendedVotes = sorted.get(1).voteCount();
    }

    public int bestVotes() {
        return bestVotes;
    }

    public int recommendedVotes() {
        return recommendedVotes;
    }

    public int notRecommendedVotes() {
        return notRecommendedVotes;
    }

    public Optional<Range<String>> pollResult() {
        /*
        1. PlayerCountPollChoice.BEST
            filter value="Best" + numvotes > 5 - return first numvotes desc
        2. PlayerCountPollChoice.RECOMMENDED
            exclude value="Not Recommended" - filter numvotes > 5 - group by numplayers having sum(numvotes)
        *. select also ones with score -20%? (for Range?)

         */
        return Optional.empty();
    }

    public PlayerCountPollChoice result() {
        if (bestVotes >= 5 && bestVotes >= recommendedVotes && bestVotes >= notRecommendedVotes) {
            return PlayerCountPollChoice.BEST;
        } else if (recommendedVotes >= 5 && recommendedVotes >= notRecommendedVotes) {
            return PlayerCountPollChoice.RECOMMENDED;
        } else {
            return PlayerCountPollChoice.NOT_RECOMMENDED;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PlayerCountPoll.class.getSimpleName() + "[", "]")
                .add(String.format("bestVotes=%s", bestVotes))
                .add(String.format("recommendedVotes=%s", recommendedVotes))
                .add(String.format("notRecommendedVotes=%s", notRecommendedVotes))
                .add(String.format("result=%s", result()))
                .toString();
    }
}
