package edu.boardgames.collections.explorer.domain.poll;

public interface PlayerCountVotes {
	NumberOfPlayers numberOfPlayers();

	String poll();

	int voteCount();
}
