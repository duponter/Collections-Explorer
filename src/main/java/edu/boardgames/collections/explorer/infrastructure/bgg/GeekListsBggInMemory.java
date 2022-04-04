package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;

import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.GeekLists;

public class GeekListsBggInMemory implements GeekLists {
    @Override
    public List<GeekList> all() {
        return List.of(new GeekListBgg("274761"));   // Borrowed Board Games
    }

    @Override
    public GeekList one(String id) {
        return new GeekListBgg(id);
    }
}
