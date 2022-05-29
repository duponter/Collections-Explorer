package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Objects;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

public record GeekBuddyBgg(String username, String name, String lastName) implements GeekBuddy {
    public GeekBuddyBgg(String username, String name, String lastName) {
        this.username = Objects.requireNonNull(username);
        this.name = Objects.requireNonNull(name);
        this.lastName = lastName;
    }

    public GeekBuddyBgg(String username, String name) {
        this(username, name, null);
    }

    @Override
    public BoardGameCollection ownedCollection() {
        return BggInit.get().geekBuddyCollections().owned(this);
    }

    @Override
    public BoardGameCollection preorderedCollection() {
        return BggInit.get().geekBuddyCollections().preordered(this);
    }

    @Override
    public BoardGameCollection wantToPlayCollection() {
        return BggInit.get().geekBuddyCollections().wantToPlay(this);
    }

    @Override
    public BoardGameCollection playedCollection() {
        return BggInit.get().geekBuddyCollections().played(this);
    }

    @Override
    public BoardGameCollection ratedCollection() {
        return BggInit.get().geekBuddyCollections().rated(this);
    }

    @Override
    public BoardGameCollection ratedCollection(int minrating) {
        return BggInit.get().geekBuddyCollections().minimallyRated(this, minrating);
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
}
