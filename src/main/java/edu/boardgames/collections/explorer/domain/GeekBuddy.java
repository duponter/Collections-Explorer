package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekBuddy {
    String username();

    String name();

    List<BoardGame> ownedCollection();

    List<BoardGame> preorderedCollection();

    List<BoardGame> wantToPlayCollection();

    List<BoardGame> playedCollection();

    List<BoardGame> ratedCollection();

    List<BoardGame> ratedCollection(int rating);
}
