package edu.boardgames.collections.explorer.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.BoardGameCollections;
import edu.boardgames.collections.explorer.domain.GeekBuddy;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class CollectionsCache implements BoardGameCollections {
	private final LoadingCache<GeekBuddy, BoardGameCollection> ownedCollections;
	private final LoadingCache<GeekBuddy, BoardGameCollection> wantToPlayCollections;

	public CollectionsCache(BoardGameCollections delegate) {
		Objects.requireNonNull(delegate);
		this.ownedCollections = Caffeine.newBuilder()
				.refreshAfterWrite(Duration.ofMinutes(5))
				.build(delegate::owned);
		this.wantToPlayCollections = Caffeine.newBuilder()
				.refreshAfterWrite(Duration.ofMinutes(5))
				.build(delegate::wantToPlay);
	}

	@Override
	public BoardGameCollection owned(GeekBuddy geekBuddy) {
		return Objects.requireNonNullElseGet(ownedCollections.get(geekBuddy), () -> emptyCollection(geekBuddy));
	}

	@Override
	public BoardGameCollection wantToPlay(GeekBuddy geekBuddy) {
		return Objects.requireNonNullElseGet(wantToPlayCollections.get(geekBuddy), () -> emptyCollection(geekBuddy));
	}

	private BoardGameCollection emptyCollection(GeekBuddy geekBuddy) {
		return new BoardGameCollection(geekBuddy, List.of());
	}
}
