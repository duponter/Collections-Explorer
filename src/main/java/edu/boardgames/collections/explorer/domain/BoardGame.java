package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Optional;

import edu.boardgames.collections.explorer.domain.poll.PlayerCountPoll;
import edu.boardgames.collections.explorer.domain.poll.PlayerCountPollResult;
import edu.boardgames.collections.explorer.domain.poll.Poll;

public interface BoardGame extends BoardGameSummary {
	Double bggScore();

	Poll<PlayerCountPollResult> playerCountPoll();

	Range<String> playerCount();

	Integer playerCountTotalVoteCount();

	Optional<PlayerCountPoll> playerCountVotes(int playerCount);

	Optional<Range<String>> bestWithPlayerCount();

	Optional<Range<String>> recommendedWithPlayerCount();

	Range<String> playtime();

	Double averageWeight();

	List<BoardGame> contains();
}
