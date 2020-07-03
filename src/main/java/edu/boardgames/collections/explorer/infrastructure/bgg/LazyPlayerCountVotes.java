package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.NumberOfPlayers;
import io.vavr.Lazy;

public class LazyPlayerCountVotes implements PlayerCountVotes {
	private final Lazy<NumberOfPlayers> numberOfPlayers;
	private final Lazy<String> poll;
	private final Lazy<Integer> voteCount;

	public LazyPlayerCountVotes(PlayerCountVotes delegate) {
		this.numberOfPlayers = Lazy.of(delegate::numberOfPlayers);
		this.poll = Lazy.of(delegate::poll);
		this.voteCount = Lazy.of(delegate::voteCount);
	}

	@Override
	public NumberOfPlayers numberOfPlayers() {
		return numberOfPlayers.get();
	}

	@Override
	public String poll() {
		return poll.get();
	}

	@Override
	public int voteCount() {
		return voteCount.get();
	}
}
