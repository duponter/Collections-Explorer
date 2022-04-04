package edu.boardgames.collections.explorer.domain;

public interface GeekBuddy {
    String username();

    String name();

    BoardGameCollection ownedCollection();

    BoardGameCollection preorderedCollection();

    BoardGameCollection wantToPlayCollection();

    BoardGameCollection playedCollection();

    BoardGameCollection ratedCollection();

    BoardGameCollection ratedCollection(int rating);
}
