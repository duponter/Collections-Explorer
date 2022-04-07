package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.List;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.DetailedBoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollectionFilter;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.impl.block.factory.Predicates2;

public class BggGeekBuddyCollections implements GeekBuddyCollections {
    @Override
    public BoardGameCollection complete(GeekBuddy geekBuddy) {
        return this.asCollection(geekBuddy, GeekBuddyCollectionFilter.NONE);
    }

    @Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
        return this.asCollection(geekBuddy, GeekBuddyCollectionFilter.OWNED);
    }

    @Override
    public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
        return this.asCollection(geekBuddy, GeekBuddyCollectionFilter.WANT_TO_PLAY);
    }

    @Override
    public BoardGameCollection rated(GeekBuddy geekBuddy) {
        return this.asCollection(geekBuddy, GeekBuddyCollectionFilter.RATED);
    }

    @Override
    public BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating) {
        return new DetailedBoardGameCollection(
                GeekBuddyCollectionFilter.RATED.id(geekBuddy) + minrating,
                GeekBuddyCollectionFilter.RATED.name(geekBuddy).replace("]", " " + minrating + "]"),
                Lists.immutable.withAll(fetch(geekBuddy, GeekBuddyCollectionFilter.RATED))
                        .selectWith(Predicates2.attributeGreaterThanOrEqualTo(CollectedBoardGame::rating), minrating)
                        .toList()
        );
    }

    @Override
    public BoardGameCollection played(GeekBuddy geekBuddy) {
        return this.asCollection(geekBuddy, GeekBuddyCollectionFilter.PLAYED);
    }

    @Override
    public BoardGameCollection preordered(GeekBuddy geekBuddy) {
        return this.asCollection(geekBuddy, GeekBuddyCollectionFilter.PREORDERED);
    }

    private BoardGameCollection asCollection(GeekBuddy geekBuddy, GeekBuddyCollectionFilter filter) {
        return new DetailedBoardGameCollection(filter.id(geekBuddy), filter.name(geekBuddy), fetch(geekBuddy, filter));
    }

    private List<CollectedBoardGame> fetch(GeekBuddy geekBuddy, GeekBuddyCollectionFilter filter) {
        CollectionEndpoint endpoint = new CollectionEndpoint(geekBuddy.username()).withStats().withoutExpansions();
        return (switch (filter) {
            case NONE -> endpoint;
            case OWNED -> endpoint.owned();
            case PREORDERED -> endpoint.preordered();
            case WANT_TO_PLAY -> endpoint.wantToPlay();
            case RATED -> endpoint.rated();
            case PLAYED -> endpoint.played();
        }).execute().toList();
    }
}
