package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.CollectedBoardGame;

public record CollectedBoardGameCopy(String collection, CollectedBoardGame delegate) implements CollectedBoardGame {
    @Override
    public String id() {
        return this.delegate.id();
    }

    @Override
    public String name() {
        return this.delegate.name();
    }

    @Override
    public String year() {
        return this.delegate.year();
    }

    @Override
    public String originalName() {
        return this.delegate.originalName();
    }

    @Override
    public Integer rating() {
        return this.delegate.rating();
    }

    @Override
    public boolean owned() {
        return this.delegate.owned();
    }

    @Override
    public boolean previouslyOwned() {
        return this.delegate.previouslyOwned();
    }

    @Override
    public boolean forTrade() {
        return this.delegate.forTrade();
    }

    @Override
    public boolean wanted() {
        return this.delegate.wanted();
    }

    @Override
    public boolean wantToPlay() {
        return this.delegate.wantToPlay();
    }

    @Override
    public boolean wantToBuy() {
        return this.delegate.wantToBuy();
    }

    @Override
    public boolean wishlisted() {
        return this.delegate.wishlisted();
    }

    @Override
    public boolean preordered() {
        return this.delegate.preordered();
    }

    @Override
    public Integer numberOfPlays() {
        return this.delegate.numberOfPlays();
    }

    @Override
    public String publicComment() {
        return this.delegate.publicComment();
    }
}
