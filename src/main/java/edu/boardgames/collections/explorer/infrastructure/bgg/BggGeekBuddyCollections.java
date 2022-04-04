package edu.boardgames.collections.explorer.infrastructure.bgg;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.DetailedBoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;

public class BggGeekBuddyCollections implements GeekBuddyCollections {
	@Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
        return new DetailedBoardGameCollection(
                geekBuddy.username() + ".owned",
                "Owned collection of " + geekBuddy.name(),
                newRequestFor(geekBuddy).owned().execute().toList()
        );
    }

    @Override
    public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
        return new DetailedBoardGameCollection(
                geekBuddy.username() + ".wantToPlay",
                "Want to Play collection of " + geekBuddy.name(),
                newRequestFor(geekBuddy).wantToPlay().execute().toList()
        );
    }

    @Override
    public BoardGameCollection rated(GeekBuddy geekBuddy) {
        return new DetailedBoardGameCollection(
                geekBuddy.username() + ".rated",
                "Rated collection of " + geekBuddy.name(),
                newRequestFor(geekBuddy).rated().execute().toList()
        );
    }

    @Override
    public BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating) {
        return new DetailedBoardGameCollection(
                geekBuddy.username() + ".rated" + minrating,
                minrating + " rated collection of " + geekBuddy.name(),
                newRequestFor(geekBuddy).minimallyRated(minrating).execute().toList()
        );
    }

    @Override
    public BoardGameCollection played(GeekBuddy geekBuddy) {
        return new DetailedBoardGameCollection(
                geekBuddy.username() + ".played",
                "Played collection of " + geekBuddy.name(),
                newRequestFor(geekBuddy).played().execute().toList()
        );
    }

    @Override
    public BoardGameCollection preordered(GeekBuddy geekBuddy) {
        return new DetailedBoardGameCollection(
                geekBuddy.username() + ".preordered",
                "Preordered collection of " + geekBuddy.name(),
                newRequestFor(geekBuddy).preordered().execute().toList()
        );
    }

    private CollectionEndpoint newRequestFor(GeekBuddy geekBuddy) {
        return new CollectionEndpoint(geekBuddy.username()).withStats().withoutExpansions();
    }
}
