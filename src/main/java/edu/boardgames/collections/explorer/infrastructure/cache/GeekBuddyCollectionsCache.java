package edu.boardgames.collections.explorer.infrastructure.cache;

import java.time.Duration;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollectionFilter;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;

public class GeekBuddyCollectionsCache implements GeekBuddyCollections {
    private final Cache<String, BoardGameCollection> collections;
    private final GeekBuddyCollections delegate;

	public GeekBuddyCollectionsCache(GeekBuddyCollections delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.collections = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }

    @Override
    public BoardGameCollection complete(GeekBuddy geekBuddy) {
        return collections.get(GeekBuddyCollectionFilter.NONE.id(geekBuddy), id -> delegate.complete(geekBuddy));
    }

    @Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
        return collections.get(GeekBuddyCollectionFilter.OWNED.id(geekBuddy), id -> delegate.owned(geekBuddy));
	}

    @Override
    public BoardGameCollection preordered(GeekBuddy geekBuddy) {
        return collections.get(GeekBuddyCollectionFilter.PREORDERED.id(geekBuddy), id -> delegate.preordered(geekBuddy));
    }

    @Override
    public BoardGameCollection rated(GeekBuddy geekBuddy) {
        return collections.get(GeekBuddyCollectionFilter.RATED.id(geekBuddy), id -> delegate.rated(geekBuddy));
    }

    @Override
    public BoardGameCollection played(GeekBuddy geekBuddy) {
        return collections.get(GeekBuddyCollectionFilter.PLAYED.id(geekBuddy), id -> delegate.played(geekBuddy));
    }

    @Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
        return collections.get(GeekBuddyCollectionFilter.WANT_TO_PLAY.id(geekBuddy), id -> delegate.wantToPlay(geekBuddy));
	}

	@Override
	public BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating) {
		return delegate.minimallyRated(geekBuddy, minrating);
	}
}
