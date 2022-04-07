package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.Objects;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekList;

public record GeekListBgg(String id) implements GeekList {
    public GeekListBgg(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public BoardGameCollection asCollection() {
        return BggInit.get().geekListCollections().resolved(this);
    }
}
