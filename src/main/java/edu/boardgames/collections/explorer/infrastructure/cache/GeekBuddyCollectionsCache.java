package edu.boardgames.collections.explorer.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.BoardGameCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddy;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollection;
import edu.boardgames.collections.explorer.domain.GeekBuddyCollections;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class GeekBuddyCollectionsCache implements GeekBuddyCollections {
	private final LoadingCache<GeekBuddy, BoardGameCollection> ownedCollections;
	private final LoadingCache<GeekBuddy, BoardGameCollection> wantToPlayCollections;

	public GeekBuddyCollectionsCache(GeekBuddyCollections delegate) {
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
		return new GeekBuddyCollection(geekBuddy, List.of());
	}
}
