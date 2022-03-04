package edu.boardgames.collections.explorer.infrastructure.bgg;

import java.util.function.UnaryOperator;

import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGames;
import edu.boardgames.collections.explorer.domain.CollectedBoardGame;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;

public class BggGeekBuddyCollections implements GeekBuddyCollections {
	private final BoardGames boardGames;

	BggGeekBuddyCollections(BoardGames boardGames) {
		this.boardGames = boardGames;
	}

	@Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, CollectionEndpoint::owned);
	}

	@Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, CollectionEndpoint::wantToPlay);
	}

	@Override
	public BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating) {
		return this.requestCollection(geekBuddy, request -> request.minimallyRated(minrating));
	}

    @Override
	public BoardGameCollection rated(GeekBuddy geekBuddy) {
		return this.requestCollection(geekBuddy, CollectionEndpoint::rated);
	}

    @Override
	public BoardGameCollection played(GeekBuddy geekBuddy) {
        return this.requestCollection(geekBuddy, CollectionEndpoint::played);
	}

    @Override
	public BoardGameCollection preordered(GeekBuddy geekBuddy) {
        return this.requestCollection(geekBuddy, CollectionEndpoint::preordered);
	}

	private BoardGameCollection requestCollection(GeekBuddy geekBuddy, UnaryOperator<CollectionEndpoint> processor) {
		CollectionEndpoint collectionRequest = processor.apply(new CollectionEndpoint(geekBuddy.username()).abbreviatedResults().withoutExpansions());
        return new GeekBuddyCollection(geekBuddy, boardGames.withIds(collectionRequest.execute().map(CollectedBoardGame::id)));
	}
}
