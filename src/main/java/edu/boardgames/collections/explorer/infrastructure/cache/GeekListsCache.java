package edu.boardgames.collections.explorer.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import edu.boardgames.collections.explorer.domain.GeekList;
import edu.boardgames.collections.explorer.domain.GeekLists;

import java.time.Duration;
import java.util.Objects;

public class GeekListsCache implements GeekLists {
	private final LoadingCache<String, GeekList> cache;

	public GeekListsCache(GeekLists delegate) {
		Objects.requireNonNull(delegate);
		this.cache = Caffeine.newBuilder()
				.refreshAfterWrite(Duration.ofMinutes(5))
				.build(delegate::withId);
	}

	@Override
	public GeekList withId(String id) {
		return Objects.requireNonNull(cache.get(id));
	}
}
