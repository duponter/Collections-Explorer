package edu.boardgames.collections.explorer.domain;

import java.util.List;
import java.util.Optional;

import edu.boardgames.collections.explorer.infrastructure.bgg.PlayerCountPoll;

public interface BoardGame {
	String id();

	String name();

	String year();

	Double bggScore();

	Range<String> playerCount();

	Integer playerCountTotalVoteCount();

	Optional<PlayerCountPoll> playerCountVotes(int playerCount);

	Optional<Range<String>> bestWithPlayerCount();

	Optional<Range<String>> recommendedWithPlayerCount();

	Range<String> playtime();

	Double averageWeight();

	List<BoardGame> contains();
}
