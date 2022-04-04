package edu.boardgames.collections.explorer.domain;

import java.util.Arrays;
import java.util.List;

public interface GeekLists {
    List<GeekList> all();

    GeekList one(String id);

    default List<GeekList> withId(String... ids) {
        return Arrays.stream(ids)
                .map(this::one)
                .toList();
    }
}
