package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

public class GeekBuddyBgg implements GeekBuddy {
	private final String username;
	private final String name;

	GeekBuddyBgg(String username, String name) {
		this.username = Objects.requireNonNull(username);
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String username() {
		return username;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public List<BoardGame> ownedCollection() {
		return BggInit.get().geekBuddyCollections().owned(this).boardGames();
	}

    @Override
    public List<BoardGame> preorderedCollection() {
        return BggInit.get().geekBuddyCollections().preordered(this).boardGames();
    }

    @Override
    public List<BoardGame> wantToPlayCollection() {
        return BggInit.get().geekBuddyCollections().wantToPlay(this).boardGames();
    }

    @Override
    public List<BoardGame> playedCollection() {
        return BggInit.get().geekBuddyCollections().played(this).boardGames();
    }

    @Override
    public List<BoardGame> ratedCollection() {
        return BggInit.get().geekBuddyCollections().rated(this).boardGames();
    }

	@Override
	public List<BoardGame> ratedCollection(int minrating) {
		return BggInit.get().geekBuddyCollections().minimallyRated(this, minrating).boardGames();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeekBuddyBgg that = (GeekBuddyBgg) o;
		return username.equals(that.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", GeekBuddyBgg.class.getSimpleName() + "[", "]")
				.add(String.format("username='%s'", username))
				.add(String.format("name='%s'", name))
				.toString();
	}
}
