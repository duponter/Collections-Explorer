package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.NumberOfPlayers;

public interface PlayerCountVotes {
	NumberOfPlayers numberOfPlayers();

	String poll();

	int voteCount();
}
