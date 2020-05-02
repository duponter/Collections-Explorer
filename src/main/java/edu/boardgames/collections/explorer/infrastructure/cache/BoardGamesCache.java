package edu.boardgames.collections.explorer.infrastructure.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGames;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BoardGamesCache implements BoardGames {
	private final LoadingCache<String, BoardGame> cache;

	public BoardGamesCache(BoardGames delegate) {
		Objects.requireNonNull(delegate);
		this.cache = Caffeine.newBuilder()
				.refreshAfterWrite(Duration.ofMinutes(5))
				.build(new CacheLoader<>() {
					@Override
					public BoardGame load(String s) {
						return delegate.withIds(Stream.of(s)).stream()
								.findFirst()
								.map(LazyBoardGame::new)
								.orElse(null);
					}

					@Override
					public Map<String, BoardGame> loadAll(Iterable<? extends String> keys) {
						Stream<String> ids = StreamSupport.stream(keys.spliterator(), false).map(String.class::cast);
						return delegate.withIds(ids).stream()
								.collect(Collectors.toMap(BoardGame::id, LazyBoardGame::new));
					}
				});
	}

	@Override
	public List<BoardGame> withIds(Stream<String> ids) {
		return List.copyOf(cache.getAll(ids.collect(Collectors.toList())).values());
	}
}
