package edu.boardgames.collections.explorer.infrastructure.cache;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.BoardGame;
import edu.boardgames.collections.explorer.domain.BoardGames;

//TODO_EDU CacheWriter? https://github.com/ben-manes/caffeine/issues/274
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
					public Map<? extends String, ? extends BoardGame> loadAll(Set<? extends String> keys) throws Exception {
						Stream<String> ids = keys.stream().map(String.class::cast);
						return delegate.withIds(ids).stream()
								.collect(Collectors.toMap(BoardGame::id, LazyBoardGame::new));
					}
				});
	}

	@Override
	public List<BoardGame> withIds(Stream<String> ids) {
		return List.copyOf(cache.getAll(ids.toList()).values());
	}
}
