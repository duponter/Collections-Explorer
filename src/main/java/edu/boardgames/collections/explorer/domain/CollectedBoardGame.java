package edu.boardgames.collections.explorer.domain;

public interface CollectedBoardGame extends BoardGameSummary {
    String collection();

    String originalName();

    Integer rating();

    default boolean rated() {
        return rating() > 0;
    }

    boolean owned();

    boolean previouslyOwned();

    boolean forTrade();

    boolean wanted();

    boolean wantToPlay();

    boolean wantToBuy();

    boolean wishlisted();

    boolean preordered();

    default boolean played() {
        return numberOfPlays() > 0;
    }

    Integer numberOfPlays();

    String publicComment();
}

