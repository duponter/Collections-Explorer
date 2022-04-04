package edu.boardgames.collections.explorer.domain;

import java.util.Arrays;
import java.util.List;

public interface GeekBuddies {
    List<GeekBuddy> all();

    GeekBuddy one(String username);

    default List<GeekBuddy> withUsername(String... usernames) {
        return Arrays.stream(usernames)
                .map(this::one)
                .toList();
    }
}
