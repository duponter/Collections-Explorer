package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.DetailedBoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.GeekListCollection;
import edu.boardgames.collections.explorer.domain.GeekListCollections;

public class BggGeekListCollections implements GeekListCollections {
    @Override
    public BoardGameCollection resolved(GeekList geekList) {
        GeekListCollection geekListCollection = new GeekListEndpoint(geekList.id()).execute();
        return new DetailedBoardGameCollection(
                geekList.id(),
                geekListCollection.name(),
                geekListCollection.boardGames()
        );
    }
}
