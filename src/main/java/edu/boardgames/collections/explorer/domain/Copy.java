package edu.boardgames.collections.explorer.domain;

import java.util.Objects;

public record Copy(BoardGame boardGame, BoardGameCollection collection) {
    public Copy(BoardGame boardGame, BoardGameCollection collection) {
        this.boardGame = Objects.requireNonNull(boardGame);
        this.collection = Objects.requireNonNull(collection);
    }
}
