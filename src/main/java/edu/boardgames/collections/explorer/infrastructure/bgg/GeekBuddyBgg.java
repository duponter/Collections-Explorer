package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

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
	public List<BoardGame> wantToPlayCollection() {
		return BggInit.get().geekBuddyCollections().wantToPlay(this).boardGames();
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
