package edu.boardgames.collections.explorer.infrastructure.cache;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.DetailedBoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;

public class GeekBuddyCollectionsCache implements GeekBuddyCollections {
	private final LoadingCache<GeekBuddy, BoardGameCollection> ownedCollections;
	private final LoadingCache<GeekBuddy, BoardGameCollection> wantToPlayCollections;
	private final GeekBuddyCollections delegate;

	public GeekBuddyCollectionsCache(GeekBuddyCollections delegate) {
		this.delegate = Objects.requireNonNull(delegate);
		this.ownedCollections = Caffeine.newBuilder()
				.refreshAfterWrite(Duration.ofMinutes(5))
				.build(delegate::owned);
		this.wantToPlayCollections = Caffeine.newBuilder()
				.refreshAfterWrite(Duration.ofMinutes(5))
				.build(delegate::wantToPlay);
	}

    @Override
    public BoardGameCollection complete(GeekBuddy geekBuddy) {
        return delegate.complete(geekBuddy);
    }

    @Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
		return Objects.requireNonNullElseGet(ownedCollections.get(geekBuddy), () -> emptyCollection(geekBuddy));
	}

    @Override
    public BoardGameCollection preordered(GeekBuddy geekBuddy) {
        return delegate.preordered(geekBuddy);
    }

    @Override
    public BoardGameCollection rated(GeekBuddy geekBuddy) {
        return delegate.rated(geekBuddy);
    }

    @Override
    public BoardGameCollection played(GeekBuddy geekBuddy) {
        return delegate.played(geekBuddy);
    }

    @Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
		return Objects.requireNonNullElseGet(wantToPlayCollections.get(geekBuddy), () -> emptyCollection(geekBuddy));
	}

	@Override
	public BoardGameCollection minimallyRated(GeekBuddy geekBuddy, int minrating) {
		return delegate.minimallyRated(geekBuddy, minrating);
	}

	private BoardGameCollection emptyCollection(GeekBuddy geekBuddy) {
		return new DetailedBoardGameCollection(geekBuddy.username(), geekBuddy.name(), List.of());
	}
}
