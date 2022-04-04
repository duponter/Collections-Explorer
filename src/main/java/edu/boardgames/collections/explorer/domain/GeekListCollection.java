package edu.boardgames.collections.explorer.domain;

import java.util.List;

public interface GeekListCollection {
    String id();

    String name();

    List<CollectedBoardGame> boardGames();
}
