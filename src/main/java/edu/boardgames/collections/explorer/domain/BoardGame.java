package edu.boardgames.collections.explorer.domain;

import java.util.Optional;

public interface BoardGame {
	String name();

	String year();

	Double bggScore();

	Range<String> playerCount();

	Optional<Range<Integer>> bestWithPlayerCount();

	Optional<Range<Integer>> recommendedWithPlayerCount();

	Range<String> playtime();

	Double weight();
}
