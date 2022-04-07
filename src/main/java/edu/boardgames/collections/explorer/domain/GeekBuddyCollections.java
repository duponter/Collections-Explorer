package edu.boardgames.collections.explorer.domain;

public interface GeekBuddyCollections {
    BoardGameCollection complete(GeekBuddy geekBuddy);

    BoardGameCollection owned(GeekBuddy geekBuddy);

    BoardGameCollection preordered(GeekBuddy geekBuddy);

    BoardGameCollection wantToPlay(GeekBuddy geekBuddy);

    BoardGameCollection rated(GeekBuddy geekBuddy);

    BoardGameCollection played(GeekBuddy geekBuddy);

    BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating);
}
